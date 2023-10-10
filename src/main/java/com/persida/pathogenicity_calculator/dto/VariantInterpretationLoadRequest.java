package com.persida.pathogenicity_calculator.dto;


import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public  class VariantInterpretationLoadRequest {
    @Pattern(regexp = "^CA[0-9]+$")
    private String caid;
}
