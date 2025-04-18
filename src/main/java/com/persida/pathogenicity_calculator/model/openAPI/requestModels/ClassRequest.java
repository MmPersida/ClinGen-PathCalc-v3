package com.persida.pathogenicity_calculator.model.openAPI.requestModels;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class ClassRequest {
    @NotNull
    @Pattern(regexp = "^CA[0-9]+$")
    private String caid;
}
