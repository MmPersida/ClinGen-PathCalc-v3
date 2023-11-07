package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class VarInterpSaveUpdateEvidenceDocRequest {
    @Pattern(regexp = "^CA[0-9]+$")
    private String caid;
    @Pattern(regexp = "^[0-9]+$")
    private Integer interpretationId;
    private Integer conditionId;
    private String condition;
    private Integer inheritanceId;
    private String inheritance;
}
