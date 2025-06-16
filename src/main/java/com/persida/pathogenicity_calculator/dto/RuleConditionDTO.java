package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

@Data
public class RuleConditionDTO {
    private String label;
    private String evidenceTableMarkers;
    private int conditionsLeft;

    public RuleConditionDTO(String label, String evidenceTableMarkers){
        this.label = label;
        this.evidenceTableMarkers = evidenceTableMarkers;
    }

    public RuleConditionDTO(String label, String evidenceTableMarkers, int conditionsLeft){
        this.label = label;
        this.evidenceTableMarkers = evidenceTableMarkers;
        this.conditionsLeft = conditionsLeft;
    }
}
