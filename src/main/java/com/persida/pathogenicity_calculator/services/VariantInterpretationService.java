package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.EvidenceDocUpdateEvent;
import com.persida.pathogenicity_calculator.dto.VariantInterpretationDTO;
import com.persida.pathogenicity_calculator.dto.VariantInterpretationLoadRequest;
import com.persida.pathogenicity_calculator.dto.VariantInterpretationSaveResponse;

public interface VariantInterpretationService {
    VariantInterpretationSaveResponse saveNewInterpretation(VariantInterpretationDTO saveInterpretationRequest);
    VariantInterpretationDTO loadInterpretation(VariantInterpretationLoadRequest loadInterpretationRequest);
    VariantInterpretationSaveResponse updateEvidenceDoc(EvidenceDocUpdateEvent evidenceDocUpdateEvent);
    String getFinalCallForCaID(String variantCID);
}
