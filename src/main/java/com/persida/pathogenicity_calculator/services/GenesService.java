package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.CSpecEngineDTO;
import com.persida.pathogenicity_calculator.dto.GeneList;
import com.persida.pathogenicity_calculator.repository.entity.Gene;

import java.util.HashMap;

public interface GenesService {
    HashMap<String, CSpecEngineDTO> engineDataForGenes(GeneList geneList);
    String getGeneData(String geneNameID);
    String[] getGeneHGNCandNCBIids(String geneName);
    void compareAndUpdateGene(Gene newGene);
}
