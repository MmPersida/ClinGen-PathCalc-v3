package com.persida.pathogenicity_calculator.model.openAPI.requestModels;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class ClassificationIDRequest {
    @NotNull
    @Pattern(regexp = "^[0-9]+$")
    private Integer classificationId;
}
