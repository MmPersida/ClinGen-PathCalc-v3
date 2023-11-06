package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.config.AuthentificationManager;
import com.persida.pathogenicity_calculator.dto.*;

import com.persida.pathogenicity_calculator.repository.*;
import com.persida.pathogenicity_calculator.repository.entity.*;
import com.persida.pathogenicity_calculator.utils.EvidenceMapperAndSupport;
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
    private AuthentificationManager authentificationManager;

    @Autowired
    private UserService userService;
    @Autowired
    private EvidenceService evidenceService;
    /*
    @Autowired
    private ModelMapper modelMapper;*/

    @Override
    public VariantInterpretationSaveResponse saveNewInterpretation(VariantInterpretationDTO saveInterpretationDTO){
        //use the CAID to find this variant in the DB
        Variant var = variantRepository.getVariantByCAID(saveInterpretationDTO.getCaid());
        if(var == null){
            //if it's new, create it
            var = new Variant(saveInterpretationDTO.getCaid());
            variantRepository.save(var);
            logger.info("Saved new Variant, caid: "+var.getCaid());
        }

        //get the current user
        User u = getCurrentUserEntityObj();
        if(u == null){
            return null;
        }

        EvidenceMapperAndSupport esMapperSupport = new EvidenceMapperAndSupport();
        Condition con = null;
        FinalCall fc = null;
        Inheritance inher = null;

        //get the Condition based on name or id, whatever is present in the request
        if(saveInterpretationDTO.getConditionId() != null && saveInterpretationDTO.getConditionId() > 0){
            con = conditionRepository.getConditionById(saveInterpretationDTO.getConditionId());
        }else{
            con = conditionRepository.getConditionByName(saveInterpretationDTO.getCondition());
        }

        //get FinalCall based on name or id, whatever is present in the request
        if(saveInterpretationDTO.getFinalCallId() != null && saveInterpretationDTO.getFinalCallId() > 0){
            fc = finalCallRepository.getFinalCallById(saveInterpretationDTO.getFinalCallId());
        }else{
            fc = finalCallRepository.getFinalCallByName(saveInterpretationDTO.getFinalCall());
        }

        //get the Mode of Inheritance based on name or id, whatever is present in the request
        if(saveInterpretationDTO.getInheritanceId() != null && saveInterpretationDTO.getInheritanceId() > 0){
            inher = inheritanceRepository.getInheritanceById(saveInterpretationDTO.getInheritanceId());
        }else{
            inher = inheritanceRepository.getInheritanceByName(saveInterpretationDTO.getInheritance());
        }

        VariantInterpretation interpretation = null;
        HashMap<String, Evidence> newEvidenceMap = esMapperSupport.mapEvidenceDTOListToEvdMap(saveInterpretationDTO.getEvidenceList());
        if(saveInterpretationDTO.getInterpretationId() == null || saveInterpretationDTO.getInterpretationId() == 0){
            //this is a new Interpretation
            Set<Evidence> newEvidenceSet = esMapperSupport.getEvidenceSet(newEvidenceMap);
            interpretation = new VariantInterpretation(u, var, newEvidenceSet, con, fc, inher);
            evidenceService.saveEvidenceSet(newEvidenceSet);
        }else{
            //use the var. interpretation to get its evidence set for update
            interpretation = variantInterpretationRepository.getVariantInterpretationById(saveInterpretationDTO.getInterpretationId());
            if(interpretation == null){
                logger.error("Error: Unable to find the variant interpretation with id: "+saveInterpretationDTO.getInterpretationId());
                return null;
            }

            if(con != null){
                interpretation.setCondition(con);
            }
            if(fc != null){
                interpretation.setFinalcall(fc);
            }
            if(inher != null){
                interpretation.setInheritance(inher);
            }
            //map the new evidence set from the request to the current internal evidence set
            esMapperSupport.compareAndMapNewEvidences(interpretation, newEvidenceMap);
            //save the update evidence set
            evidenceService.saveEvidenceSet(interpretation.getEvidences());
        }

        variantInterpretationRepository.save(interpretation);
        return new VariantInterpretationSaveResponse(interpretation.getId());
    }

    @Override
    public VariantInterpretationDTO loadInterpretation(VariantInterpretationLoadRequest loadInterpretationRequest) {
        //get VI based on te unique ID
        VariantInterpretation vi = variantInterpretationRepository.getVariantInterpretationById(loadInterpretationRequest.getInterpretationId());
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

        VariantInterpretation vi = variantInterpretationRepository.getVariantInterpretationById(edue.getInterpretationId());
        if(vi != null){
            if(con != null){
                vi.setCondition(con);
            }
            if(inher != null){
                vi.setInheritance(inher);
            }
            variantInterpretationRepository.save(vi);
        }else{
            vi = new VariantInterpretation(u, var, null, con, finalCallRepository.getFinalCallInsufficient(), inher);
            variantInterpretationRepository.save(vi);
            return new VariantInterpretationSaveResponse(vi.getId());
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
        if(viList == null || viList.size() == 0){
            return null;
        }

        List<VIBasicDTO> viBasicDTOList = new ArrayList<VIBasicDTO>();
        for(VariantInterpretation vi : viList){
            viBasicDTOList.add(new VIBasicDTO(vi.getVariant().getCaid(),
                                                vi.getId(),
                                                vi.getCondition().getTerm(),
                                                vi.getInheritance().getTerm(),
                                                vi.getFinalCall().getTerm()));
        }
        return viBasicDTOList;
    }

    private VariantInterpretationDTO convertVariantInterpretationEntityToDTO(VariantInterpretation vi) {
        EvidenceMapperAndSupport esMapperSupport = new EvidenceMapperAndSupport();
        List<EvidenceDTO> resultEvidenceList = esMapperSupport.mapEvidenceSetToDTO(vi.getEvidences());

        VariantInterpretationDTO viTDO = new VariantInterpretationDTO();
        viTDO.setInterpretationId(vi.getId());
        viTDO.setCaid(vi.getVariant().getCaid());
        viTDO.setConditionId(vi.getCondition().getId());
        viTDO.setCondition(vi.getCondition().getTerm());
        viTDO.setInheritanceId(vi.getInheritance().getId());
        viTDO.setInheritance(vi.getInheritance().getTerm());
        viTDO.setEvidenceList(resultEvidenceList);
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
