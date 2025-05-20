package com.persida.pathogenicity_calculator.RequestAndResponseModels;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class VarInterpUpdateFinalCallRequest {
    @Pattern(regexp = "^[0-9]+$")
    private Integer interpretationId;
    private Integer finalCallId;

    public VarInterpUpdateFinalCallRequest(){};

    public VarInterpUpdateFinalCallRequest(Integer interpretationId, Integer finalCallId){
        this.interpretationId = interpretationId;
        this.finalCallId = finalCallId;
    }
}
