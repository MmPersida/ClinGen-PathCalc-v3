package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.EvidenceListDTO;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.VariantInterpretationSaveResponse;

public interface EvidenceService {
    VariantInterpretationSaveResponse saveNewEvidence(EvidenceListDTO saveEvidenceSetDTO);
    VariantInterpretationSaveResponse deleteEvidence(EvidenceListDTO deleteEvidenceSetDTO);
}
