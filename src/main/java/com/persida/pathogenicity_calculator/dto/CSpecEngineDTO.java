package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Data
public class CSpecEngineDTO {
    private String engineId;
    private String engineSummary;
    private String organizationName;
    private Integer ruleSetId;
    private String ruleSetURL;
    private Set<EngineRelatedGene> genes;

    public CSpecEngineDTO(String engineId, String engineSummary, String organizationName, Integer ruleSetId, String ruleSetURL, Set<EngineRelatedGene> genes){
        this.engineId = engineId;
        this.engineSummary = engineSummary;
        this.organizationName = organizationName;
        this.ruleSetId = ruleSetId;
        this.ruleSetURL = ruleSetURL;
        if(genes != null){
            this.genes = genes;
        }
    }

    public void addGenes(EngineRelatedGene engineRelatedGene){
        if(engineRelatedGene == null){
            return;
        }
        if(genes == null){
            genes = new HashSet<EngineRelatedGene>();
        }
        genes.add(engineRelatedGene);
    }

    @Override
    public String toString(){
        return "{\"engineId\":\""+engineId+"\",\"engineSummary\":\""+engineSummary+"\",\"organizationName\":\""+organizationName+"\",\"ruleSetId\":\""+ruleSetId+"\",\"ruleSetURL\":\""+ruleSetURL+"\",\"genes\":"+genes+"},";
    }
}
