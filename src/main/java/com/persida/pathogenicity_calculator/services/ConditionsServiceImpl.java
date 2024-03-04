package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.ConditionsTermAndIdDTO;
import com.persida.pathogenicity_calculator.repository.ConditionRepository;
import com.persida.pathogenicity_calculator.repository.jpa.ConditionTermIdJPA;
import com.persida.pathogenicity_calculator.utils.HTTPSConnector;
import com.persida.pathogenicity_calculator.utils.StackTracePrinter;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ConditionsServiceImpl implements ConditionsService{
    static Logger logger = Logger.getLogger(ConditionsServiceImpl.class);

    @Value("${numOfConditionsPerPage}")
    private Integer numOfConditionsPerPage;

    @Value("${conditionsInfoListURL}")
    private String conditionsInfoListURL;

    @Autowired
    private ConditionRepository conditionRepository;

    private JSONParser jsonParser;

    @Override
    public List<ConditionsTermAndIdDTO> getConditionsLike(String partialCAID){
        List<ConditionsTermAndIdDTO> conditionsTermAndIdDTO = new ArrayList<ConditionsTermAndIdDTO>();
        List<ConditionTermIdJPA> conJpaList =  conditionRepository.getConditionTermsLike(partialCAID);
        if(conJpaList == null || conJpaList.size() == 0){
            return null;
        }
        for(ConditionTermIdJPA cJPA : conJpaList){
            conditionsTermAndIdDTO.add(new ConditionsTermAndIdDTO(cJPA.getConditionId(), cJPA.getTerm()));
        }
        return conditionsTermAndIdDTO;
    }

    @Override
    public ArrayList<ConditionsTermAndIdDTO> getConditionsInfoByCall(){
        ArrayList<JSONObject> competeReposneList = null;
        String  conditionsListResponse = null;
        int pageNum = 1;
        int iterLimit = 150;
        int totalNumOfConditionsFromResponse = 0;
        logger.info("Getting response from Conditions API, "+numOfConditionsPerPage+" per page!");
        mainLoop:
        while(true){
            conditionsListResponse = getConditionsInfoList("&pg=" + pageNum, "&pgSize="+numOfConditionsPerPage);
            if(conditionsListResponse != null && !conditionsListResponse.equals("")){
                JSONArray dataArray = null;
                try {
                    if(jsonParser == null){
                        jsonParser = new JSONParser();
                    }
                    JSONObject obj = (JSONObject) jsonParser.parse(conditionsListResponse);
                    dataArray = (JSONArray) obj.get("data");
                    if(dataArray != null && dataArray.size() > 0){
                        if(dataArray.size() < numOfConditionsPerPage){
                            pageNum = 100; //first measure to make sure that the loop stops
                        }
                        if(competeReposneList == null){
                            competeReposneList = new ArrayList<JSONObject>();
                        }
                        for(Object dataObj : dataArray){
                            competeReposneList.add((JSONObject) dataObj);
                        }
                        totalNumOfConditionsFromResponse += dataArray.size();

                        if(dataArray.size() < numOfConditionsPerPage){
                            break mainLoop; //second measure to make sure that the loop stops
                        }
                    }
                }catch(Exception e){
                    logger.error(StackTracePrinter.printStackTrace(e));
                }
            }
            pageNum++;
            if(pageNum == iterLimit){
                //something is wrong at this point, we estimate slightly more than 20.000 diseases, around 80-90 iterations !
                logger.error("The conditions loop has iterated "+iterLimit+" times!");
                break mainLoop;
            }
        }

        if(competeReposneList == null){
            return null;
        }
        logger.info("Finished collecting Conditions from responses, gathered total num: "+totalNumOfConditionsFromResponse);

        ConditionsTermAndIdDTO condTermAndIdDTO = null;
        ArrayList<ConditionsTermAndIdDTO> conditionsInfoList = new ArrayList<ConditionsTermAndIdDTO>();
        for(JSONObject jsonObj : competeReposneList){
            JSONObject entContentObj = (JSONObject) jsonObj.get("entContent");
            if(entContentObj == null){
                continue;
            }
            String conditionId = String.valueOf(jsonObj.get("entId"));
            String term = String.valueOf(((JSONObject) entContentObj.get("MONDO")).get("lbl"));

            condTermAndIdDTO = new ConditionsTermAndIdDTO(conditionId, term);
            conditionsInfoList.add(condTermAndIdDTO);
        }

        logger.info("Total num of Conditions after processing: "+conditionsInfoList.size()+", rejected: "+(totalNumOfConditionsFromResponse-conditionsInfoList.size()));
        return conditionsInfoList;
    }

    private String getConditionsInfoList(String pageNumberParam, String pageSizeParam){
        HashMap<String,String> httpProperties = new HashMap<String,String>();
        httpProperties.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_APP_JSON);

        String conditionsInfoListWithPgNumURL =  conditionsInfoListURL + pageNumberParam + pageSizeParam;

        HTTPSConnector https = new HTTPSConnector();
        String response = https.sendHttpsRequest(conditionsInfoListWithPgNumURL, Constants.HTTP_GET, null, httpProperties);
        return response;
    }
}
