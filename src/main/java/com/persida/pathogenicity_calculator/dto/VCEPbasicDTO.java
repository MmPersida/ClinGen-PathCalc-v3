package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class VCEPbasicDTO {
    private String engineId;
    private String organizationName;
    private String organizationLink;
    private Integer ruleSetId;
    private String ruleSetURL;
    private Set<EngineRelatedGeneDTO> genes;

    public void addGenes(EngineRelatedGeneDTO vcepRelatedGene){
        if(vcepRelatedGene == null){
            return;
        }
        if(genes == null){
            genes = new HashSet<EngineRelatedGeneDTO>();
        }
        genes.add(vcepRelatedGene);
    }
}
