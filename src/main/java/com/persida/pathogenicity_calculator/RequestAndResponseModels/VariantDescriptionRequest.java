package com.persida.pathogenicity_calculator.RequestAndResponseModels;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class VariantDescriptionRequest {
    @NotNull
    @Pattern(regexp = "^[0-9]+$")
    private Integer interpretationId;
    @NotNull
    private String viDescription;
}
