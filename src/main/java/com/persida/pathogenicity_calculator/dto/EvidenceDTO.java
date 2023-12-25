package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class EvidenceDTO {
    private Integer evidenceId;
    @NotNull
    @NotBlank(message = "Evidence type must not be blank!")
    private String type;
    private String modifier;
    private String fullLabelForFE;
    private String summary;

    public EvidenceDTO(){};

    public EvidenceDTO(String type, String modifier){
        this.type = type;
        this.modifier = modifier;
    };

    public EvidenceDTO(Integer evidenceId, String type, String modifier, String fullLabelForFE, String summary){
        this.evidenceId = evidenceId;
        this.type = type;
        this.modifier = modifier;
        this.fullLabelForFE = fullLabelForFE;
        if(summary != null){
            this.summary = summary;
        }
    };
}
