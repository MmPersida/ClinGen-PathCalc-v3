package com.persida.pathogenicity_calculator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConditionsTermAndIdDTO {
    private String conditionId;
    private String term;

    public ConditionsTermAndIdDTO(String conditionId, String term){
        this.conditionId = conditionId;
        this.term = term;
    }
}
