package com.persida.pathogenicity_calculator.model.openAPI;

import lombok.Data;

@Data
public class RuleConditionResponse {
    private String label;
    private int conditionsLeft;

    public RuleConditionResponse(){}
    public RuleConditionResponse(String label, int conditionsLeft){
        this.label = label;
        this.conditionsLeft = conditionsLeft;
    }
}
