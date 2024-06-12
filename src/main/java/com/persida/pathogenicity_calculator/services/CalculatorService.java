package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.CSpecEngineDTO;
import com.persida.pathogenicity_calculator.dto.GeneList;
import com.persida.pathogenicity_calculator.dto.IheritanceDTO;

import java.util.HashMap;
import java.util.List;

public interface CalculatorService {
    String getAlleleRepositoryData(String variantCID);
    HashMap<String, CSpecEngineDTO> engineDataForGenes(GeneList geneList);
    String getGeneData(String geneNameID);
    String getMyVariantInfoHG38Link(String myVariantInfoGH38Identifier);
    List<IheritanceDTO> getInheritanceModes();
}
