package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

@Data
public class VariantInterpretationSaveResponse {
    private Integer interpretationId;
    private String message;

    public VariantInterpretationSaveResponse(Integer newInterpretationId){
        this.interpretationId = newInterpretationId;
    }

    public VariantInterpretationSaveResponse(Integer newInterpretationId, String message){
        this.interpretationId = newInterpretationId;
        this.message = message;
    }
}
