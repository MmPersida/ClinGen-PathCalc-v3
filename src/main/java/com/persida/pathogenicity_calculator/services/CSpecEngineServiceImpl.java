package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.*;
import com.persida.pathogenicity_calculator.repository.CSpecRuleSetRepository;
import com.persida.pathogenicity_calculator.repository.ConditionRepository;
import com.persida.pathogenicity_calculator.repository.entity.CSpecRuleSet;
import com.persida.pathogenicity_calculator.repository.entity.Condition;
import com.persida.pathogenicity_calculator.repository.entity.Gene;
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

import java.net.URI;
import java.util.*;

@Service
public class CSpecEngineServiceImpl implements CSpecEngineService{
    static Logger logger = Logger.getLogger(CSpecEngineServiceImpl.class);

    @Value("${listOfAllCSpecEngines}")
    private String listOfAllCSpecEngines;

    @Value("${cSpecEngineInfoNoIdURL}")
    private String cSpecEngineInfoNoIdURL;

    @Value("${cspecRuleSetNoIdUrl}")
    private String cspecRuleSetNoIdUrl;

    @Value("${cspecAssertionsURL}")
    private String cspecAssertionsURL;

    @Autowired
    private CSpecRuleSetRepository cSpecRuleSetRepository;
    @Autowired
    private ConditionRepository conditionRepository;

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
            mainLoop:
            for(Object dataObj : dataArray){
                JSONObject cspecEngineObj = (JSONObject) dataObj;
                JSONObject entContentObj = (JSONObject) cspecEngineObj.get("entContent");
                if(entContentObj == null){
                    continue mainLoop;
                }

                //engine has never been approved if the entContent.states has NO items where name === 'Released'
                boolean isReleased = false;
                if(entContentObj.get("states") != null){
                    JSONArray statesArray = (JSONArray) entContentObj.get("states");
                    if(statesArray != null && statesArray.size() > 0){
                        stateLoop:
                        for(Object stateObj : statesArray){
                            JSONObject stateJsonObj = (JSONObject) stateObj;
                            String stateName = String.valueOf(stateJsonObj.get("name"));
                            if(stateName.equals("Released")){
                                isReleased = true;
                                break stateLoop;
                            }
                        }
                    }
                }else{
                    isReleased = true;
                }

                if(!isReleased){
                    continue mainLoop;
                }
                if(entContentObj.get("legacyReplaced") != null && Boolean.valueOf(String.valueOf(entContentObj.get("legacyReplaced")))){
                    continue mainLoop;
                }
                if(entContentObj.get("legacyFullySuperseded") != null && !Boolean.valueOf(String.valueOf(entContentObj.get("legacyFullySuperseded")))){
                    continue mainLoop;
                }

                String engineId = String.valueOf(cspecEngineObj.get("entId"));
                String engineInfoResponse = getcSpecEngineRelatedInfo(engineId);
                if(engineInfoResponse == null){
                    continue mainLoop;
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

                String ruleSetJSONStr = getRuleSetForCSpecEngine(rulseSetId);
                if(ruleSetJSONStr == null || ruleSetJSONStr.equals("")){
                    continue mainLoop;
                }

                cSpecEngineDTO = new CSpecEngineDTO(engineId, engineSummary, organizationName, rulseSetId, ruleSetURL, null, ruleSetJSONStr);

                EngineRelatedGeneDTO engineRelatedGene = null;
                JSONArray genes = (JSONArray) ruleSetObj.get("genes");
                if(genes != null){
                    for(Object gene : genes){
                        JSONObject geneObj = (JSONObject) gene;
                        String geneName = String.valueOf(geneObj.get("label"));

                        engineRelatedGene = new EngineRelatedGeneDTO(geneName);

                        JSONArray diseases = (JSONArray) geneObj.get("diseases");
                        if(diseases != null && diseases.size() != 0){
                            for(Object disease : diseases){
                                JSONObject diseaseObj = (JSONObject) disease;
                                String diseaseMongoId = String.valueOf(diseaseObj.get("label"));
                                Condition c = conditionRepository.getConditionById(diseaseMongoId);
                                if(c != null){
                                    engineRelatedGene.addConditions(new ConditionsTermAndIdDTO(diseaseMongoId, c.getTerm()));
                                }
                            }
                        }
                        cSpecEngineDTO.addGenes(engineRelatedGene);
                    }
                }

                cSpecEngineDTOList.add(cSpecEngineDTO);
            }
        }catch(Exception e){
            logger.info(StackTracePrinter.printStackTrace(e));
        }
        logger.info("Received info on "+cSpecEngineDTOList.size()+" valid (non legacy) CSpecEgines!");
        return cSpecEngineDTOList;
    }

    private String getRuleSetForCSpecEngine(int ruleSetID){
        String response = getcSpecEngineRuleSet(ruleSetID);
        if(response == null || response.equals("")){
            return null;
        }
        try {
            JSONObject obj = (JSONObject) jsonParser.parse(response);
            JSONObject data = (JSONObject) obj.get("data");
            if(data == null){
                return null;
            }
            JSONObject entContent = (JSONObject) data.get("entContent");
            if(entContent == null){
                return null;
            }
            JSONObject rules = (JSONObject) entContent.get("rules");
            if(rules == null){
                return null;
            }

            JSONArray mainRules = (JSONArray) rules.get("mainRules");
            if(mainRules == null || mainRules.size() == 0){
                return null;
            }

            return mainRules.toJSONString();
        }catch(Exception e){
            logger.error(StackTracePrinter.printStackTrace(e));
        }
        return null;
    }

    @Override
    public CSpecEngineDTO getCSpecEngineInfo(String cspecengineId){
        CSpecEngineDTO cspecengineDTO = null;
        CSpecRuleSet cspec = cSpecRuleSetRepository.getCSpecRuleSetById(cspecengineId);
        if(cspec == null){
            return null;
        }
        Set<EngineRelatedGeneDTO> erGenesSet = processRelatedGenes(cspec);
        cspecengineDTO = new CSpecEngineDTO(cspec.getEngineId(), cspec.getEngineSummary(), cspec.getOrganizationName(), cspec.getRuleSetId(), cspec.getRuleSetURL(), erGenesSet);
        return cspecengineDTO;
    }

    @Override
    public AssertionsDTO getCSpecRuleSet(CSpecEngineRuleSetRequest cSpecEngineRuleSetRequest){
        CSpecRuleSet cspec = cSpecRuleSetRepository.getCSpecRuleSetById( cSpecEngineRuleSetRequest.getCspecengineId());
        if(cspec == null || cspec.getRuleSetJSONStr() == null || cspec.getRuleSetJSONStr().equals("")){
            return null;
        }

        try{
            if(jsonParser == null){
                jsonParser = new JSONParser();
            }
            JSONArray arrayObj = (JSONArray) jsonParser.parse(cspec.getRuleSetJSONStr());
            return determineAssertions(arrayObj, cSpecEngineRuleSetRequest.getEvidenceMap());
        }catch(Exception e){
            System.out.println(StackTracePrinter.printStackTrace(e));
        }
        return null;
    }

    @Override
    public ArrayList<CSpecEngineDTO> getCSpecEnginesInfo(){
        List<CSpecRuleSet> allEnginesInfo = cSpecRuleSetRepository.findAll();
        if(allEnginesInfo == null || allEnginesInfo.size() == 0){
            return null;
        }

        ArrayList<CSpecEngineDTO> enginesDTOList = new ArrayList<CSpecEngineDTO>();
        for(CSpecRuleSet e : allEnginesInfo){
            Set<EngineRelatedGeneDTO> erGenesSet = processRelatedGenes(e);
            enginesDTOList.add(new CSpecEngineDTO(e.getEngineId(), e.getEngineSummary(), e.getOrganizationName(),
                    e.getRuleSetId(), e.getRuleSetURL(), erGenesSet));
        }
        return enginesDTOList;
    }

    private Set<EngineRelatedGeneDTO> processRelatedGenes(CSpecRuleSet e){
        EngineRelatedGeneDTO erGene = null;
        Set<EngineRelatedGeneDTO> erGenesSet = null;
        if(e.getGenes() != null && e.getGenes().size() > 0){
            erGenesSet = new HashSet<EngineRelatedGeneDTO>();
            Set<Gene> gList = e.getGenes();
            for(Gene g : gList){
                if(g.getConditions() != null && g.getConditions().size() > 0){
                    Set<Condition> cSet = g.getConditions();
                    ArrayList<ConditionsTermAndIdDTO> condDTOList = new ArrayList<ConditionsTermAndIdDTO>();
                    for(Condition c : cSet){
                        condDTOList.add(new ConditionsTermAndIdDTO(c.getCondition_id(), c.getTerm()));
                    }
                    erGene = new EngineRelatedGeneDTO(g.getGeneId(), condDTOList);
                }else{
                    erGene = new EngineRelatedGeneDTO(g.getGeneId());
                }
                if(erGene != null){
                    erGenesSet.add(erGene);
                }
            }
        }
        return erGenesSet;
    }

    @Override
    public String callScpecEngine(CSpecEngineRuleSetRequest cSpecEngineRuleSetRequest){
        Integer ruleSetId = null;
        if(cSpecEngineRuleSetRequest.getRulesetId() != null && !cSpecEngineRuleSetRequest.getRulesetId().equals("")){
            ruleSetId = cSpecEngineRuleSetRequest.getRulesetId();
        }else{
            CSpecRuleSet cspec = cSpecRuleSetRepository.getCSpecRuleSetById(cSpecEngineRuleSetRequest.getCspecengineId());
            if(cspec != null){
                ruleSetId = cspec.getRuleSetId();
            }
        }

        if(ruleSetId == null){
            logger.error("Unabale to get rulesetId!");
            return null;
        }

        JSONObject obj = new JSONObject();
        obj.put("cspecRuleSetUrl",cspecRuleSetNoIdUrl+ruleSetId);
        obj.put("evidence",cSpecEngineRuleSetRequest.getEvidenceMap());
        String jsonData = obj.toJSONString();

        HashMap<String,String> httpProperties = new HashMap<String,String>();
        httpProperties.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_APP_JSON);

        HTTPSConnector https = new HTTPSConnector();
        String response = https.sendHttpsRequest(cspecAssertionsURL, Constants.HTTP_POST, jsonData, httpProperties);
        return response;
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

    private String getcSpecEngineRuleSet(int ruseSetId){
        HashMap<String,String> httpProperties = new HashMap<String,String>();
        httpProperties.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_APP_JSON);

        String cspecRuleSetWithIdUrl = cspecRuleSetNoIdUrl+ruseSetId;

        HTTPSConnector https = new HTTPSConnector();
        String response = https.sendHttpsRequest(cspecRuleSetWithIdUrl, Constants.HTTP_GET, null, httpProperties);
        return response;
    }

    private AssertionsDTO determineAssertions(JSONArray arrayObj, Map<String,Integer> evidenceMap){
        AssertionsDTO assertionsDTO = null;

        if(arrayObj == null || arrayObj.size() == 0){
            logger.error("Error: Unable to get CSpec Engine rule set from the response, entContent property is null!");
            return null;
        }
        assertionsDTO = new AssertionsDTO();

        for(Object obj : arrayObj){
            JSONObject ruleObj = (JSONObject) obj;
            String currentInference = String.valueOf(ruleObj.get("inference"));

            //individual rules
            String extractedCondLabels = "";
            String extractedEvidenceTableColumnMarkers = "";
            int totalCondVal = 0;

            JSONArray conditions = (JSONArray) ruleObj.get("conditions");
            int passedConditions = conditions.size();
            int j = passedConditions;
            for(int k=0; k<j; k++){
                JSONObject condition = (JSONObject) conditions.get(k);

                String conditionValue = String.valueOf(condition.get("condition"));
                int currentCondNumValue = getValueForCondition(conditionValue);
                String partitionPathVal = String.valueOf(condition.get("partitionPath"));
                boolean evidenceFailed = true;

                if(evidenceMap != null){
                    //evidenceSet co tines all of the currently add evidences
                    if(evidenceMap.get(partitionPathVal) != null){
                        int totalNumOfEvidenceAdded = evidenceMap.get(partitionPathVal);
                        if(totalNumOfEvidenceAdded < currentCondNumValue){
                            //there aren't enough added evidences to satisfy this condition, therefor the condition failed to pass
                            passedConditions--;
                        }else{
                            evidenceFailed = false;
                        }
                    }else{
                        passedConditions--;
                    }
                }else{
                    passedConditions--;
                }

                if(evidenceFailed){
                    totalCondVal = totalCondVal + currentCondNumValue;
                }

                extractedEvidenceTableColumnMarkers = extractedEvidenceTableColumnMarkers + determineEvidenceTableColumnMarkerValue(partitionPathVal)+"_";

                if(k == (j-1)){
                    extractedCondLabels = extractedCondLabels + partitionPathVal+""+conditionValue;
                }else{
                    extractedCondLabels = extractedCondLabels + partitionPathVal+""+conditionValue+" & ";
                }
            }

            if(totalCondVal > 2){
                continue;
            }

            RuleConditionDTO rcTDO = null;
            if(evidenceMap != null){
                if(passedConditions == j){
                    if(assertionsDTO.getReachedRuleSetMap().get(currentInference) == null){
                        assertionsDTO.addToReachedRuleSet(currentInference, new ArrayList<RuleConditionDTO>());
                    }
                    rcTDO = new RuleConditionDTO(extractedCondLabels, extractedEvidenceTableColumnMarkers);
                    assertionsDTO.addReachedRuleSetConditionForKey(currentInference,rcTDO);
                }else{
                    if(assertionsDTO.getFailedRuleSetMap().get(currentInference) == null){
                        assertionsDTO.addToFailedRuleSet(currentInference, new ArrayList<RuleConditionDTO>());
                    }
                    rcTDO = new RuleConditionDTO(extractedCondLabels, extractedEvidenceTableColumnMarkers, totalCondVal);
                    assertionsDTO.addFailedRuleSetConditionForKey(currentInference,rcTDO);
                }
            }else{
                if(passedConditions < j){
                    if(assertionsDTO.getFailedRuleSetMap().get(currentInference) == null){
                        assertionsDTO.addToFailedRuleSet(currentInference, new ArrayList<RuleConditionDTO>());
                    }
                    rcTDO = new RuleConditionDTO(extractedCondLabels, extractedEvidenceTableColumnMarkers, totalCondVal);
                    assertionsDTO.addFailedRuleSetConditionForKey(currentInference,rcTDO);
                }
            }
        }

        sortFailedRuleSet(assertionsDTO.getFailedRuleSetMap());

        return assertionsDTO;
    }

    private int getValueForCondition(String cValue){
        int baseVal = 0;
        switch(cValue){
            case "==1": baseVal = 1; break;
            case ">=1": baseVal = 1; break;
            case "==2": baseVal = 2; break;
            case ">=2": baseVal = 2; break;
            case "==3": baseVal = 3; break;
            case ">=3": baseVal = 3; break;
            case "==4": baseVal = 4; break;
            case ">=4": baseVal = 4; break;
            case "==5": baseVal = 5; break;
            case ">=5": baseVal = 5; break;
            case "==6": baseVal = 6; break;
            case ">=6": baseVal = 6; break;
        }
        return baseVal;
    }

    private String determineEvidenceTableColumnMarkerValue(String partitionPathVal){
        String[] partitionPathValArray = partitionPathVal.split("\\.");
        String pathBasic = partitionPathValArray[0];
        String pathDetail = partitionPathValArray[1];

        String markerValue = "";

        if(pathBasic.equals("Benign")){
            markerValue = markerValue + '1';

            if(pathDetail.equals("Supporting")){
                markerValue = markerValue + "1";
            }else if(pathDetail.equals("Strong")){
                markerValue = markerValue + "2";
            }else if(pathDetail.equals("Stand Alone")){
                markerValue = markerValue + "3";
            }
        }else if(pathBasic.equals("Pathogenic")){
            markerValue = markerValue + "2";

            if(pathDetail.equals("Supporting")){
                markerValue = markerValue + "1";
            }else if(pathDetail.equals("Moderate")){
                markerValue = markerValue + "2";
            }else if(pathDetail.equals("Strong")){
                markerValue = markerValue + "3";
            }else if(pathDetail.equals("Very Strong")){
                markerValue = markerValue + "4";
            }
        }

        return markerValue;
    }

    private void sortFailedRuleSet(Map<String, ArrayList<RuleConditionDTO>> failedRuleSetMap){
        Iterator<String> iter = failedRuleSetMap.keySet().iterator();
        mainLoop:
        while(iter.hasNext()){
            String key = iter.next();
            ArrayList<RuleConditionDTO> ruleConditions = failedRuleSetMap.get(key);
            if(ruleConditions.size() == 0){
                continue mainLoop;
            }

            while(true){
                boolean switchMade = false;
                int n = ruleConditions.size();
                for(int i=0; n>i; i++){
                    RuleConditionDTO rule = ruleConditions.get(i);
                    int currentCondLeft = rule.getConditionsLeft();
                    if(i < (n-1) && ruleConditions.get(i+1) != null && currentCondLeft > ruleConditions.get(i+1).getConditionsLeft()){
                        RuleConditionDTO tempRule = ruleConditions.get((i+1));
                        ruleConditions.set((i+1), rule);
                        ruleConditions.set(i, tempRule);
                        switchMade = true;
                    }
                }
                if(!switchMade){
                    break;
                }
            }

        }
    }
}
