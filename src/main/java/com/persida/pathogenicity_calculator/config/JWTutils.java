package com.persida.pathogenicity_calculator.config;

import com.persida.pathogenicity_calculator.model.JWTHeaderAndPayloadData;

public interface JWTutils {
    String getTokenFromAuth(String username, String password);
    JWTHeaderAndPayloadData decodeAndValidateTokenFromNativeAPI(String bearerToken);
    JWTHeaderAndPayloadData decodeAndValidateToken(String jwtToken);
}

