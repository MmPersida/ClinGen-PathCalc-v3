package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.EvidenceDTO;
import com.persida.pathogenicity_calculator.dto.VariantInterpretationDTO;
import com.persida.pathogenicity_calculator.dto.VariantInterpretationSaveResponse;
import com.persida.pathogenicity_calculator.repository.EvidenceRepository;
import com.persida.pathogenicity_calculator.repository.FinalCallRepository;
import com.persida.pathogenicity_calculator.repository.VariantInterpretationRepository;
import com.persida.pathogenicity_calculator.repository.entity.Evidence;
import com.persida.pathogenicity_calculator.repository.entity.FinalCall;
import com.persida.pathogenicity_calculator.repository.entity.VariantInterpretation;
import com.persida.pathogenicity_calculator.utils.EvidenceMapperAndSupport;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
public class EvidenceServiceImpl implements EvidenceService{

    private static Logger logger = Logger.getLogger(EvidenceServiceImpl.class);

    @Autowired
    private EvidenceRepository evidenceRepository;
    @Autowired
    private FinalCallRepository finalCallRepository;
    @Autowired
    private VariantInterpretationRepository variantInterpretationRepository;

    @Override
    public VariantInterpretationSaveResponse saveNewEvidence(VariantInterpretationDTO saveInterpretationDTO){
        if(saveInterpretationDTO.getEvidenceList() == null || saveInterpretationDTO.getEvidenceList().size() == 0){
            logger.error("Error: Evidence list in the request to save new evidences is null or empty.");
            return null;
        }
        EvidenceMapperAndSupport esMapperSupport = new EvidenceMapperAndSupport();

        FinalCall fc = null;
        //get FinalCall based on name or id, whatever is present in the request
        if(saveInterpretationDTO.getFinalCallId() != null && saveInterpretationDTO.getFinalCallId() > 0){
            fc = finalCallRepository.getFinalCallById(saveInterpretationDTO.getFinalCallId());
        }else{
            fc = finalCallRepository.getFinalCallByName(saveInterpretationDTO.getFinalCall());
        }

        VariantInterpretation interpretation = variantInterpretationRepository.getVariantInterpretationById(saveInterpretationDTO.getInterpretationId());
        if(interpretation == null){
            return new VariantInterpretationSaveResponse(saveInterpretationDTO.getInterpretationId(), "Unable to find the variant interpretation with id: "+saveInterpretationDTO.getInterpretationId());
        }

        HashMap<String, Evidence> newEvidenceMap = esMapperSupport.mapEvidenceDTOListToEvdMap(saveInterpretationDTO.getEvidenceList());
        //map the new evidence set from the request to the current internal evidence set
        esMapperSupport.compareAndMapNewEvidences(interpretation, newEvidenceMap);
        //save the update evidence set
        saveEvidenceSet(interpretation.getEvidences());
        if(fc != null){
            interpretation.setFinalcall(fc);
        }
        variantInterpretationRepository.save(interpretation);
        return new VariantInterpretationSaveResponse(interpretation.getId());
    }

    @Override
    public VariantInterpretationSaveResponse deleteEvidence(VariantInterpretationDTO deleteInterpretationEvdRequest){
        if(deleteInterpretationEvdRequest.getEvidenceList() != null && deleteInterpretationEvdRequest.getEvidenceList().size() > 0){
            deleteEvidenceSetByNameAndVIId(deleteInterpretationEvdRequest.getInterpretationId(), deleteInterpretationEvdRequest.getEvidenceList());
        }
        return new VariantInterpretationSaveResponse(200,null);
    }

    public void saveEvidenceSet(Set<Evidence> evidenceSet){
        if(evidenceSet == null || evidenceSet.size() == 0){
            return;
        }
        for(Evidence e : evidenceSet){
            evidenceRepository.save(e);
        }
    }

    public void deleteEvidenceSetByNameAndVIId(Integer interpretationId, List<EvidenceDTO> evdToDelete){
        if(interpretationId == null ||interpretationId == 0){
            logger.error("Unable to delete evidences interpretationId is unknown!");
            return;
        }
        Evidence e = null;
        for(EvidenceDTO eDTO : evdToDelete){
            e = evidenceRepository.getEvidenceByNameAndVIId(interpretationId, eDTO.getName());
            if(e != null){
                evidenceRepository.delete(e);
            }
        }
    }
}
