package com.persida.pathogenicity_calculator.services.openAPI;

import com.persida.pathogenicity_calculator.model.openAPI.ClassificationsResponse;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.ClassRequest;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.RequestAuthData;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.TokenResponse;

public interface OpenAPIService {
    TokenResponse tokenRequest(RequestAuthData requestAuthData);
    ClassificationsResponse classifications(ClassRequest classRequest, String username);
}
