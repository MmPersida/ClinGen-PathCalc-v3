package com.persida.pathogenicity_calculator.model.openAPI.requestModels;

import com.sun.istack.NotNull;
import lombok.Data;

@Data
public class RequestAuthData {
    @NotNull
    private String username;
    @NotNull
    private String pass;
}
