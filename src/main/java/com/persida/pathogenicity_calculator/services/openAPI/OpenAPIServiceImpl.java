package com.persida.pathogenicity_calculator.services.openAPI;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.*;
import com.persida.pathogenicity_calculator.services.JWT.JWTservice;
import com.persida.pathogenicity_calculator.dto.*;
import com.persida.pathogenicity_calculator.model.JWTHeaderAndPayloadData;
import com.persida.pathogenicity_calculator.model.openAPI.*;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.*;
import com.persida.pathogenicity_calculator.repository.FinalCallRepository;
import com.persida.pathogenicity_calculator.repository.UserRepository;
import com.persida.pathogenicity_calculator.repository.VariantInterpretationRepository;
import com.persida.pathogenicity_calculator.repository.entity.*;
import com.persida.pathogenicity_calculator.repository.jpa.FinalCallJPA;
import com.persida.pathogenicity_calculator.services.*;
import com.persida.pathogenicity_calculator.utils.DateUtils;
import com.persida.pathogenicity_calculator.utils.EvidenceMapperAndSupport;
import com.persida.pathogenicity_calculator.utils.StackTracePrinter;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class OpenAPIServiceImpl implements OpenAPIService {
    static Logger logger = Logger.getLogger(CalculatorServiceImpl.class);

    @Autowired
    private JWTservice jwtUtils;
    @Autowired
    private VariantInterpretationService variantInterpretationService;
    @Autowired
    private ConditionsService conditionsService;
    @Autowired
    private CalculatorService calculatorService;
    @Autowired
    private CSpecEngineService cSpecEngineService;
    @Autowired
    private EvidenceService evidenceService;
    @Autowired
    private VariantInterpretationRepository variantInterpretationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FinalCallRepository finalCallRepository;

    @Override
    public SRVCResponse srvc() {
        return new SRVCResponse();
    }

    @Override
    public TokenResponse tokenRequest(RequestAuthData requestAuthData) {
        String tokenValue = jwtUtils.getTokenFromAuthAPI(requestAuthData.getUsername(), requestAuthData.getPass());

        if (tokenValue == null || tokenValue.equals("")) {
            return new TokenResponse("Invalid credentials!");
        }

        JWTHeaderAndPayloadData jwtData = jwtUtils.decodeAndValidateToken(tokenValue);
        if (jwtData == null) {
            logger.error("Unable to validate token for user : " + requestAuthData.getUsername() + "!");
            return new TokenResponse("Unable to validate token for user : " + requestAuthData.getUsername() + "!");
        }

        if (jwtData.getUsername().equals(requestAuthData.getUsername())) {
            return new TokenResponse(tokenValue, jwtData.getTokenExpTime());
        } else {
            return null;
        }
    }

    @Override
    public ClassificationsResponse allClassificationsForUser(String username, boolean useHighDetail) {
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            return new ClassificationsResponse("Unable to determine user!", Constants.NAME_INVALID);
        }

        List<VariantInterpretation> viList = variantInterpretationService.getAllInterpretedVariantsByUser(user.getId());
        if (viList == null || viList.isEmpty()) {
            return new ClassificationsResponse("No classifications can be found for this user!", Constants.NAME_NOT_FOUND);
        }
        return convertVIListToClassReponse(viList, null, user, useHighDetail);
    }

    @Override
    public ClassificationsResponse classificationsForVariant(ClassByVariantRequest classRequest, String username, boolean useHighDetail) {
        String caid = classRequest.getCaid();
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            return new ClassificationsResponse("Unable to determine user!", Constants.NAME_INVALID);
        }

        List<VariantInterpretation> viList = variantInterpretationService.getUserVIForCaid(user.getId(), caid);
        if (viList == null || viList.isEmpty()) {
            return new ClassificationsResponse("No classifications can be found using the provided CAID: " + caid, Constants.NAME_NOT_FOUND);
        }
        return convertVIListToClassReponse(viList, caid, user, useHighDetail);
    }

    @Override
    public ClassificationResponse classificationById(ClassByIdRequest classByIdReq, String username, boolean useHighDetail) {
        Integer classId = classByIdReq.getClassId();
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            return new ClassificationResponse("Unable to determine user!", Constants.NAME_INVALID);
        }

        VariantInterpretation vi = variantInterpretationService.getInterpretationById(classId);
        if (vi == null) {
            return new ClassificationResponse("No classification can be found using the provided ID: " + classId, Constants.NAME_NOT_FOUND);
        }

        ClassificationResponse cr = new ClassificationResponse(mapVItoClassification(vi, username, useHighDetail));

        if(useHighDetail && vi.getEvidences() != null && !vi.getEvidences().isEmpty()) {
            CSpecEngineRuleSetRequest ruleSetRequest = new CSpecEngineRuleSetRequest();
            ruleSetRequest.setCspecengineId(vi.getCspecRuleSet().getEngineId());

            EvidenceMapperAndSupport esMapperSupport = new EvidenceMapperAndSupport();
            Map<String,Integer> eMap = esMapperSupport.formatEvdSetToCSpecEvdMap(vi.getEvidences());
            ruleSetRequest.setEvidenceMap(eMap);

            AssertionsDTO assertionsDTO = cSpecEngineService.getCSpecRuleSet(ruleSetRequest);
            if(assertionsDTO != null && (assertionsDTO.getFailedAssertions() != null || assertionsDTO.getReachedAssertions() != null)){
                AssertionsDTOResponse aDTOResp = new AssertionsDTOResponse();
                aDTOResp.setReachedAssertions(formatAssertionDTOtoResponse(assertionsDTO.getReachedAssertions()));
                aDTOResp.setFailedAssertions(formatAssertionDTOtoResponse(assertionsDTO.getFailedAssertions()));
                cr.getData().getEntContent().setAssertions(aDTOResp);
            }
        }
        return cr;
    }

    @Override
    public DiseasesResponse getDiseasesLike(String partialDiseaseTerm){
        List<ConditionsTermAndIdDTO> condList = conditionsService.getConditionsLike(partialDiseaseTerm);
        if(condList == null || condList.isEmpty()){
            return new DiseasesResponse("No Disease types can be found for the provided partial name.", Constants.NAME_NOT_FOUND);
        }
        DiseasesResponse dr = new DiseasesResponse();

        for(ConditionsTermAndIdDTO cond : condList){
            dr.getData().addDisease(new Disease(cond.getConditionId(), cond.getTerm()));
        }
        return dr;
    }

    @Override
    public MOIResponse getModesOfInheritance(){
        List<IheritanceDTO> moiList = calculatorService.getInheritanceModes();
        if(moiList == null || moiList.isEmpty()){
            return new MOIResponse("Inheritance modes cannot be retrieved at this moment.", Constants.NAME_INVALID);
        }
        MOIResponse moi = new MOIResponse();
        moi.getData().setModesOfInheritance(moiList);
        return moi;
    }

    @Override
    public SpecificationsResponse getSpecifications(){
        SortedCSpecEnginesDTO sortedCSpecList = cSpecEngineService.getSortedAndEnabledCSpecEngines(new SortedCSpecEnginesRequest());
        ArrayList<CSpecEngineDTO> cSpecList = sortedCSpecList.getOthersList();
        if(cSpecList == null || cSpecList.size() == 0){
            return new SpecificationsResponse("Specification data cannot be retrieved at this moment.", Constants.NAME_INVALID);
        }

        SpecificationsResponse sr = new SpecificationsResponse();
        for(CSpecEngineDTO csDTO : cSpecList){
            sr.getData().addSpecification(new Specification(csDTO.getEngineId(), csDTO.getEngineSummary(),
                                                csDTO.getOrganizationName(), csDTO.getOrganizationLink(),
                                                csDTO.getRuleSetId(), csDTO.getRuleSetURL()));
        }
        return sr;
    }

    @Override
    public ClassificationResponse createClassification(CreateUpdateClassWithEvidencesRequest ccRequest, String username) {
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            return new ClassificationResponse("Unable to determine user!", Constants.NAME_INVALID);
        }

        if(ccRequest == null){
            return new ClassificationResponse("Input data missing, formatted improperly or null.", Constants.NAME_INVALID);
        }

        VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq = mapToVarInterpSaveUpdateEvidenceDocRequest(ccRequest);
        VariantInterpretationSaveResponse viSaveUpdateResp = variantInterpretationService.saveNewInterpretation(viSaveEvdUpdateReq, user);

        if(ccRequest.getEvidenceList() != null && !ccRequest.getEvidenceList().isEmpty()) {
            EvidenceListDTO elDTO = mapToEvidenceListDTO(viSaveUpdateResp, ccRequest.getEvidenceList());
            evidenceService.saveNewEvidence(elDTO);
        }

        String dfcValue = null;
        String rgName = ccRequest.getGene();

        ClassificationEntContent cec = new ClassificationEntContent(viSaveUpdateResp.getCspecengineId(), rgName, viSaveEvdUpdateReq.getCondition(),
                viSaveEvdUpdateReq.getInheritance(), viSaveUpdateResp.getCalculatedFinalCall().getTerm(), dfcValue);

        Classification c = new Classification(cec, viSaveUpdateResp.getInterpretationId(), viSaveEvdUpdateReq.getCaid(),
                DateUtils.dateToStringParser(new Date()), null, username);
        ClassificationResponse cr = new ClassificationResponse(c);
        return cr;
    }

    @Override
    public ClassificationResponse updateClassification(CreateUpdateClassRequest ucRequest, String username){
        if(ucRequest == null){
            return new ClassificationResponse("Input data missing, formatted improperly or null.", Constants.NAME_INVALID);
        }

        VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq = mapToVarInterpSaveUpdateEvidenceDocRequest(ucRequest);
        VariantInterpretationSaveResponse viSaveUpdateResp = variantInterpretationService.updateEvidenceDocAndEngine(viSaveEvdUpdateReq);

        /*
        FinalCallDTO calculatedFC = null;
        if(ucRequest.getEvidenceList() != null && !ucRequest.getEvidenceList().isEmpty()){
            EvidenceListDTO elDTO = mapToEvidenceListDTO(viSaveUpdateResp, ucRequest.getEvidenceList());
            evidenceService.saveNewEvidence(elDTO);

            CSpecEngineRuleSetRequest cSpecReq = new CSpecEngineRuleSetRequest();
            cSpecReq.setCspecengineId(viSaveUpdateResp.getCspecengineId());

            EvidenceMapperAndSupport esMapperSupport = new EvidenceMapperAndSupport();
            Map<String,Integer> eMap = esMapperSupport.formatEvdDTOListToCSpecEvdMap(ucRequest.getEvidenceList());

            cSpecReq.setEvidenceMap(eMap);
            calculatedFC = cSpecEngineService.callScpecEngine(cSpecReq);
        }
        if(calculatedFC == null){
            return new ClassificationResponse("Unable to call the specification engine to get the Final Call value, please try latter.", Constants.NAME_ERROR);
        }*/

        /*
        if(viSaveUpdateResp.getCalculatedFinalCall().getId() != calculatedFC.getId()){
            variantInterpretationService.updateCalculatedFinalCall(new VarInterpUpdateFinalCallRequest(ucRequest.getClassificationId(), calculatedFC.getId()));
        }*/

        String expertFC = null;
        if(viSaveUpdateResp.getDeterminedFinalCall() != null && viSaveUpdateResp.getDeterminedFinalCall().getId() != null){
            expertFC = viSaveUpdateResp.getDeterminedFinalCall().getTerm();
        }

        ClassificationEntContent cec = new ClassificationEntContent(viSaveUpdateResp.getCspecengineId(), ucRequest.getGene(),
                viSaveEvdUpdateReq.getCondition(), viSaveEvdUpdateReq.getInheritance(),
                viSaveUpdateResp.getCalculatedFinalCall().getTerm(), expertFC);

        Classification c = new Classification(cec, viSaveUpdateResp.getInterpretationId(), viSaveEvdUpdateReq.getCaid(),
                DateUtils.dateToStringParser(new Date()), null, username);
        ClassificationResponse cr = new ClassificationResponse(c);
        return cr;
    }

    @Override
    public ClassificationResponse deleteClassification(ClassificationIDRequest classIdRequest){
        VariantInterpretationIDRequest viReq = new VariantInterpretationIDRequest(classIdRequest.getClassificationId());
        VariantInterpretationSaveResponse viSaveResp = variantInterpretationService.deleteInterpretation(viReq);

        ClassificationEntContent cec = new ClassificationEntContent();
        Classification c = new Classification(viSaveResp.getInterpretationId());
        ClassificationResponse cr = new ClassificationResponse(c);
        return cr;
    }

    @Override
    public ClassificationResponse addEvidence(AddEvidencesRequest evdRequest, String username){
        VariantInterpretation vi = variantInterpretationService.getInterpretationById(evdRequest.getClassificationId());
        if(vi == null){
            return new ClassificationResponse("Unable to get the specification data.", Constants.NAME_ERROR);
        }

        if(evdRequest.getEvidenceList() != null && !evdRequest.getEvidenceList().isEmpty()){
            EvidenceMapperAndSupport esMapperSupport = new EvidenceMapperAndSupport();
            HashMap<String, Evidence> newEvidenceMap = esMapperSupport.mapEvidenceDTOListToEvdMap(evdRequest.getEvidenceList());
            //map the new evidence set from the request to the current internal evidence set
            esMapperSupport.compareAndMapNewEvidences(vi, newEvidenceMap);
            //save the update evidence set
            evidenceService.saveEvidenceSet(vi.getEvidences());
        }

        CSpecEngineRuleSetRequest cSpecReq = new CSpecEngineRuleSetRequest();
        cSpecReq.setCspecengineId(vi.getCspecRuleSet().getEngineId());

        EvidenceMapperAndSupport esMapperSupport = new EvidenceMapperAndSupport();
        Map<String,Integer> eMap = esMapperSupport.formatEvdSetToCSpecEvdMap(vi.getEvidences());
        cSpecReq.setEvidenceMap(eMap);
        FinalCallDTO newCalculatedFC = cSpecEngineService.callScpecEngine(cSpecReq);
        if(newCalculatedFC == null){
            return new ClassificationResponse("Unable to call the specification engine to get the Final Call value, please try latter.", Constants.NAME_ERROR);
        }

        if(vi.getFinalCall().getId() != newCalculatedFC.getId()) {
            if (vi != null) {
                FinalCall fc = finalCallRepository.getFinalCallById(newCalculatedFC.getId());
                if (fc != null) {
                    vi.setFinalcall(fc);
                }
                try {
                    variantInterpretationRepository.save(vi);
                } catch (Exception e) {
                    logger.error(StackTracePrinter.printStackTrace(e));
                }
            }
        }

        ClassificationEntContent cec = new ClassificationEntContent(vi.getCspecRuleSet().getEngineId(), vi.getVariant().getGene().getGeneId(),
                vi.getCondition().getTerm(), vi.getInheritance().getTerm(), newCalculatedFC.getTerm(), null);

        Classification c = new Classification(cec, vi.getId(), vi.getVariant().getCaid(), DateUtils.dateToStringParser(vi.getCreatedOn()),
                DateUtils.dateToStringParser(new Date()), username);
        ClassificationResponse cr = new ClassificationResponse(c);
        return cr;
    }

    @Override
    public ClassificationResponse removeEvidence(RemoveEvidencesRequest evdRequest, String username){
        FinalCallJPA currentCalculatedFC = variantInterpretationRepository.getCalculatedFinalCallForVI(evdRequest.getClassificationId());
        FinalCallDTO currentCalculatedFCDTO = new FinalCallDTO(currentCalculatedFC.getFinalCall_Id(), currentCalculatedFC.getTerm());

        if(evdRequest.getEvidenceIDs() != null && !evdRequest.getEvidenceIDs().isEmpty() && currentCalculatedFC != null){

            List<Integer> ebdIds = evdRequest.getEvidenceIDs();
            EvidenceListDTO  elDTO = new EvidenceListDTO();
            elDTO.setInterpretationId(evdRequest.getClassificationId());
            elDTO.setCalculatedFinalCall(currentCalculatedFCDTO);
            List<EvidenceDTO> evidenceList = null;
            if(ebdIds != null && !ebdIds.isEmpty()){
                evidenceList = new ArrayList<EvidenceDTO>();
                for(Integer evdId : ebdIds) {
                    evidenceList.add(new EvidenceDTO(evdId));
                }
            }
            elDTO.setEvidenceList(evidenceList);

            if(elDTO != null && elDTO.getEvidenceList() != null){
                evidenceService.deleteEvidenceById(elDTO);
            }
        }

        VariantInterpretation vi = variantInterpretationService.getInterpretationById(evdRequest.getClassificationId());
        if(vi == null){
            return new ClassificationResponse("Unable to get the specification data after the evidence removal.", Constants.NAME_ERROR);
        }
        CSpecEngineRuleSetRequest cSpecReq = new CSpecEngineRuleSetRequest();
        cSpecReq.setCspecengineId(vi.getCspecRuleSet().getEngineId());

        EvidenceMapperAndSupport esMapperSupport = new EvidenceMapperAndSupport();
        Map<String,Integer> eMap = esMapperSupport.formatEvdSetToCSpecEvdMap(vi.getEvidences());
        cSpecReq.setEvidenceMap(eMap);
        FinalCallDTO newCalculatedFC = cSpecEngineService.callScpecEngine(cSpecReq);

        if(newCalculatedFC == null){
            return new ClassificationResponse("Unable to call the specification engine to get the Final Call value, please try latter.", Constants.NAME_ERROR);
        }

        if(currentCalculatedFCDTO.getId() != newCalculatedFC.getId()) {
            if (vi != null) {
                FinalCall fc = finalCallRepository.getFinalCallById(newCalculatedFC.getId());
                if (fc != null) {
                    vi.setFinalcall(fc);
                }
                try {
                    variantInterpretationRepository.save(vi);
                } catch (Exception e) {
                    logger.error(StackTracePrinter.printStackTrace(e));
                }
            }
        }

        ClassificationEntContent cec = new ClassificationEntContent(vi.getCspecRuleSet().getEngineId(), vi.getVariant().getGene().getGeneId(),
                vi.getCondition().getTerm(), vi.getInheritance().getTerm(), newCalculatedFC.getTerm(), null);

        Classification c = new Classification(cec, vi.getId(), vi.getVariant().getCaid(), DateUtils.dateToStringParser(vi.getCreatedOn()),
                DateUtils.dateToStringParser(new Date()), username);
        ClassificationResponse cr = new ClassificationResponse(c);
        return cr;
    }

    @Override
    public AssertionsResponse getClassAssertionsByClassId(ClassByIdRequest classByIdReq, String username){
        Integer classId = classByIdReq.getClassId();
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            return new AssertionsResponse("Unable to determine user!", Constants.NAME_INVALID);
        }

        VariantInterpretation vi = variantInterpretationService.getInterpretationById(classId);
        if (vi == null) {
            return new AssertionsResponse("No classification can be found using the provided ID: " + classId, Constants.NAME_NOT_FOUND);
        }

        AssertionsResponse ar = new AssertionsResponse();
        ar.getData().setCaid(vi.getVariant().getCaid());
        ar.getData().setClassificationId(vi.getId());

        CSpecEngineRuleSetRequest ruleSetRequest = new CSpecEngineRuleSetRequest();
        ruleSetRequest.setCspecengineId(vi.getCspecRuleSet().getEngineId());

        EvidenceMapperAndSupport esMapperSupport = new EvidenceMapperAndSupport();
        ruleSetRequest.setEvidenceMap(esMapperSupport.formatEvdSetToCSpecEvdMap(vi.getEvidences()));

        AssertionsDTO assertionsDTO = cSpecEngineService.getCSpecRuleSet(ruleSetRequest);
        if(assertionsDTO != null && (assertionsDTO.getFailedAssertions() != null || assertionsDTO.getReachedAssertions() != null)){
            AssertionsDTOResponse aDTOResp = new AssertionsDTOResponse();
            aDTOResp.setReachedAssertions(formatAssertionDTOtoResponse(assertionsDTO.getReachedAssertions()));
            aDTOResp.setFailedAssertions(formatAssertionDTOtoResponse(assertionsDTO.getFailedAssertions()));
            ar.getData().setAssertions(aDTOResp);
        }
        return ar;
    }

    private ClassificationsResponse convertVIListToClassReponse(List<VariantInterpretation> viBasicDtoList, String caid, User user, boolean useHighDetail){
        ClassificationsResponse cr = new ClassificationsResponse();
        cr.getData().setVariant(caid);
        for(VariantInterpretation vi : viBasicDtoList){
            cr.getData().addClassification(mapVItoClassification(vi, user.getUsername(), useHighDetail));
        }
        return cr;
    }

    private Classification mapVItoClassification(VariantInterpretation vi, String username, boolean useHighDetail){
        String dfcValue = null;
        if (vi.getDeterminedFinalCall() != null) {
            dfcValue = vi.getDeterminedFinalCall().getTerm();
        }

        String rgName = null;
        Gene g = vi.getVariant().getGene();
        if (g != null) {
            rgName = g.getGeneId();
        }

        List<EvidenceR> evidences = null;
        if(useHighDetail){
            EvidenceMapperAndSupport esMapperSupport = new EvidenceMapperAndSupport();
            evidences = esMapperSupport.mapEvidenceSetToEvidenceRList(vi.getEvidences());
        }
        ClassificationEntContent cec = new ClassificationEntContent(vi.getCspecRuleSet().getEngineId() , rgName, vi.getCondition().getTerm(),
                vi.getInheritance().getTerm(), vi.getFinalCall().getTerm(), dfcValue, evidences);

        return new Classification(cec, vi.getId(), vi.getVariant().getCaid(),
                DateUtils.dateToStringParser(vi.getCreatedOn()),
                DateUtils.dateToStringParser(vi.getModifiedOn()), username);
    }

    private VarInterpSaveUpdateEvidenceDocRequest mapToVarInterpSaveUpdateEvidenceDocRequest(CreateUpdateClassRequest ccRequest){
        VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq = new VarInterpSaveUpdateEvidenceDocRequest();
        viSaveEvdUpdateReq.setCaid(ccRequest.getCaid());
        if(ccRequest.getClassificationId() != null){
            viSaveEvdUpdateReq.setInterpretationId(ccRequest.getClassificationId());
        }
        viSaveEvdUpdateReq.setGeneName(ccRequest.getGene());

        if(ccRequest.getDisease() != null){
            if(ccRequest.getDisease().getId() != null){
                viSaveEvdUpdateReq.setConditionId(ccRequest.getDisease().getId());
            }
            if(ccRequest.getDisease().getTerm() != null) {
                viSaveEvdUpdateReq.setCondition(ccRequest.getDisease().getTerm());
            }
        }

        if(ccRequest.getModeOfInheritance() != null) {
            if(ccRequest.getModeOfInheritance().getId() != null){
                viSaveEvdUpdateReq.setInheritanceId(ccRequest.getModeOfInheritance().getId());
            }
            if(ccRequest.getModeOfInheritance().getTerm() != null){
                viSaveEvdUpdateReq.setInheritance(ccRequest.getModeOfInheritance().getTerm());
            }
        }

        viSaveEvdUpdateReq.setCspecengineId(ccRequest.getCspecId());
        return viSaveEvdUpdateReq;
    }

    private EvidenceListDTO mapToEvidenceListDTO(VariantInterpretationSaveResponse viSaveUpdateResp,
                                                 List<EvidenceDTO> evidenceDTOList){
        EvidenceListDTO elDTO = new EvidenceListDTO();
        elDTO.setInterpretationId(viSaveUpdateResp.getInterpretationId());
        elDTO.setCalculatedFinalCall(viSaveUpdateResp.getCalculatedFinalCall());
        elDTO.setEvidenceList(evidenceDTOList);
        return elDTO;
    }

    private Map<String, ArrayList<RuleConditionResponse>> formatAssertionDTOtoResponse(Map<String, ArrayList<RuleConditionDTO>> assertions){
        if(assertions == null || assertions.size() == 0){
            return new HashMap<String, ArrayList<RuleConditionResponse>>();
        }

        Map<String, ArrayList<RuleConditionResponse>> responseMap = new HashMap<String, ArrayList<RuleConditionResponse>>();
        Iterator<String> iter = assertions.keySet().iterator();
        while(iter.hasNext()){
            String key = iter.next();

            ArrayList<RuleConditionResponse> rcResponse = null;

            ArrayList<RuleConditionDTO> rcDTOs = assertions.get(key);
            if(rcDTOs != null && !rcDTOs.isEmpty()){
                rcResponse = new ArrayList<RuleConditionResponse>();
                for(RuleConditionDTO rcDTO : rcDTOs){
                    rcResponse.add(new RuleConditionResponse(rcDTO.getLabel(), rcDTO.getConditionsLeft()));
                }
            }
            if(rcResponse != null) {
                responseMap.put(key, rcResponse);
            }
        }
        return responseMap;
    }
}
