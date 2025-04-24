package com.persida.pathogenicity_calculator.model.openAPI.requestModels;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class ClassByIdRequest {
    @NotNull
    @Pattern(regexp = "^[0-9]+$")
    private Integer classId;

    public ClassByIdRequest(){}

    public ClassByIdRequest(Integer classId){
        this.classId = classId;
    }
}
