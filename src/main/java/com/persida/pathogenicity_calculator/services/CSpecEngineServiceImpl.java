package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.CSpecEngineDTO;
import com.persida.pathogenicity_calculator.dto.EngineRelatedGene;
import com.persida.pathogenicity_calculator.utils.HTTPSConnector;
import com.persida.pathogenicity_calculator.utils.StackTracePrinter;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class CSpecEngineServiceImpl implements CSpecEngineService{
    static Logger logger = Logger.getLogger(CSpecEngineServiceImpl.class);

    @Value("${listOfAllCSpecEngines}")
    private String listOfAllCSpecEngines;

    @Value("${cSpecEngineInfoNoIdURL}")
    private String cSpecEngineInfoNoIdURL;

    private JSONParser jsonParser;

    @Override
    public ArrayList<CSpecEngineDTO> getCSpecEnginesInfoByCall(){
        logger.info("Getting data from CSpecEngines!");
        String  enginesListResponse = getListOfSpecEngines();
        if(enginesListResponse == null || enginesListResponse.equals("")){
            return null;
        }

        ArrayList<CSpecEngineDTO> cSpecEngineDTOList = new ArrayList<CSpecEngineDTO>();
        try {
            if(jsonParser == null){
                jsonParser = new JSONParser();
            }
            JSONObject obj = (JSONObject) jsonParser.parse(enginesListResponse);
            JSONArray dataArray = (JSONArray) obj.get("data");
            if(dataArray == null || dataArray.size() == 0){
                return null;
            }

            CSpecEngineDTO cSpecEngineDTO = null;
            URI uri = null;
            for(Object dataObj : dataArray){
                JSONObject cspecEngineObj = (JSONObject) dataObj;
                JSONObject entContentObj = (JSONObject) cspecEngineObj.get("entContent");
                if(entContentObj == null){
                    continue;
                }
                if(entContentObj.get("legacyReplaced") != null && Boolean.valueOf(String.valueOf(entContentObj.get("legacyReplaced")))){
                    continue;
                }
                if(entContentObj.get("legacyFullySuperseded") != null && !Boolean.valueOf(String.valueOf(entContentObj.get("legacyFullySuperseded")))){
                    continue;
                }

                String engineId = String.valueOf(cspecEngineObj.get("entId"));
                String engineInfoResponse = getcSpecEngineRelatedInfo(engineId);
                if(engineInfoResponse == null){
                    continue;
                }

                JSONObject engineInfObj = (JSONObject) jsonParser.parse(engineInfoResponse);
                String engineSummary = String.valueOf(engineInfObj.get("label"));

                JSONObject affiliationObj = (JSONObject) engineInfObj.get("affiliation");
                String organizationName = String.valueOf(affiliationObj.get("label"));

                JSONArray ruleSetsObj = (JSONArray)  engineInfObj.get("ruleSets");
                JSONObject ruleSetObj = (JSONObject) ruleSetsObj.get(0);
                String ruleSetURL = String.valueOf(ruleSetObj.get("@id"));

                uri = new URI(ruleSetURL);
                String[] segments = uri.getPath().split("/");
                String idStr = segments[segments.length-1];
                int rulseSetId = Integer.parseInt(idStr);

                cSpecEngineDTO = new CSpecEngineDTO(engineId, engineSummary, organizationName, rulseSetId, ruleSetURL, null);

                EngineRelatedGene engineRelatedGene = null;
                JSONArray genes = (JSONArray) ruleSetObj.get("genes");
                if(genes != null){
                    for(Object gene : genes){
                        JSONObject geneObj = (JSONObject) gene;
                        String geneName = String.valueOf(geneObj.get("label"));

                        engineRelatedGene = new EngineRelatedGene(geneName);

                        JSONArray diseases = (JSONArray) geneObj.get("diseases");
                        if(diseases != null && diseases.size() != 0){
                            for(Object disease : diseases){
                                JSONObject diseaseObj = (JSONObject) disease;
                                String diseaseMongoId = String.valueOf(diseaseObj.get("label"));
                                engineRelatedGene.addDiseases(diseaseMongoId);
                            }
                        }
                        cSpecEngineDTO.addGenes(engineRelatedGene);
                    }
                }

                cSpecEngineDTOList.add(cSpecEngineDTO);
            }
        }catch(Exception e){
            logger.error(StackTracePrinter.printStackTrace(e));
        }
        logger.info("Received info on "+cSpecEngineDTOList.size()+" valid (non legacy) CSpecEgines!");
        return cSpecEngineDTOList;
    }

    private String getListOfSpecEngines(){
        HashMap<String,String> httpProperties = new HashMap<String,String>();
        httpProperties.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_APP_JSON);

        HTTPSConnector https = new HTTPSConnector();
        String response = https.sendHttpsRequest(listOfAllCSpecEngines, Constants.HTTP_GET, null, httpProperties);
        return response;
    }

    private String getcSpecEngineRelatedInfo(String cSpecEngineEntID){
        HashMap<String,String> httpProperties = new HashMap<String,String>();
        httpProperties.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_APP_JSON);

        String cSpecEngineInfoWithIdURL = cSpecEngineInfoNoIdURL + cSpecEngineEntID;

        HTTPSConnector https = new HTTPSConnector();
        String response = https.sendHttpsRequest(cSpecEngineInfoWithIdURL, Constants.HTTP_GET, null, httpProperties);
        return response;
    }
}
