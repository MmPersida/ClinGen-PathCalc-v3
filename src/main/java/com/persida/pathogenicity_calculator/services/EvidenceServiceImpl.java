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

        FinalCall cfc = null;
        //get FinalCall based on name or id, whatever is present in the request
        if(saveEvidenceSetDTO.getCalculatedFinalCall() != null){
            if(saveEvidenceSetDTO.getCalculatedFinalCall().getId() > 0){
                cfc = finalCallRepository.getFinalCallById(saveEvidenceSetDTO.getCalculatedFinalCall().getId());
            }else{
                cfc = finalCallRepository.getFinalCallByName(saveEvidenceSetDTO.getCalculatedFinalCall().getTerm());
            }
        }

        FinalCall dfc = null;
        if(saveEvidenceSetDTO.getDeterminedFinalCall() != null){
            if(saveEvidenceSetDTO.getDeterminedFinalCall().getId() > 0){
                dfc = finalCallRepository.getFinalCallById(saveEvidenceSetDTO.getDeterminedFinalCall().getId());
            }else{
                dfc = finalCallRepository.getFinalCallByName(saveEvidenceSetDTO.getDeterminedFinalCall().getTerm());
            }
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
        if(cfc != null){
            interpretation.setFinalcall(cfc);
        }
        if(dfc != null){
            interpretation.setFinalcall(dfc);
        }
        variantInterpretationRepository.save(interpretation);
        return new VariantInterpretationSaveResponse(interpretation.getId());
    }

    @Override
    public VariantInterpretationSaveResponse deleteEvidence(EvidenceListDTO deleteEvidenceSetDTO){
        if(deleteEvidenceSetDTO.getEvidenceList() != null && deleteEvidenceSetDTO.getEvidenceList().size() > 0){
            //delete the actual evidence using the interId and the evd type and modifier (they are unique to this inter.)
            Integer interpretationId = deleteEvidenceSetDTO.getInterpretationId();
            if(interpretationId == null ||interpretationId == 0){
                logger.error("Unable to delete evidences interpretationId is unknown!");
                return new VariantInterpretationSaveResponse(deleteEvidenceSetDTO.getInterpretationId(), "Unable to find the variant interpretation with id: "+interpretationId);
            }
            Evidence e = null;
            for(EvidenceDTO evdDTO : deleteEvidenceSetDTO.getEvidenceList()){
                e = evidenceRepository.getEvidenceByNameAndVIId(interpretationId, evdDTO.getType(), evdDTO.getModifier());
                if(e != null){
                    evidenceRepository.delete(e);
                }else{
                    logger.error("Unable to find evidence and delete it!");
                }
            }
        }

        VariantInterpretation interpretation = variantInterpretationRepository.getVariantInterpretationById(deleteEvidenceSetDTO.getInterpretationId());

        FinalCall cfc = null;
        //get FinalCall based on name or id, whatever is present in the request
        if(deleteEvidenceSetDTO.getCalculatedFinalCall() != null){
            if(deleteEvidenceSetDTO.getCalculatedFinalCall().getId() > 0){
                cfc = finalCallRepository.getFinalCallById(deleteEvidenceSetDTO.getCalculatedFinalCall().getId());
            }else{
                cfc = finalCallRepository.getFinalCallByName(deleteEvidenceSetDTO.getCalculatedFinalCall().getTerm());
            }
        }

        FinalCall dfc = null;
        if(deleteEvidenceSetDTO.getDeterminedFinalCall() != null){
            if(deleteEvidenceSetDTO.getDeterminedFinalCall().getId() > 0){
                dfc = finalCallRepository.getFinalCallById(deleteEvidenceSetDTO.getDeterminedFinalCall().getId());
            }else{
                dfc = finalCallRepository.getFinalCallByName(deleteEvidenceSetDTO.getDeterminedFinalCall().getTerm());
            }
        }

        boolean fcEdited = false;
        if(cfc != null && !cfc.getTerm().equals(interpretation.getFinalCall().getTerm())){
            interpretation.setFinalcall(cfc);
            fcEdited = true;
        }
        if(dfc != null && !dfc.getTerm().equals(interpretation.getDeterminedFinalCall().getTerm())){
            interpretation.setDeterminedFinalCall(dfc);
            fcEdited = true;
        }
        if(fcEdited){
            variantInterpretationRepository.save(interpretation);
        }
        return new VariantInterpretationSaveResponse(200,null);
    }

    @Override
    public VariantInterpretationSaveResponse deleteEvidenceById(EvidenceListDTO deleteEvidenceSetDTO){
        if(deleteEvidenceSetDTO.getEvidenceList() != null && deleteEvidenceSetDTO.getEvidenceList().size() > 0){

            Integer interpretationId = deleteEvidenceSetDTO.getInterpretationId();
            if(interpretationId == null ||interpretationId == 0){
                logger.error("Unable to delete evidences interpretationId is unknown!");
                return new VariantInterpretationSaveResponse(deleteEvidenceSetDTO.getInterpretationId(), "Unable to find the variant interpretation with id: "+interpretationId);
            }
            Evidence e = null;
            for(EvidenceDTO evdDTO : deleteEvidenceSetDTO.getEvidenceList()){
                if(evdDTO.getEvidenceId() == null){
                    logger.error("Unable to find evidence and delete it, evidence id is NULL!");
                    continue;
                }
                e = evidenceRepository.getEvidenceById(evdDTO.getEvidenceId());
                if(e != null){
                    evidenceRepository.delete(e);
                }else{
                    logger.error("Unable to find evidence by id "+evdDTO.getEvidenceId()+" and delete it!");
                }
            }        }
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

    @Override
    public void saveEvidenceSet(Set<Evidence> evidenceSet){
        if(evidenceSet == null || evidenceSet.size() == 0){
            return;
        }
        for(Evidence e : evidenceSet){
            evidenceRepository.save(e);
        }
    }
}
