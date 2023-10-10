package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.config.AuthentificationManager;
import com.persida.pathogenicity_calculator.dto.*;

import com.persida.pathogenicity_calculator.repository.*;
import com.persida.pathogenicity_calculator.repository.entity.*;
import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class VariantInterpretationServiceImpl implements VariantInterpretationService{

    private static Logger logger = Logger.getLogger(VariantInterpretationServiceImpl.class);

    @Autowired
    private VariantRepository variantRepository;
    @Autowired
    private InheritanceRepository inheritanceRepository;
    @Autowired
    private FinalCallRepository finalCallRepository;
    @Autowired
    private ConditionRepository conditionRepository;
    @Autowired
    private VariantInterpretationRepository variantInterpretationRepository;
    @Autowired
    private EvidenceSetRepository evidenceSetRepository;

    @Autowired
    private AuthentificationManager authentificationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public VariantInterpretationSaveResponse saveNewInterpretation(VariantInterpretationDTO saveInterpretationDTO){
        Variant var = variantRepository.getVariantByCAID(saveInterpretationDTO.getCaid());
        if(var == null){
            var = new Variant(saveInterpretationDTO.getCaid());
            variantRepository.save(var);
            logger.info("Saved new Variant, caid: "+var.getCaid());
        }

        User u = getCurrentUserEntityObj();
        if(u == null){
            return null;
        }

        EvidenceSet es = null;
        Condition con = null;
        FinalCall fc = null;
        Inheritance inher = null;

        if(saveInterpretationDTO.getConditionId() != null && saveInterpretationDTO.getConditionId() > 0){
            con = conditionRepository.getConditionById(saveInterpretationDTO.getConditionId());
        }else{
            con = conditionRepository.getConditionByName(saveInterpretationDTO.getCondition());
        }

        if(saveInterpretationDTO.getFinalCallId() != null && saveInterpretationDTO.getFinalCallId() > 0){
            fc = finalCallRepository.getFinalCallById(saveInterpretationDTO.getFinalCallId());
        }else{
            fc = finalCallRepository.getFinalCallByName(saveInterpretationDTO.getFinalCall());
        }

        if(saveInterpretationDTO.getInheritanceId() != null && saveInterpretationDTO.getInheritanceId() > 0){
            inher = inheritanceRepository.getInheritanceById(saveInterpretationDTO.getInheritanceId());
        }else{
            inher = inheritanceRepository.getInheritanceByName(saveInterpretationDTO.getInheritance());
        }

        VariantInterpretation interpretation = null;
        if(saveInterpretationDTO.getInterpretationId() == null || saveInterpretationDTO.getInterpretationId() == 0){
            //this is a new Interpretation
            es = new EvidenceSet();
            modelMapper.map(saveInterpretationDTO.getEvidenceSet(), es);
            interpretation = new VariantInterpretation(u, var, es, con, fc, inher);
        }else{
            //use the var. interpretation to get its evidence set for update
            interpretation = variantInterpretationRepository.getVariantInterpretationById(saveInterpretationDTO.getInterpretationId());
            if(interpretation == null){
                //juts in case!
                interpretation = variantInterpretationRepository.getVariantInterpretationByCAID(u.getId(), saveInterpretationDTO.getCaid());
            }
            if(interpretation == null){
                logger.error("Error: Unable to find the variant interpretation for caid: "+saveInterpretationDTO.getCaid());
                return null;
            }

            interpretation.setCondition(con);
            interpretation.setFinalcall(fc);
            interpretation.setInheritance(inher);
            es = interpretation.getEvidenceset();
            modelMapper.map(saveInterpretationDTO.getEvidenceSet(), es);
        }

        evidenceSetRepository.save(es);
        variantInterpretationRepository.save(interpretation);
        return new VariantInterpretationSaveResponse(interpretation.getId());
    }

    @Override
    public VariantInterpretationDTO loadInterpretation(VariantInterpretationLoadRequest loadInterpretationRequest) {
        CustomUserDetails cud = getCurrentUserCustomDetails();
        if(cud == null){
            return null;
        }

        VariantInterpretation vi = variantInterpretationRepository.getVariantInterpretationByCAID(cud.getUserId(), loadInterpretationRequest.getCaid());
        if(vi != null && vi.getId() != null && vi.getId() > 0){
            return convertVariantInterpretationEntityToDTO(vi);
        }else{
            return null;
        }
    }

    @Override
    public VariantInterpretationSaveResponse updateEvidenceDoc(EvidenceDocUpdateEvent edue){
        Variant var = variantRepository.getVariantByCAID(edue.getCaid());
        if(var == null){
            var = new Variant(edue.getCaid());
            variantRepository.save(var);
            logger.info("Saved new Variant, caid: "+var.getCaid());
        }

        User u = getCurrentUserEntityObj();
        if(u == null){
            return null;
        }

        Condition con = null;
        Inheritance inher = null;

        if(edue.getConditionId() != null && edue.getConditionId() > 0){
            con = conditionRepository.getConditionById(edue.getConditionId());
        }else{
            con = conditionRepository.getConditionByName(edue.getCondition());
        }
        if(edue.getInheritanceId() != null && edue.getInheritanceId() > 0){
            inher = inheritanceRepository.getInheritanceById(edue.getInheritanceId());
        }else{
            inher = inheritanceRepository.getInheritanceByName(edue.getInheritance());
        }

        VariantInterpretation vi = variantInterpretationRepository.getVariantInterpretationByCAID(u.getId(), edue.getCaid());
        if(vi != null){
            vi.setCondition(con);
            vi.setInheritance(inher);
            variantInterpretationRepository.save(vi);
        }else{
            EvidenceSet es = new EvidenceSet();
            evidenceSetRepository.save(es);
            vi = new VariantInterpretation(u, var, es, con, finalCallRepository.getFinalCallInsufficient(), inher);
            variantInterpretationRepository.save(vi);
            return new VariantInterpretationSaveResponse(vi.getId());
        }
        return new VariantInterpretationSaveResponse(vi.getId());
    }

    @Override
    public String getFinalCallForCaID(String variantCID){
        CustomUserDetails cud = getCurrentUserCustomDetails();
        if(cud == null){
            return null;
        }

        VariantInterpretation vi = variantInterpretationRepository.getVariantInterpretationByCAID(cud.getUserId(), variantCID);
        if(vi != null && vi.getId() != null && vi.getId() > 0){
            return vi.getFinalCall().getTerm();
        }else{
            return null;
        }
    }

    private VariantInterpretationDTO convertVariantInterpretationEntityToDTO(VariantInterpretation vi) {
        EvidenceSetDTO resultEvidenceSet = new EvidenceSetDTO();
        modelMapper.map(vi.getEvidenceset(), resultEvidenceSet);

        VariantInterpretationDTO viTDO = new VariantInterpretationDTO();

        viTDO.setInterpretationId(vi.getId());
        viTDO.setCaid(vi.getVariant().getCaid());
        viTDO.setConditionId(vi.getCondition().getId());
        viTDO.setCondition(vi.getCondition().getTerm());
        viTDO.setInheritanceId(vi.getInheritance().getId());
        viTDO.setInheritance(vi.getInheritance().getTerm());
        viTDO.setEvidenceSet(resultEvidenceSet);
        viTDO.setFinalCallId(vi.getFinalCall().getId());
        viTDO.setFinalCall(vi.getFinalCall().getTerm());
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
