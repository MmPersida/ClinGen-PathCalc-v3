package com.persida.pathogenicity_calculator.services.JWT;

import com.persida.pathogenicity_calculator.model.JWTHeaderAndPayloadData;

public interface JWTservice {
    String getTokenFromAuthAPI(String username, String password);
    JWTHeaderAndPayloadData decodeAndValidateTokenFromNativeAPI(String bearerToken);
    JWTHeaderAndPayloadData decodeAndValidateToken(String jwtToken);
}

