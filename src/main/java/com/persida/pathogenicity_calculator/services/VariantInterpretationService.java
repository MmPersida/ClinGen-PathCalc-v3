package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.*;

import java.util.List;

public interface VariantInterpretationService {
    VariantInterpretationSaveResponse saveNewInterpretation(VariantInterpretationDTO saveInterpretationRequest);
    VariantInterpretationDTO loadInterpretation(VariantInterpretationLoadRequest loadInterpretationRequest);
    VariantInterpretationSaveResponse updateEvidenceDoc(EvidenceDocUpdateEvent evidenceDocUpdateEvent);
    List<VIBasicDTO> getVIBasicDataForCaid(String variantCID);
}
