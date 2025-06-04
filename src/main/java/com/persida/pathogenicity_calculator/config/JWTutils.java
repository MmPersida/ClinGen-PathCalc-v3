package com.persida.pathogenicity_calculator.config;

import com.persida.pathogenicity_calculator.model.JWTHeaderAndPayloadData;

public interface JWTutils {
    String getTokenFromAuthAPI(String username, String password);
    JWTHeaderAndPayloadData decodeAndValidateTokenFromNativeAPI(String bearerToken);
    JWTHeaderAndPayloadData decodeAndValidateToken(String jwtToken);
}

