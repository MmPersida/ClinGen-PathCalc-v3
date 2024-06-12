package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

@Data
public class GeneList {
    private String[] genes;
    public String[] getGenes() {
        return genes;
    }

    public void setGenes(String[] genes) {
        this.genes = genes;
    }
}
