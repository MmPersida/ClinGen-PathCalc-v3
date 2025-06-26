package com.persida.pathogenicity_calculator.model;

import lombok.Data;

@Data
public class EvdTag {
    private String type;
    private String modifier;
    public EvdTag(String type, String modifier){
        this.type = type;
        this.modifier = modifier;
    }
}
