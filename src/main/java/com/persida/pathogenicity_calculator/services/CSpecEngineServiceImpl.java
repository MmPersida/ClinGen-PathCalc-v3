package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.CSpecEngineRuleSetRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.SortedCSpecEnginesRequest;
import com.persida.pathogenicity_calculator.dto.*;
import com.persida.pathogenicity_calculator.repository.CSpecRuleSetRepository;
import com.persida.pathogenicity_calculator.repository.ConditionRepository;
import com.persida.pathogenicity_calculator.repository.entity.CSpecRuleSet;
import com.persida.pathogenicity_calculator.repository.entity.Condition;
import com.persida.pathogenicity_calculator.repository.entity.Gene;
import com.persida.pathogenicity_calculator.repository.jpa.CSpecRuleSetJPA;
import com.persida.pathogenicity_calculator.utils.HTTPSConnector;
import com.persida.pathogenicity_calculator.utils.StackTracePrinter;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import lombok.Data;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CSpecEngineServiceImpl implements CSpecEngineService{
    static Logger logger = Logger.getLogger(CSpecEngineServiceImpl.class);

    @Value("${numOfEnginesPerPage}")
    private Integer numOfEnginesPerPage;

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

    private JSONObject vcepIDsToEnableByDefault = null;


    @PostConstruct
    public void init() {
        vcepIDsToEnableByDefault = readVCEPsInclusionFile();
        logger.info("Read VCEps inclusion list, total number to enable for usage: "+vcepIDsToEnableByDefault.size());
    }

    @Override
    public ArrayList<CSpecEngineDTO> getCSpecEnginesInfoByCall(){
        ArrayList<JSONObject> completeResponseList = null;
        String  enginesListResponse = null;
        int pageNum = 1;
        int iterLimit = 150;
        int totalNumOfEnginesFromResponse = 0;
        logger.info("Getting response from CSpecEngines API, "+numOfEnginesPerPage+" per page!");
        mainLoop:
        while(true){
            enginesListResponse = getListOfSpecEngines("&pg=" + pageNum, "&pgSize="+numOfEnginesPerPage);
            if(enginesListResponse != null && !enginesListResponse.equals("")){
                JSONArray dataArray = null;
                try {
                    if(jsonParser == null){
                        jsonParser = new JSONParser();
                    }
                    JSONObject obj = (JSONObject) jsonParser.parse(enginesListResponse);
                    dataArray = (JSONArray) obj.get("data");
                    if(dataArray != null && dataArray.size() > 0){
                        if(dataArray.size() < numOfEnginesPerPage){
                            pageNum = 100; //first measure to make sure that the loop stops
                        }
                        if(completeResponseList == null){
                            completeResponseList = new ArrayList<JSONObject>();
                        }
                        for(Object dataObj : dataArray){
                            completeResponseList.add((JSONObject) dataObj);
                        }
                        totalNumOfEnginesFromResponse += dataArray.size();

                        if(dataArray.size() < numOfEnginesPerPage){
                            break mainLoop; //second measure to make sure that the loop stops
                        }
                    }
                }catch(Exception e){
                    logger.error(StackTracePrinter.printStackTrace(e));
                }
            }
            pageNum++;
            if(pageNum == iterLimit){
                //something is wrong at this point!
                logger.error("The engines loop has iterated "+iterLimit+" times!");
                break mainLoop;
            }
        }

        if(completeResponseList == null){
            return null;
        }
        logger.info("Finished collecting Engines(RuleSets) from responses, gathered total num: "+totalNumOfEnginesFromResponse);

        ArrayList<CSpecEngineDTO> cSpecEngineDTOList = new ArrayList<CSpecEngineDTO>();
        try {
            CSpecEngineDTO cSpecEngineDTO = null;
            URI uri = null;
            mainLoop:
            for (JSONObject cspecEngineObj : completeResponseList) {
                JSONObject entContentObj = (JSONObject) cspecEngineObj.get("entContent");
                if (entContentObj == null) {
                    continue mainLoop;
                }

                //engine has never been approved if the entContent.states has NO items where name === 'Released'
                boolean isReleased = false;
                if (entContentObj.get("states") != null) {
                    JSONArray statesArray = (JSONArray) entContentObj.get("states");
                    if (statesArray != null && statesArray.size() > 0) {
                        stateLoop:
                        for (Object stateObj : statesArray) {
                            JSONObject stateJsonObj = (JSONObject) stateObj;
                            String stateName = String.valueOf(stateJsonObj.get("name"));
                            if (stateName.equals("Released")) {
                                isReleased = true;
                                break stateLoop;
                            }
                        }
                    }
                } else {
                    isReleased = true;
                }

                if (!isReleased) {
                    continue mainLoop;
                }
                if (entContentObj.get("legacyReplaced") != null && Boolean.valueOf(String.valueOf(entContentObj.get("legacyReplaced")))) {
                    continue mainLoop;
                }
                if (entContentObj.get("legacyFullySuperseded") != null && !Boolean.valueOf(String.valueOf(entContentObj.get("legacyFullySuperseded")))) {
                    continue mainLoop;
                }

                String engineId = String.valueOf(cspecEngineObj.get("entId"));

                //chek is this engine id in the inclusion list, if so it will be marked as enabled and can be used for interpretations
                boolean enabled = false;
                JSONObject vcepToSkipObj =  (JSONObject) vcepIDsToEnableByDefault.get(engineId);
                if(vcepToSkipObj != null){
                    enabled= true;
                }

                String engineInfoResponse = getcSpecEngineRelatedInfo(engineId);
                if (engineInfoResponse == null) {
                    continue mainLoop;
                }

                JSONObject engineInfObj = (JSONObject) jsonParser.parse(engineInfoResponse);
                String engineSummary = String.valueOf(engineInfObj.get("label"));

                JSONObject affiliationObj = (JSONObject) engineInfObj.get("affiliation");
                String organizationName = String.valueOf(affiliationObj.get("label"));
                String organizationLink = String.valueOf(affiliationObj.get("url"));

                JSONArray ruleSetsObj = (JSONArray) engineInfObj.get("ruleSets");
                JSONObject ruleSetObj = (JSONObject) ruleSetsObj.get(0);
                String ruleSetURL = String.valueOf(ruleSetObj.get("@id"));

                uri = new URI(ruleSetURL);
                String[] segments = uri.getPath().split("/");
                String idStr = segments[segments.length - 1];
                int rulseSetId = Integer.parseInt(idStr);

                //get the combined rule-sets and criteria codes
                MainRulesAndCriteriaCodes mainRulesAndCriteriaCodes = getMainRulesAndCriteriaCodesFromRuleSetInfo(rulseSetId);
                if (mainRulesAndCriteriaCodes == null) {
                    continue mainLoop;
                }

                //if the main rules (Guideline Combining Criteria) are not present just mark it as disabled to be sure
                String ruleSetJSONStr = mainRulesAndCriteriaCodes.getMainRules().toJSONString();
                if (ruleSetJSONStr == null || ruleSetJSONStr.equals("")) {
                    enabled = false;
                }

                //set all basic data including main rule set
                cSpecEngineDTO = new CSpecEngineDTO(engineId, engineSummary, organizationName, organizationLink, rulseSetId, ruleSetURL, null, ruleSetJSONStr, enabled);

                //set criteriaCodes - evidence tag info for this engine (VCEP), at this point this can be null
                JSONArray criteriaCodes = mainRulesAndCriteriaCodes.getCriteriaCodes();
                String criteriaCodesJsonStr = processCriteriaCodes(criteriaCodes, rulseSetId);

                if (criteriaCodesJsonStr != null && !criteriaCodesJsonStr.equals("")) {
                    cSpecEngineDTO.setCriteriaCodesJSONStr(criteriaCodesJsonStr);
                }

                //set related genes
                JSONArray genes = (JSONArray) ruleSetObj.get("genes");
                if (genes != null) {
                    ArrayList<EngineRelatedGeneDTO> engineRelatedGeneDTOList = processEngineRelatedGenes(genes);
                    if (engineRelatedGeneDTOList != null && engineRelatedGeneDTOList.size() > 0) {
                        for (EngineRelatedGeneDTO e : engineRelatedGeneDTOList) {
                            cSpecEngineDTO.addGenes(e);
                        }
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

    private MainRulesAndCriteriaCodes getMainRulesAndCriteriaCodesFromRuleSetInfo(int ruleSetID){
        String response = getcSpecEngineRuleSet(ruleSetID);
        if(response == null || response.equals("")){
            return null;
        }

        try {
            //main rules
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

            //CriteriaCode's - applicable evidence tags
            JSONObject ld = (JSONObject) data.get("ld");
            if(ld == null){
                return null;
            }
            JSONArray criteriaCode = (JSONArray) ld.get("CriteriaCode");
            if(criteriaCode == null){
                return null;
            }

            return new MainRulesAndCriteriaCodes(mainRules, criteriaCode);
        }catch(Exception e){
            logger.error(StackTracePrinter.printStackTrace(e));
        }
        return null;
    }

    private String processCriteriaCodes(JSONArray criteriaCodes, int ruleSetId){
        if(criteriaCodes == null || criteriaCodes.size() == 0){
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        //CriteriaCode criteriaCode = null;
        JSONObject jsonObj = null;

        for(Object o : criteriaCodes){
            JSONObject criteriaCodeObj = (JSONObject) o;
            JSONObject entContent = (JSONObject) criteriaCodeObj.get("entContent");
            if(entContent == null){
                continue;
            }
            jsonObj = new JSONObject();

            String name = String.valueOf(entContent.get("label"));
            jsonObj.put("name", name);

            boolean applicable = true;
            String applicability = String.valueOf(entContent.get("applicability"));
            if(applicability != null && !applicability.equals("") && !applicability.equals("null")){
                if((applicability != null && applicability.equals("Applicable"))){
                    applicable = true;
                }else if(applicability.startsWith("Not Applicable")){
                    applicable = false;
                }else if(applicability.startsWith("Not applicable")){
                    //Not applicable
                    applicable = false;
                }else{
                    applicable = false;
                    logger.info("NOTE: Unknown applicability value for criteria code \""+applicability+"\" in ruleSet: "+ruleSetId);
                }
            }
            jsonObj.put("applicable", applicable);

            String comment = String.valueOf(entContent.get("additionalComments"));
            if(comment == null || comment.equals("")){
                comment = String.valueOf(entContent.get("originalACMGSummary"));
            }
            jsonObj.put("comment", comment);

            String infoURL = String.valueOf(criteriaCodeObj.get("ldhIri"));
            jsonObj.put("infoURL", infoURL);

            JSONArray genes = (JSONArray) entContent.get("gene");
            if(genes != null && genes.size() > 0){
                jsonObj.put("genes", genes);
            }

            JSONArray disease = (JSONArray) entContent.get("disease");
            if(disease != null && disease.size() > 0){
                jsonObj.put("diseases", disease);
            }

            jsonArray.add(jsonObj);
        }

        if(jsonArray.size() > 0){
            return jsonArray.toJSONString();
        }
        return null;
    }

    private ArrayList<EngineRelatedGeneDTO> processEngineRelatedGenes(JSONArray genes){
        if(genes == null || genes.size() == 0){
            return null;
        }
        ArrayList<EngineRelatedGeneDTO> list = new ArrayList<EngineRelatedGeneDTO>();

        for(Object gene : genes){
            JSONObject geneObj = (JSONObject) gene;
            String geneName = String.valueOf(geneObj.get("label"));

            EngineRelatedGeneDTO eRelatedGene = new EngineRelatedGeneDTO(geneName);

            JSONArray diseases = (JSONArray) geneObj.get("diseases");
            if(diseases != null && diseases.size() != 0){
                for(Object disease : diseases){
                    JSONObject diseaseObj = (JSONObject) disease;
                    String diseaseMongoId = String.valueOf(diseaseObj.get("label"));
                    Condition c = conditionRepository.getConditionById(diseaseMongoId);
                    if(c != null){
                        eRelatedGene.addConditions(new ConditionsTermAndIdDTO(diseaseMongoId, c.getTerm()));
                    }
                }
            }
            list.add(eRelatedGene);
        }
        return list;
    }

    private JSONObject readVCEPsInclusionFile(){
        String fileName = "vcepsIDsToInclude.json";
        try{
            InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, Constants.UTF8));

            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }

            if(responseStrBuilder.length() == 0){
                return null;
            }

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(responseStrBuilder.toString());
            if(jsonObj != null){
                return jsonObj;
            }
        }catch(Exception e){
            System.out.println(StackTracePrinter.printStackTrace(e));
        }
        return null;
    }

    @Override
    public ArrayList<CSpecEngineDTO> getVCEPsInfoByName(String vcepNamePartial){
        ArrayList<CSpecEngineDTO> cspecenginesList = null;
        List<CSpecRuleSet> allEnginesList = null;
        if(!vcepNamePartial.isEmpty() && vcepNamePartial.equals("all_data")){
            allEnginesList = cSpecRuleSetRepository.getAllEnabledCSpecEnginesInfo();
        }else if(!vcepNamePartial.isEmpty() && vcepNamePartial.length() >= 4){
            allEnginesList = cSpecRuleSetRepository.getAllEnabledCSpecEnginesInfoByNameLike(vcepNamePartial);
        }

        if(allEnginesList == null || allEnginesList.size() == 0){
            return cspecenginesList;
        }

        cspecenginesList = new ArrayList<CSpecEngineDTO>();

        CSpecEngineDTO cspecengineDTO = null;
        for(CSpecRuleSet cspec: allEnginesList){
            Set<EngineRelatedGeneDTO> erGenesSet = processRelatedGenes(cspec);
            cspecengineDTO = new CSpecEngineDTO(cspec.getEngineId(), cspec.getEngineSummary(),
                    cspec.getOrganizationName(), cspec.getOrganizationLink(),
                    cspec.getRuleSetId(), cspec.getRuleSetURL(), erGenesSet, cspec.getEnabled());
            cspecenginesList.add(cspecengineDTO);
        }
        return cspecenginesList;
    }

    @Override
    public CSpecEngineDTO getCSpecEngineInfo(String cspecengineId){
        CSpecEngineDTO cspecengineDTO = null;
        CSpecRuleSet cspec = cSpecRuleSetRepository.getCSpecRuleSetById(cspecengineId);
        if(cspec == null){
            return null;
        }
        Set<EngineRelatedGeneDTO> erGenesSet = processRelatedGenes(cspec);
        cspecengineDTO = new CSpecEngineDTO(cspec.getEngineId(), cspec.getEngineSummary(),
                cspec.getOrganizationName(), cspec.getOrganizationLink(),
                cspec.getRuleSetId(), cspec.getRuleSetURL(), erGenesSet, cspec.getEnabled());
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
    public SortedCSpecEnginesDTO getSortedAndEnabledCSpecEngines(SortedCSpecEnginesRequest sortedCSpecEnginesRequest){
        SortedCSpecEnginesDTO sCseDTO = null;

        List<CSpecRuleSetJPA> allEnginesList = cSpecRuleSetRepository.getAllEnabledCSpecEnginesBasicInfo();
        if(allEnginesList == null || allEnginesList.size() == 0){
            return sCseDTO;
        }

        //get engines that are linked to this gene or condition
        HashMap<String, CSpecRuleSetJPA> sortedEnginesMap = null;
        String conditionId = conditionRepository.getConditionIdFromName(sortedCSpecEnginesRequest.getCondition());
        List<CSpecRuleSetJPA> sortedEnginesList = cSpecRuleSetRepository.getSortedAndEnabledCSpecEngines(sortedCSpecEnginesRequest.getGene(), conditionId);
        if(sortedEnginesList != null && sortedEnginesList.size() > 0){
            sortedEnginesMap = new HashMap<String, CSpecRuleSetJPA>();
            for(CSpecRuleSetJPA e : sortedEnginesList){
                sortedEnginesMap.put(e.getEngineId(), e);
            }
        }

        sCseDTO = new SortedCSpecEnginesDTO();
        //go through all the engines available
        for(CSpecRuleSetJPA e : allEnginesList){
            CSpecEngineDTO cseDTO = new CSpecEngineDTO(e.getEngineId(), e.getEngineSummary(),
                                                        e.getOrganization(), e.getOrganizationLink());

            //if no engines that are linked to the specified gene or condition exist then load all of them in sCseDTO and be done
            if(sortedEnginesMap == null || sortedEnginesMap.size() == 0){
                //this is a way not to check other cases in vain from the beginning
                sCseDTO.addToOthersList(cseDTO);
                continue;
            }

            //if engines that are linked to the specified gene or condition exist, then first try to load those in separate lists then try "the other ones" at the very end
            CSpecRuleSetJPA tempEngine = sortedEnginesMap.get(e.getEngineId());
            if(tempEngine != null){
                if(tempEngine.getGeneId() != null && sortedCSpecEnginesRequest.getGene().equals(tempEngine.getGeneId()) &&
                        tempEngine.getConditionId() != null && conditionId.equals(tempEngine.getConditionId())){
                    sCseDTO.addToGeneAndConditionList(cseDTO);
                    continue;
                }
                if(sortedCSpecEnginesRequest.getGene().equals(tempEngine.getGeneId())){
                    sCseDTO.addToGeneList(cseDTO);
                    continue;
                }
                if(conditionId.equals(tempEngine.getConditionId())){
                    sCseDTO.addToConditionList(cseDTO);
                    continue;
                }
            }

            sCseDTO.addToOthersList(cseDTO);
        }

        return sCseDTO;
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

    @Override
    public String getRuleSetCriteriaCodes(String cspecengineId){
        CSpecRuleSet cspec = cSpecRuleSetRepository.getCSpecRuleSetById(cspecengineId);
        if(cspec == null || cspec.getCriteriaCodesJSONStr() == null || cspec.getCriteriaCodesJSONStr().equals("")){
            return null;
        }

        return cspec.getCriteriaCodesJSONStr();
    }

    private String getListOfSpecEngines(String pageNumberParam, String pageSizeParam){
        HashMap<String,String> httpProperties = new HashMap<String,String>();
        httpProperties.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_APP_JSON);

        String listOfAllCSpecEnginesWithPgNumURL =  listOfAllCSpecEngines + pageNumberParam + pageSizeParam;;

        HTTPSConnector https = new HTTPSConnector();
        String response = https.sendHttpsRequest(listOfAllCSpecEnginesWithPgNumURL, Constants.HTTP_GET, null, httpProperties);
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
        //cValue, example: ==1, >=1, ==2, >=2, ==5, >=5....

        Pattern pattern = Pattern.compile("^(==|>=)[1-9][1-9]*");
        Matcher matcher = pattern.matcher(cValue);
        if(matcher.find()){
            String value = cValue.substring(2, cValue.length());
            if(value !=null && !value.equals("")){
                try{
                    baseVal = Integer.parseInt(value);
                }catch(Exception e){
                    logger.error(StackTracePrinter.printStackTrace(e));
                }
            }
        }
        return baseVal;
    }

    private String determineEvidenceTableColumnMarkerValue(String partitionPathVal){
        String[] partitionPathValArray = partitionPathVal.split("\\.");
        String pathBasic = partitionPathValArray[0];
        String pathDetail = partitionPathValArray[1];

        String markerValue = "";

        if(pathBasic.equals(Constants.TYPE_BENIGN)) {
            markerValue += '1';

            if (pathDetail.equals(Constants.MODIFIER_SUPPORTING)) {
                markerValue += "1";
            } else if (pathDetail.equals(Constants.MODIFIER_MODERATE)) {
                markerValue += "2";
            } else if (pathDetail.equals(Constants.MODIFIER_STRONG)) {
                markerValue += "3";
            }else if(pathDetail.equals(Constants.MODIFIER_VERY_STRONG)){
                markerValue += "4";
            }else if(pathDetail.equals(Constants.MODIFIER_STAND_ALONE)){
                markerValue += "5";
            }
        }else if(pathBasic.equals(Constants.TYPE_PATHOGENIC)){
            markerValue += "2";

            if(pathDetail.equals(Constants.MODIFIER_SUPPORTING)){
                markerValue += "1";
            }else if(pathDetail.equals(Constants.MODIFIER_MODERATE)){
                markerValue += "2";
            }else if(pathDetail.equals(Constants.MODIFIER_STRONG)){
                markerValue += "3";
            }else if(pathDetail.equals(Constants.MODIFIER_VERY_STRONG)){
                markerValue += "4";
            }else if(pathDetail.equals(Constants.MODIFIER_STAND_ALONE)){
                markerValue += "5";
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

    @Data
    private class MainRulesAndCriteriaCodes{
        private JSONArray mainRules;
        private JSONArray criteriaCodes;

        public MainRulesAndCriteriaCodes(JSONArray mainRules, JSONArray criteriaCodes){
            this.mainRules = mainRules;
            this.criteriaCodes = criteriaCodes;
        }
    }
}
