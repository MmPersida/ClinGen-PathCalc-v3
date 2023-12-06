package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.config.AuthentificationManager;
import com.persida.pathogenicity_calculator.dto.*;

import com.persida.pathogenicity_calculator.repository.*;
import com.persida.pathogenicity_calculator.repository.entity.*;
import com.persida.pathogenicity_calculator.utils.EvidenceMapperAndSupport;
import com.persida.pathogenicity_calculator.utils.StackTracePrinter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VariantInterpretationServiceImpl implements VariantInterpretationService{

    private static Logger logger = Logger.getLogger(VariantInterpretationServiceImpl.class);

    @Autowired
    private VariantRepository variantRepository;
    @Autowired
    private GeneRepository geneRepository;
    @Autowired
    private InheritanceRepository inheritanceRepository;
    @Autowired
    private FinalCallRepository finalCallRepository;
    @Autowired
    private ConditionRepository conditionRepository;
    @Autowired
    private VariantInterpretationRepository variantInterpretationRepository;
    @Autowired
    private EvidenceRepository evidenceRepository;
    @Autowired
    private CSpecRuleSetRepository cSpecRuleSetRepository;

    @Autowired
    private AuthentificationManager authentificationManager;

    @Autowired
    private UserService userService;
    @Autowired
    private EvidenceService evidenceService;

    @Override
    public VariantInterpretationDTO loadInterpretation(VariantInterpretationIDRequest interpretationIDRequest) {
        //get VI based on te unique ID
        VariantInterpretation vi = variantInterpretationRepository.getVariantInterpretationById(interpretationIDRequest.getInterpretationId());
        if(vi != null && vi.getId() != null && vi.getId() > 0){
            return convertVariantInterpretationEntityToDTO(vi);
        }else{
            VariantInterpretationDTO viDTO = new VariantInterpretationDTO();
            viDTO.setMessage("Unable to find Variant Interpretation with ID: "+interpretationIDRequest.getInterpretationId());
            return viDTO;
        }
    }

    @Override
    public VariantInterpretationSaveResponse saveNewInterpretation(VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq){
        Variant var = variantRepository.getVariantByCAID(viSaveEvdUpdateReq.getCaid());
        if(var == null){
            Gene g = null;
            Optional<Gene> optGene = geneRepository.findById(viSaveEvdUpdateReq.getGeneName());
            if(optGene != null && optGene.isPresent()){
                g = optGene.get();
            }else{
                g = new Gene(viSaveEvdUpdateReq.getGeneName());
                geneRepository.save(g);
            }
            var = new Variant(viSaveEvdUpdateReq.getCaid(), g);
            variantRepository.save(var);
            logger.info("Saved new Variant, caid: "+var.getCaid());
        }

        User u = getCurrentUserEntityObj();
        if(u == null){
            return null;
        }

        Condition con = null;
        Inheritance inher = null;
        FinalCall fc = null;
        CSpecRuleSet cspec = null;
        if(viSaveEvdUpdateReq.getConditionId() != null && viSaveEvdUpdateReq.getConditionId() > 0){
            con = conditionRepository.getConditionById(viSaveEvdUpdateReq.getConditionId());
        }else{
            con = conditionRepository.getConditionByName(viSaveEvdUpdateReq.getCondition());
        }

        if(viSaveEvdUpdateReq.getInheritanceId() != null && viSaveEvdUpdateReq.getInheritanceId() > 0){
            inher = inheritanceRepository.getInheritanceById(viSaveEvdUpdateReq.getInheritanceId());
        }else{
            inher = inheritanceRepository.getInheritanceByName(viSaveEvdUpdateReq.getInheritance());
        }

        if(viSaveEvdUpdateReq.getCspecengineId() != null && !viSaveEvdUpdateReq.getCspecengineId().equals("")){
            cspec = cSpecRuleSetRepository.getCSpecRuleSetById(viSaveEvdUpdateReq.getCspecengineId());
        }

        fc = finalCallRepository.getFinalCallInsufficientEvidence();

        VariantInterpretation vi = new VariantInterpretation(u, var, null, con, fc, inher, cspec);
        try{
            variantInterpretationRepository.save(vi);
        }catch(Exception e){
            logger.error(StackTracePrinter.printStackTrace(e));
            return new VariantInterpretationSaveResponse(vi.getId(), "Unable to save new variant interpretation!");
        }
        return new VariantInterpretationSaveResponse(vi.getId());
    }

    @Override
    public VariantInterpretationSaveResponse deleteInterpretation(VariantInterpretationIDRequest interpretationIDRequest){
        VariantInterpretation vi = variantInterpretationRepository.getVariantInterpretationById(interpretationIDRequest.getInterpretationId());
        if(vi != null){
            try{
                variantInterpretationRepository.delete(vi);
            }catch(Exception e){
                logger.error(StackTracePrinter.printStackTrace(e));
                return new VariantInterpretationSaveResponse(vi.getId(), "Unable to delete Variant Interpretation with ID: "+interpretationIDRequest.getInterpretationId());
            }
        }
        return new VariantInterpretationSaveResponse(vi.getId());
    }

    @Override
    public VariantInterpretationSaveResponse updateEvidenceDocAndEngine(VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq){
        Condition con = null;
        Inheritance inher = null;
        CSpecRuleSet cspec = null;

        if(viSaveEvdUpdateReq.getConditionId() != null && viSaveEvdUpdateReq.getConditionId() > 0){
            con = conditionRepository.getConditionById(viSaveEvdUpdateReq.getConditionId());
        }else{
            con = conditionRepository.getConditionByName(viSaveEvdUpdateReq.getCondition());
        }

        if(viSaveEvdUpdateReq.getInheritanceId() != null && viSaveEvdUpdateReq.getInheritanceId() > 0){
            inher = inheritanceRepository.getInheritanceById(viSaveEvdUpdateReq.getInheritanceId());
        }else{
            inher = inheritanceRepository.getInheritanceByName(viSaveEvdUpdateReq.getInheritance());
        }

        if(viSaveEvdUpdateReq.getCspecengineId() != null && !viSaveEvdUpdateReq.getCspecengineId().equals("")){
            cspec = cSpecRuleSetRepository.getCSpecRuleSetById(viSaveEvdUpdateReq.getCspecengineId());
        }

        VariantInterpretation vi = variantInterpretationRepository.getVariantInterpretationById(viSaveEvdUpdateReq.getInterpretationId());
        if(vi != null){
            if(con != null){
                vi.setCondition(con);
            }
            if(inher != null){
                vi.setInheritance(inher);
            }
            if(cspec != null){
                vi.setCspecRuleSet(cspec);
            }

            try{
                variantInterpretationRepository.save(vi);
            }catch(Exception e){
                logger.error(StackTracePrinter.printStackTrace(e));
                return new VariantInterpretationSaveResponse(vi.getId(), "Unable to save the updated Condition or Mode Of Inheritance!");
            }
        }else{
            return new VariantInterpretationSaveResponse(vi.getId(), "Unable to save the updated Condition or Mode Of Inheritance, cannot find a Variant Interpretation with ID: "+viSaveEvdUpdateReq.getInterpretationId());
        }
        return new VariantInterpretationSaveResponse(vi.getId());
    }

    @Override
    public VariantInterpretationSaveResponse updateFinalCall(VarInterpUpdateFinalCallRequest viUpdateFCReq){
        VariantInterpretation vi = variantInterpretationRepository.getVariantInterpretationById(viUpdateFCReq.getInterpretationId());
        if(vi != null){
            FinalCall fc = finalCallRepository.getFinalCallByName(viUpdateFCReq.getFinalCall());
            if(fc != null){
                vi.setFinalcall(fc);
            }

            try{
                variantInterpretationRepository.save(vi);
            }catch(Exception e){
                logger.error(StackTracePrinter.printStackTrace(e));
                return new VariantInterpretationSaveResponse(vi.getId(), "Unable to save the updated Final Call!");
            }
        }else{
            return new VariantInterpretationSaveResponse(vi.getId(), "Unable to save the updated Final Call, cannot find a Variant Interpretation with ID: "+viUpdateFCReq.getInterpretationId());
        }
        return new VariantInterpretationSaveResponse(vi.getId());
    }

    @Override
    public List<VIBasicDTO> getVIBasicDataForCaid(String variantCAID){
        CustomUserDetails cud = getCurrentUserCustomDetails();
        if(cud == null){
            return null;
        }

        List<VariantInterpretation> viList = variantInterpretationRepository.getVariantInterpretationsByCAID(cud.getUserId(), variantCAID);
        return mapVIListToVIBasicDTOList(viList);
    }

    @Override
    public  List<VIBasicDTO> searchInterpByCaidEvidenceDoc(VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq){
        User u = getCurrentUserEntityObj();
        if(u == null){
            return null;
        }

        Condition con = null;
        Inheritance inher = null;
        if(viSaveEvdUpdateReq.getConditionId() != null && viSaveEvdUpdateReq.getConditionId() > 0){
            con = conditionRepository.getConditionById(viSaveEvdUpdateReq.getConditionId());
        }else{
            con = conditionRepository.getConditionByName(viSaveEvdUpdateReq.getCondition());
        }
        if(viSaveEvdUpdateReq.getInheritanceId() != null && viSaveEvdUpdateReq.getInheritanceId() > 0){
            inher = inheritanceRepository.getInheritanceById(viSaveEvdUpdateReq.getInheritanceId());
        }else{
            inher = inheritanceRepository.getInheritanceByName(viSaveEvdUpdateReq.getInheritance());
        }

        List<VariantInterpretation> viList = variantInterpretationRepository.searchInterpretationsByCaidEvdcDocEngineId(
                u.getId(), viSaveEvdUpdateReq.getCaid(), con.getCondition_id(), inher.getId(), viSaveEvdUpdateReq.getCspecengineId());
        return mapVIListToVIBasicDTOList(viList);
    }

    @Override
    public String loadViDescription(VariantInterpretationIDRequest interpretationIDRequest){
        VariantInterpretation vi = variantInterpretationRepository.getVariantInterpretationById(interpretationIDRequest.getInterpretationId());
        if(vi != null){
            return vi.getViDescription();
        }
        return null;
    }

    @Override
    public String saveEditVIDescription(VariantDescriptionRequest interpretationIDRequest){
        VariantInterpretation vi = variantInterpretationRepository.getVariantInterpretationById(interpretationIDRequest.getInterpretationId());
        if(vi != null){
            vi.setViDescription(interpretationIDRequest.getViDescription());
            try{
                variantInterpretationRepository.save(vi);
            }catch(Exception e){
                logger.error(StackTracePrinter.printStackTrace(e));
                return "Unable to save the edited interpretation description!";
            }
        }
        return "OK";
    }

    private List<VIBasicDTO> mapVIListToVIBasicDTOList(List<VariantInterpretation> viList){
        List<VIBasicDTO> viBasicDTOList = new ArrayList<VIBasicDTO>();
        if(viList == null || viList.size() == 0){
            return viBasicDTOList;
        }

        for(VariantInterpretation vi : viList){
            viBasicDTOList.add(new VIBasicDTO(vi.getVariant().getCaid(),
                    vi.getId(),
                    vi.getCondition().getTerm(),
                    vi.getInheritance().getTerm(),
                    vi.getFinalCall().getTerm(),
                    vi.getCreatedOn(),
                    vi.getModifiedOn()));
        }
        return viBasicDTOList;
    }

    private VariantInterpretationDTO convertVariantInterpretationEntityToDTO(VariantInterpretation vi) {
        EvidenceMapperAndSupport esMapperSupport = new EvidenceMapperAndSupport();
        List<EvidenceDTO> resultEvidenceList = esMapperSupport.mapEvidenceSetToDTO(vi.getEvidences());

        VariantInterpretationDTO viTDO = new VariantInterpretationDTO();
        viTDO.setInterpretationId(vi.getId());
        viTDO.setCaid(vi.getVariant().getCaid());
        viTDO.setConditionId(vi.getCondition().getCondition_id());
        viTDO.setCondition(vi.getCondition().getTerm());
        viTDO.setInheritanceId(vi.getInheritance().getId());
        viTDO.setInheritance(vi.getInheritance().getTerm());
        viTDO.setEvidenceList(resultEvidenceList);
        viTDO.setFinalCallId(vi.getFinalCall().getId());
        viTDO.setFinalCall(vi.getFinalCall().getTerm());

        CSpecRuleSet csrs = vi.getCspecRuleSet();
        viTDO.setCspecEngineDTO(new CSpecEngineDTO(csrs.getEngineId(), csrs.getEngineSummary(), csrs.getOrganizationName(),
                                                    csrs.getRuleSetId(), csrs.getRuleSetURL(), null));
        viTDO.setViDescription(vi.getViDescription());
        return viTDO;
    }

    private CustomUserDetails getCurrentUserCustomDetails(){
        Authentication authenticate  = authentificationManager.getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authenticate.getPrincipal();
        if(customUserDetails == null){
            logger.error("Unable to get current username!");
            return null;
        }
        return customUserDetails;
    }

    private User getCurrentUserEntityObj(){
        CustomUserDetails cud = getCurrentUserCustomDetails();
        if(cud == null){
            return null;
        }

        User u = userService.getUserById(cud.getUserId());
        if(u != null){
            return u;
        }else {
            logger.error("Unable to return user data for username: "+cud.getUsername()+"("+cud.getUserId()+")");
            return null;
        }
    }
}
