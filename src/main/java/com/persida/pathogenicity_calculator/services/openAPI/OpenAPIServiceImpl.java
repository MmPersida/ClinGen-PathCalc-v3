package com.persida.pathogenicity_calculator.services.openAPI;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.*;
import com.persida.pathogenicity_calculator.config.JWTutils;
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
import lombok.Data;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class OpenAPIServiceImpl implements OpenAPIService {
    static Logger logger = Logger.getLogger(CalculatorServiceImpl.class);

    @Autowired
    private JWTutils jwtUtils;
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

    private HashMap<String,TagGroup> tagToGroupMap = new HashMap<String,TagGroup>();

    @Data
    private class TagGroup{
        private String type;
        private String modifier;
        public TagGroup(String type, String modifier){
            this.type = type;
            this.modifier = modifier;
        }
    }

    @PostConstruct
    public void prepareEvidenceTagMap(){
        tagToGroupMap.put("BP1", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("BP2", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("BP3", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("BP4", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("BP5", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("BP6", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("BP7", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("BS1", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_STRONG));
        tagToGroupMap.put("BS2", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_STRONG));
        tagToGroupMap.put("BS3", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_STRONG));
        tagToGroupMap.put("BS4", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_STRONG));
        tagToGroupMap.put("BA1", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_STAND_ALONE));
        tagToGroupMap.put("PP1", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("PP2", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("PP3", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("PP4", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("PP5", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("PM1", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_MODERATE));
        tagToGroupMap.put("PM2", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_MODERATE));
        tagToGroupMap.put("PM3", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_MODERATE));
        tagToGroupMap.put("PM4", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_MODERATE));
        tagToGroupMap.put("PM5", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_MODERATE));
        tagToGroupMap.put("PM6", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_MODERATE));
        tagToGroupMap.put("PS1", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_STRONG));
        tagToGroupMap.put("PS2", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_STRONG));
        tagToGroupMap.put("PS3", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_STRONG));
        tagToGroupMap.put("PS4", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_STRONG));
        tagToGroupMap.put("PVS1", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_VERY_STRONG));
    }

    @Override
    public SRVCResponse srvc() {
        return new SRVCResponse();
    }

    @Override
    public TokenResponse tokenRequest(RequestAuthData requestAuthData) {
        String tokenValue = jwtUtils.getTokenFromAuth(requestAuthData.getUsername(), requestAuthData.getPass());

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
    public ClassificationsResponse allClassificationsForUser(String username) {
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            return new ClassificationsResponse("Unable to determine user!", Constants.NAME_INVALID);
        }

        List<VIBasicDTO> viBasicDtoList = variantInterpretationService.getAllInterpretedVariantsByUser(user.getId());
        if (viBasicDtoList == null || viBasicDtoList.isEmpty()) {
            return new ClassificationsResponse("No classifications can be found for this user!", Constants.NAME_NOT_FOUND);
        }
        return convertFromViDTOtoCLassReponse(viBasicDtoList, null, user);
    }

    @Override
    public ClassificationsResponse classificationsForVariant(ClassByVariantRequest classRequest, String username) {
        String caid = classRequest.getCaid();
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            return new ClassificationsResponse("Unable to determine user!", Constants.NAME_INVALID);
        }

        List<VIBasicDTO> viBasicDtoList = variantInterpretationService.getUserVIBasicDataForCaid(user.getId(), caid);
        if (viBasicDtoList == null || viBasicDtoList.isEmpty()) {
            return new ClassificationsResponse("No classifications can be found using the provided CAID: " + caid, Constants.NAME_NOT_FOUND);
        }
        return convertFromViDTOtoCLassReponse(viBasicDtoList, caid, user);
    }

    @Override
    public ClassificationResponse classificationById(ClassByIdRequest classByIdReq, String username) {
        Integer classId = classByIdReq.getClassId();
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            return new ClassificationResponse("Unable to determine user!", Constants.NAME_INVALID);
        }

        VariantInterpretation vi = variantInterpretationService.getInterpretationById(classId);
        if (vi == null) {
            return new ClassificationResponse("No classification can be found using the provided ID: " + classId, Constants.NAME_NOT_FOUND);
        }

        String dfcValue = null;
        if (vi.getDeterminedFinalCall() != null) {
            dfcValue = vi.getDeterminedFinalCall().getTerm();
        }

        String rgName = null;
        Gene g = vi.getVariant().getGene();
        if (g != null) {
            rgName = g.getGeneId();
        }

        EvidenceMapperAndSupport esMapperSupport = new EvidenceMapperAndSupport();
        List<EvidenceR> evidences = esMapperSupport.mapEvidenceToEvidenceR(vi.getEvidences());

        ClassificationEntContent cec = new ClassificationEntContent(vi.getCspecRuleSet().getEngineId() , rgName, vi.getCondition().getTerm(),
                vi.getInheritance().getTerm(), vi.getFinalCall().getTerm(), dfcValue, evidences);

        Classification c = new Classification(cec, classId,
                DateUtils.dateToStringParser(vi.getCreatedOn()),
                DateUtils.dateToStringParser(vi.getModifiedOn()), username);
        ClassificationResponse cr = new ClassificationResponse(c);
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
    public ClassificationResponse createClassification(CreateUpdateClassWithEvidenceRequest ccRequest, String username) {
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            return new ClassificationResponse("Unable to determine user!", Constants.NAME_INVALID);
        }

        if(ccRequest == null){
            return new ClassificationResponse("Input data missing, formatted improperly or null.", Constants.NAME_INVALID);
        }

        VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq = mapToVarInterpSaveUpdateEvidenceDocRequest(ccRequest);
        VariantInterpretationSaveResponse viSaveUpdateResp = variantInterpretationService.saveNewInterpretation(viSaveEvdUpdateReq, user);

        if(ccRequest.getEvidenceTags() != null && !ccRequest.getEvidenceTags().isEmpty()) {
            EvidenceListDTO elDTO = mapToEvidenceListDTO(viSaveUpdateResp, ccRequest.getEvidenceTags());
            evidenceService.saveNewEvidence(elDTO);
        }

        String dfcValue = null;
        String rgName = ccRequest.getGene();

        ClassificationEntContent cec = new ClassificationEntContent(viSaveUpdateResp.getCspecengineId(), rgName, viSaveEvdUpdateReq.getCondition(),
                viSaveEvdUpdateReq.getInheritance(), viSaveUpdateResp.getCalculatedFinalCall().getTerm(), dfcValue);

        Classification c = new Classification(cec, viSaveUpdateResp.getInterpretationId(),
                DateUtils.dateToStringParser(new Date()), null, username);
        ClassificationResponse cr = new ClassificationResponse(c);
        return cr;
    }

    @Override
    public ClassificationResponse updateClassification(CreateUpdateClassWithEvidenceRequest ucRequest, String username){
        if(ucRequest == null){
            return new ClassificationResponse("Input data missing, formatted improperly or null.", Constants.NAME_INVALID);
        }

        VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq = mapToVarInterpSaveUpdateEvidenceDocRequest(ucRequest);
        VariantInterpretationSaveResponse viSaveUpdateResp = variantInterpretationService.updateEvidenceDocAndEngine(viSaveEvdUpdateReq);

        FinalCallDTO calculatedFC = null;
        if(ucRequest.getEvidenceTags() != null && !ucRequest.getEvidenceTags().isEmpty()){
            EvidenceListDTO elDTO = mapToEvidenceListDTO(viSaveUpdateResp, ucRequest.getEvidenceTags());
            evidenceService.saveNewEvidence(elDTO);

            CSpecEngineRuleSetRequest cSpecReq = new CSpecEngineRuleSetRequest();
            cSpecReq.setCspecengineId(viSaveUpdateResp.getCspecengineId());
            Map<String,Integer> eMap = formatEvidencesToMap(ucRequest.getEvidenceTags());
            cSpecReq.setEvidenceMap(eMap);
            calculatedFC = cSpecEngineService.callScpecEngine(cSpecReq);
        }
        if(calculatedFC == null){
            return new ClassificationResponse("Unable to call the specification engine to get the Final Call value, please try latter.", Constants.NAME_ERROR);
        }

        if(viSaveUpdateResp.getCalculatedFinalCall().getId() != calculatedFC.getId()){
            variantInterpretationService.updateCalculatedFinalCall(new VarInterpUpdateFinalCallRequest(ucRequest.getClassificationId(), calculatedFC.getId()));
        }

        ClassificationEntContent cec = new ClassificationEntContent(viSaveUpdateResp.getCspecengineId(), ucRequest.getGene(),
                viSaveEvdUpdateReq.getCondition(), viSaveEvdUpdateReq.getInheritance(), calculatedFC.getTerm(), null);

        Classification c = new Classification(cec, viSaveUpdateResp.getInterpretationId(),
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

        if(evdRequest.getEvidences() != null && !evdRequest.getEvidences().isEmpty()){
            EvidenceMapperAndSupport esMapperSupport = new EvidenceMapperAndSupport();
            List<EvidenceDTO> evidenceDTOList = mapFromEvdTagReqToEvdDTo(evdRequest.getEvidences());
            HashMap<String, Evidence> newEvidenceMap = esMapperSupport.mapEvidenceDTOListToEvdMap(evidenceDTOList);
            //map the new evidence set from the request to the current internal evidence set
            esMapperSupport.compareAndMapNewEvidences(vi, newEvidenceMap);
            //save the update evidence set
            evidenceService.saveEvidenceSet(vi.getEvidences());
        }

        CSpecEngineRuleSetRequest cSpecReq = new CSpecEngineRuleSetRequest();
        cSpecReq.setCspecengineId(vi.getCspecRuleSet().getEngineId());
        List<EvideneTagRequest> evidenceTags = formatFromEvidenceToEvidenceTagsList(vi.getEvidences());
        Map<String,Integer> eMap = formatEvidencesToMap(evidenceTags);
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

        Classification c = new Classification(cec, vi.getId(), DateUtils.dateToStringParser(vi.getCreatedOn()),
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
        List<EvideneTagRequest> evidenceTags = formatFromEvidenceToEvidenceTagsList(vi.getEvidences());
        Map<String,Integer> eMap = formatEvidencesToMap(evidenceTags);
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

        Classification c = new Classification(cec, vi.getId(), DateUtils.dateToStringParser(vi.getCreatedOn()),
                DateUtils.dateToStringParser(new Date()), username);
        ClassificationResponse cr = new ClassificationResponse(c);
        return cr;
    }

    private ClassificationsResponse convertFromViDTOtoCLassReponse(List<VIBasicDTO> viBasicDtoList, String caid, User user){
        ClassificationsResponse cr = new ClassificationsResponse();
        cr.getData().setVariant(caid);
        for(VIBasicDTO viDTO : viBasicDtoList){
            String dfcValue = null;
            if (viDTO.getDeterminedFinalCall() != null) {
                dfcValue = viDTO.getDeterminedFinalCall().getTerm();
            }

            String rgName = null;
            if (viDTO.getRelatedGene() != null) {
                rgName = viDTO.getRelatedGene().getGeneName();
            }

            ClassificationEntContent cec = new ClassificationEntContent(viDTO.getCspecengineId(), rgName, viDTO.getCondition(), viDTO.getInheritance(),
                    viDTO.getCalculatedFinalCall().getTerm(), dfcValue);
            Classification c = new Classification(cec, viDTO.getInterpretationId(),
                    DateUtils.dateToStringParser(viDTO.getCreateOn()),
                    DateUtils.dateToStringParser(viDTO.getModifiedOn()), user.getUsername());
            cr.getData().addClassification(c);
        }
        return cr;
    }

    private VarInterpSaveUpdateEvidenceDocRequest mapToVarInterpSaveUpdateEvidenceDocRequest(CreateUpdateClassWithEvidenceRequest ccRequest){
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
                                                 List<EvideneTagRequest> evidenceTags){
        EvidenceListDTO elDTO = new EvidenceListDTO();
        elDTO.setInterpretationId(viSaveUpdateResp.getInterpretationId());
        elDTO.setCalculatedFinalCall(viSaveUpdateResp.getCalculatedFinalCall());
        elDTO.setEvidenceList(mapFromEvdTagReqToEvdDTo(evidenceTags));
        return elDTO;
    }

    private List<EvidenceDTO> mapFromEvdTagReqToEvdDTo(List<EvideneTagRequest> evidenceTags){
        List<EvidenceDTO> evidenceList = null;
        if(evidenceTags != null && !evidenceTags.isEmpty()){
            evidenceList = new ArrayList<EvidenceDTO>();
            for(EvideneTagRequest etR : evidenceTags) {
                if(etR.getSummary() == null){
                    evidenceList.add(new EvidenceDTO(etR.getType(), etR.getModifier()));
                }else{
                    evidenceList.add(new EvidenceDTO(etR.getType(), etR.getModifier(), etR.getSummary()));
                }
            }
        }
        return evidenceList;
    }

    private  List<EvideneTagRequest> formatFromEvidenceToEvidenceTagsList(Set<Evidence> evidenceSet){
        List<EvideneTagRequest> eList = new ArrayList<EvideneTagRequest>();
        for(Evidence evd : evidenceSet){
            eList.add(new EvideneTagRequest(evd.getEvdType(), evd.getEvdModifier()));
        }
        return eList;
    }

    private Map<String,Integer> formatEvidencesToMap(List<EvideneTagRequest> evidenceTags){
        HashMap<String,Integer> evidenceMap = new HashMap<String,Integer>();

        for(EvideneTagRequest etr : evidenceTags){
            String tagCategory = null;

            TagGroup tg = tagToGroupMap.get(etr.getType());
            tagCategory = tg.type;

            if(etr.getModifier() != null && !etr.getModifier().isEmpty()){
                tagCategory = tagCategory +"."+etr.getModifier();
            }else{
                tagCategory = tagCategory +"."+tg.getModifier();
            }

            if(evidenceMap.get(tagCategory) == null){
                evidenceMap.put(tagCategory, 1);
            }else{
               Integer n = evidenceMap.get(tagCategory);
               evidenceMap.put(tagCategory, (n+1));
            }
        }
        return evidenceMap;
    }
}
