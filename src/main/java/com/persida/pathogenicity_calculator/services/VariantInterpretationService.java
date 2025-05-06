package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.*;
import com.persida.pathogenicity_calculator.dto.*;
import com.persida.pathogenicity_calculator.repository.entity.User;
import com.persida.pathogenicity_calculator.repository.entity.VariantInterpretation;

import java.util.List;

public interface VariantInterpretationService {
    VariantInterpretationDTO loadInterpretation(VariantInterpretationIDRequest interpretationIDRequest);
    VariantInterpretation getInterpretationById(Integer interpretationId);
    VariantInterpretationSaveResponse saveNewInterpretation(VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq, User user);
    VariantInterpretationSaveResponse deleteInterpretation(VariantInterpretationIDRequest interpretationIDRequest);
    VariantInterpretationSaveResponse updateEvidenceDocAndEngine(VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq);
    VarInterpUpdateFCResponse updateCalculatedFinalCall(VarInterpUpdateFinalCallRequest viUpdateFCReq);
    List<VIBasicDTO> getVIBasicDataForCaid(String variantCAID);
    List<VIBasicDTO> getUserVIBasicDataForCaid(int userId, String variantCAID);
    List<VIBasicDTO> getAllInterpretedVariantsByUser(Integer userId);
    List<VIBasicDTO> searchInterpByCaidEvidenceDoc(VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq);
    String loadViDescription(VariantInterpretationIDRequest interpretationIDRequest);
    String saveEditVIDescription(VariantDescriptionRequest interpretationIDRequest);
    VarInterpUpdateFCResponse saveDeterminedFC(VarInterpUpdateFinalCallRequest viUpdateFCReq);
    ReportDTO generateReportData(VariantInterpretationIDRequest interpretationIDRequest);
}
