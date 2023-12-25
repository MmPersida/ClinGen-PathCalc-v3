package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.EvidenceSummaryRequest;
import com.persida.pathogenicity_calculator.dto.EvidenceListDTO;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.VariantInterpretationSaveResponse;
import com.persida.pathogenicity_calculator.dto.EvidenceSummaryDTO;

import java.util.HashMap;

public interface EvidenceService {
    VariantInterpretationSaveResponse saveNewEvidence(EvidenceListDTO saveEvidenceSetDTO);
    VariantInterpretationSaveResponse deleteEvidence(EvidenceListDTO deleteEvidenceSetDTO);
    HashMap<String, EvidenceSummaryDTO> getEvdSummaryForVIIdAndEvdTags(EvidenceSummaryRequest evdSummaryReq);
}
