package com.persida.pathogenicity_calculator.dto;


import com.sun.istack.NotNull;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public  class VariantInterpretationIDRequest {
    @NotNull
    @Pattern(regexp = "^[0-9]+$")
    private Integer interpretationId;
}
