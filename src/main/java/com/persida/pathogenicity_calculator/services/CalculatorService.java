package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.VarInterpSaveUpdateEvidenceDocRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.VarInterpUpdateFinalCallRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.VariantInterpretationSaveResponse;
import com.persida.pathogenicity_calculator.dto.IheritanceDTO;

import java.util.List;

public interface CalculatorService {
    String getAlleleRepositoryData(String variantCID);
    String getGeneData(String geneNameID);
    List<IheritanceDTO> getInheritanceModes();
}
