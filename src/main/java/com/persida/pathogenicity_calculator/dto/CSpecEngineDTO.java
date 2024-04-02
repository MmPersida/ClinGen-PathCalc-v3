package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CSpecEngineDTO {
    private String engineId;
    private String engineSummary;
    private String organizationName;
    private Integer ruleSetId;
    private String ruleSetURL;
    private Set<EngineRelatedGeneDTO> genes;
    private String ruleSetJSONStr;
    private String criteriaCodesJSONStr;
    private Boolean enabled;

    public CSpecEngineDTO(String engineId, String engineSummary, String organizationName){
        this.engineId = engineId;
        this.engineSummary = engineSummary;
        this.organizationName = organizationName;
    }

    public CSpecEngineDTO(String engineId, String engineSummary, String organizationName,
                          Integer ruleSetId, String ruleSetURL, Set<EngineRelatedGeneDTO> genes, boolean enabled){
        this.engineId = engineId;
        this.engineSummary = engineSummary;
        this.organizationName = organizationName;
        this.ruleSetId = ruleSetId;
        this.ruleSetURL = ruleSetURL;
        if(genes != null){
            this.genes = genes;
        }
        this.enabled = enabled;
    }

    public CSpecEngineDTO(String engineId, String engineSummary, String organizationName,
                          Integer ruleSetId, String ruleSetURL, Set<EngineRelatedGeneDTO> genes,
                          String ruleSetStr, boolean enabled){
        this.engineId = engineId;
        this.engineSummary = engineSummary;
        this.organizationName = organizationName;
        this.ruleSetId = ruleSetId;
        this.ruleSetURL = ruleSetURL;
        if(genes != null){
            this.genes = genes;
        }
        this.ruleSetJSONStr = ruleSetStr;
        this.enabled = enabled;
    }

    public void addGenes(EngineRelatedGeneDTO engineRelatedGene){
        if(engineRelatedGene == null){
            return;
        }
        if(genes == null){
            genes = new HashSet<EngineRelatedGeneDTO>();
        }
        genes.add(engineRelatedGene);
    }

    @Override
    public String toString(){
        return "{\"engineId\":\""+engineId+"\",\"engineSummary\":\""+engineSummary+"\",\"organizationName\":\""+organizationName+"\",\"ruleSetId\":\""+ruleSetId+"\",\"ruleSetURL\":\""+ruleSetURL+"\",\"genes\":"+genes+",\"enabled\":\""+enabled+"\"},";
    }
}
