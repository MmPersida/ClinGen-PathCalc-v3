package com.persida.pathogenicity_calculator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConditionsTermAndIdDTO {
    private int conditionId;
    private String term;

    public ConditionsTermAndIdDTO(int conditionId, String term){
        this.conditionId = conditionId;
        this.term = term;
    }
}
