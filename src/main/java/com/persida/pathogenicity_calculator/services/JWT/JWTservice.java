package com.persida.pathogenicity_calculator.services.JWT;

import com.persida.pathogenicity_calculator.model.JWTHeaderAndPayloadData;
import com.persida.pathogenicity_calculator.repository.CustomUserDetails;

public interface JWTservice {
    String getTokenFromAuthAPI(String username, String password);
    JWTHeaderAndPayloadData decodeAndValidateTokenFromNativeAPI(String bearerToken);
    JWTHeaderAndPayloadData decodeAndValidateToken(String jwtToken);
    CustomUserDetails createUserIfFirstTimeLoginAndReturnCUD(JWTHeaderAndPayloadData jwtData, String role);
}

