package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.IheritanceDTO;

import java.util.List;

public interface CalculatorService {
    String getAlleleRepositoryData(String variantCID);
    String getGeneData(String geneNameID);
    String getMyVariantInfoHG38Link(String myVariantInfoGH38Identifier);
    List<IheritanceDTO> getInheritanceModes();
}
