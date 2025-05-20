package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.DeleteEvdLinkRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.EvidenceLinksRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.EvidenceSummaryRequest;
import com.persida.pathogenicity_calculator.dto.EvidenceLinkDTO;
import com.persida.pathogenicity_calculator.dto.EvidenceLinksDTO;
import com.persida.pathogenicity_calculator.dto.EvidenceListDTO;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.VariantInterpretationSaveResponse;
import com.persida.pathogenicity_calculator.dto.EvidenceSummaryDTO;
import com.persida.pathogenicity_calculator.repository.entity.Evidence;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface EvidenceService {
    VariantInterpretationSaveResponse saveNewEvidence(EvidenceListDTO saveEvidenceSetDTO);
    VariantInterpretationSaveResponse deleteEvidence(EvidenceListDTO deleteEvidenceSetDTO);
    VariantInterpretationSaveResponse deleteEvidenceById(EvidenceListDTO deleteEvidenceSetDTO);
    HashMap<String, EvidenceSummaryDTO> getEvdSummaryForVIIdAndEvdTags(EvidenceSummaryRequest evdSummaryReq);
    List<EvidenceLinkDTO> getLinksFroVIIdAndEvdTag(EvidenceLinksRequest evdLinksReq);
    String deleteEvidenceLinkById(DeleteEvdLinkRequest deleteEvdLinkRequest);
    String saveEvidenceLinks(EvidenceLinksDTO evidenceLinksDTO);
    void saveEvidenceSet(Set<Evidence> evidenceSet);
}
