package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.FinalCallDTO;
import com.persida.pathogenicity_calculator.dto.IheritanceDTO;

import java.util.List;

public interface CalculatorService {
    String getAlleleRepositoryData(String variantCID);
    String getMyVariantInfoHG38Link(String myVariantInfoGH38Identifier);
    List<IheritanceDTO> getInheritanceModes();
    List<FinalCallDTO> getFinalCalls();
}
