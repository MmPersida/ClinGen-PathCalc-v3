package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.*;

import java.util.List;

public interface VariantInterpretationService {
    VariantInterpretationDTO loadInterpretation(VariantInterpretationIDRequest interpretationIDRequest);
    VariantInterpretationSaveResponse saveNewInterpretation(VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq);
    VariantInterpretationSaveResponse deleteInterpretation(VariantInterpretationIDRequest interpretationIDRequest);
    VariantInterpretationSaveResponse updateEvidenceDocAndEngine(VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq);
    VariantInterpretationSaveResponse updateFinalCall(VarInterpUpdateFinalCallRequest viUpdateFCReq);
    List<VIBasicDTO> getVIBasicDataForCaid(String variantCAID);
    List<VIBasicDTO> searchInterpByCaidEvidenceDoc(VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq);
    String loadViDescription(VariantInterpretationIDRequest interpretationIDRequest);
    String saveEditVIDescription(VariantDescriptionRequest interpretationIDRequest);
}
