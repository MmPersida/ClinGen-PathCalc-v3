package com.persida.pathogenicity_calculator.RequestAndResponseModels;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
