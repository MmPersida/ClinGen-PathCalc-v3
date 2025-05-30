package com.persida.pathogenicity_calculator.model.openAPI.requestModels;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EvideneTagRequest {
    @NotNull
    private String type;
    @NotNull
    private String modifier;
    //private String fullLabelForFE;
    private String summary;

    public EvideneTagRequest(){}

    public EvideneTagRequest(String type, String modifier){
        this.type = type;
        this.modifier = modifier;
    }
}
