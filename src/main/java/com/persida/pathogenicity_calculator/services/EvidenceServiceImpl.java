package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.DeleteEvdLinkRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.EvidenceLinksRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.EvidenceSummaryRequest;
import com.persida.pathogenicity_calculator.dto.*;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.VariantInterpretationSaveResponse;
import com.persida.pathogenicity_calculator.repository.*;
import com.persida.pathogenicity_calculator.repository.entity.Evidence;
import com.persida.pathogenicity_calculator.repository.entity.EvidenceLink;
import com.persida.pathogenicity_calculator.repository.entity.FinalCall;
import com.persida.pathogenicity_calculator.repository.entity.VariantInterpretation;
import com.persida.pathogenicity_calculator.repository.jpa.EvidenceLinkJPA;
import com.persida.pathogenicity_calculator.repository.jpa.EvidenceSummaryJPA;
import com.persida.pathogenicity_calculator.utils.EvidenceMapperAndSupport;
import com.persida.pathogenicity_calculator.utils.StackTracePrinter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EvidenceServiceImpl implements EvidenceService{

    private static Logger logger = Logger.getLogger(EvidenceServiceImpl.class);

    @Autowired
    private EvidenceRepository evidenceRepository;
    @Autowired
    private EvidenceSummaryRepository evidenceSummaryRepository;
    @Autowired
    private EvidenceLinksRepository evidenceLinksRepository;
    @Autowired
    private FinalCallRepository finalCallRepository;
    @Autowired
    private VariantInterpretationRepository variantInterpretationRepository;

    @Override
    public VariantInterpretationSaveResponse saveNewEvidence(EvidenceListDTO saveEvidenceSetDTO){
        if(saveEvidenceSetDTO.getEvidenceList() == null || saveEvidenceSetDTO.getEvidenceList().size() == 0){
            logger.error("Error: Evidence list in the request to save new evidences is null or empty.");
            return null;
        }

        EvidenceMapperAndSupport esMapperSupport = new EvidenceMapperAndSupport();

        FinalCall fc = null;
        //get FinalCall based on name or id, whatever is present in the request
        if(saveEvidenceSetDTO.getFinalCallId() != null && saveEvidenceSetDTO.getFinalCallId() > 0){
            fc = finalCallRepository.getFinalCallById(saveEvidenceSetDTO.getFinalCallId());
        }else{
            fc = finalCallRepository.getFinalCallByName(saveEvidenceSetDTO.getFinalCall());
        }

        VariantInterpretation interpretation = variantInterpretationRepository.getVariantInterpretationById(saveEvidenceSetDTO.getInterpretationId());
        if(interpretation == null){
            return new VariantInterpretationSaveResponse(saveEvidenceSetDTO.getInterpretationId(), "Unable to find the variant interpretation with id: "+saveEvidenceSetDTO.getInterpretationId());
        }

        HashMap<String, Evidence> newEvidenceMap = esMapperSupport.mapEvidenceDTOListToEvdMap(saveEvidenceSetDTO.getEvidenceList());
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
    public VariantInterpretationSaveResponse deleteEvidence(EvidenceListDTO deleteEvidenceSetDTO){
        if(deleteEvidenceSetDTO.getEvidenceList() != null && deleteEvidenceSetDTO.getEvidenceList().size() > 0){
            deleteEvidenceSetByNameAndVIId(deleteEvidenceSetDTO.getInterpretationId(), deleteEvidenceSetDTO.getEvidenceList());
        }

        VariantInterpretation interpretation = variantInterpretationRepository.getVariantInterpretationById(deleteEvidenceSetDTO.getInterpretationId());

        FinalCall fc = null;
        //get FinalCall based on name or id, whatever is present in the request
        if(deleteEvidenceSetDTO.getFinalCallId() != null && deleteEvidenceSetDTO.getFinalCallId() > 0){
            fc = finalCallRepository.getFinalCallById(deleteEvidenceSetDTO.getFinalCallId());
        }else{
            fc = finalCallRepository.getFinalCallByName(deleteEvidenceSetDTO.getFinalCall());
        }
        if(!fc.getTerm().equals(interpretation.getFinalCall().getTerm())){
            interpretation.setFinalcall(fc);
            variantInterpretationRepository.save(interpretation);
        }
        return new VariantInterpretationSaveResponse(200,null);
    }

    @Override
    public HashMap<String, EvidenceSummaryDTO> getEvdSummaryForVIIdAndEvdTags(EvidenceSummaryRequest evdSummaryReq){
        List<EvidenceSummaryJPA> evidences = evidenceSummaryRepository.getEvdSummariesForVIIdAndEvdTags(evdSummaryReq.getInterpretationId(), evdSummaryReq.getEvidenceTags());
        if(evidences == null || evidences.size() == 0){
            return null;
        }

        HashMap<String,EvidenceSummaryDTO> evdSummaries = new HashMap<String,EvidenceSummaryDTO>();
        for(EvidenceSummaryJPA e : evidences){
            String fullEvdLabel = e.getEvidenceType();
            if(e.getEvidenceModifier() != null && !e.getEvidenceModifier().equals("")){
                fullEvdLabel += " - "+e.getEvidenceModifier();
            }
            evdSummaries.put(fullEvdLabel, new EvidenceSummaryDTO(e.getEvdSummaryId(), e.getSummary()));
        }
        return evdSummaries;
    }
    @Override
    public List<EvidenceLinkDTO> getLinksFroVIIdAndEvdTag(EvidenceLinksRequest evdLinksReq){
        List<EvidenceLinkJPA> evidenceLinksJPA = evidenceLinksRepository.getLinksFroVIIdAndEvdTag(evdLinksReq.getInterpretationId(),
                                                                                                    evdLinksReq.getEvidenceTag(),
                                                                                                    evdLinksReq.getEvidenceModifier());
        if(evidenceLinksJPA == null || evidenceLinksJPA.size() == 0){
            return null;
        }
        List<EvidenceLinkDTO> linksList = new ArrayList<EvidenceLinkDTO>();
        for(EvidenceLinkJPA e : evidenceLinksJPA){
            if(e.getEvdLinkId() == null || e.getLink() == null){
                continue;
            }
            linksList.add(new EvidenceLinkDTO(e.getEvdLinkId(), e.getLink(), e.getLinkCode(), e.getComment()));
        }
        return linksList;
    }

    @Override
    public String deleteEvidenceLinkById(DeleteEvdLinkRequest deleteEvdLinkRequest){
        EvidenceLink el = evidenceLinksRepository.getEvdLinkById(deleteEvdLinkRequest.getEvdLinkId());
        if(el != null){
            try{
                evidenceLinksRepository.delete(el);
            }catch(Exception e){
                logger.error(StackTracePrinter.printStackTrace(e));
                return null;
            }
        }
        return "Evidence LInk with ID:"+deleteEvdLinkRequest.getEvdLinkId()+" deleted!";
    }

    @Override
    public String saveEvidenceLinks(EvidenceLinksDTO evidenceLinksDTO){
        List<EvidenceLinkDTO> evidenceLinkDTOs = evidenceLinksDTO.getEvidenceLinks();
        if(evidenceLinkDTOs == null || evidenceLinkDTOs.size() == 0){
            return null;
        }

        Evidence evd = null;
        EvidenceLink el = null;
        for(EvidenceLinkDTO elDTO : evidenceLinkDTOs){
            if(elDTO.getLinkId() != null && elDTO.getLinkId() > 0){
                el = evidenceLinksRepository.getEvdLinkById(elDTO.getLinkId());
                el.setEvdLink(elDTO.getLink());
                el.setLinkCode(elDTO.getLinkCode());
                el.setComment(elDTO.getComment());
                evidenceLinksRepository.save(el);
            }else{
                if(evd == null){
                    evd = evidenceRepository.getEvidenceByNameAndVIId(evidenceLinksDTO.getInterpretationId(), evidenceLinksDTO.getEvidenceTag(), evidenceLinksDTO.getEvidenceModifier());
                }
                el = new EvidenceLink(elDTO.getLink(), elDTO.getLinkCode(), elDTO.getComment(), evd);
                evidenceLinksRepository.save(el);
            }
        }
        return "Saved, OK!";
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
        for(EvidenceDTO evdDTO : evdToDelete){
            e = evidenceRepository.getEvidenceByNameAndVIId(interpretationId, evdDTO.getType(), evdDTO.getModifier());
            if(e != null){
                evidenceRepository.delete(e);
            }
        }
    }
}
