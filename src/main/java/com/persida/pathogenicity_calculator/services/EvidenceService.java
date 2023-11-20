package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.VariantInterpretationDTO;
import com.persida.pathogenicity_calculator.dto.VariantInterpretationSaveResponse;

public interface EvidenceService {
    VariantInterpretationSaveResponse saveNewEvidence(VariantInterpretationDTO saveInterpretationEvdRequest);
    VariantInterpretationSaveResponse deleteEvidence(VariantInterpretationDTO deleteInterpretationEvdRequest);
}
