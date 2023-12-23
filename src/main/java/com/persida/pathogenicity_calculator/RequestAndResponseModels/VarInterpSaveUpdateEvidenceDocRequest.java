package com.persida.pathogenicity_calculator.RequestAndResponseModels;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class VarInterpSaveUpdateEvidenceDocRequest {
    @NotNull
    @Pattern(regexp = "^CA[0-9]+$")
    private String caid;

    private String geneName;

    @Pattern(regexp = "^[0-9]+$")
    private Integer interpretationId;

    private String conditionId;

    @NotNull
    private String condition;

    private Integer inheritanceId;

    @NotNull
    private String inheritance;

    @NotNull
    private String cspecengineId;
}
