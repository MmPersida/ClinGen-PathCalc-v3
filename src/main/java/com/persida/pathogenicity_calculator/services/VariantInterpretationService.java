package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.*;

import java.util.List;

public interface VariantInterpretationService {
    VariantInterpretationSaveResponse saveNewEvidence(VariantInterpretationDTO saveInterpretationEvdRequest);
    VariantInterpretationSaveResponse deleteEvidence(VariantInterpretationDTO deleteInterpretationEvdRequest);
    VariantInterpretationDTO loadInterpretation(VariantInterpretationLoadRequest loadInterpretationRequest);
    VariantInterpretationSaveResponse saveNewInterpretation(VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq);
    VariantInterpretationSaveResponse updateEvidenceDoc(VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq);
    List<VIBasicDTO> getVIBasicDataForCaid(String variantCAID);
    List<VIBasicDTO> searchInterpByCaidEvidenceDoc(VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq);
}
