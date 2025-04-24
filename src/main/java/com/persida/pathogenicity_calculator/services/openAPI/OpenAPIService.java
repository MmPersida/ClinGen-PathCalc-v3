package com.persida.pathogenicity_calculator.services.openAPI;

import com.persida.pathogenicity_calculator.model.openAPI.ClassificationResponse;
import com.persida.pathogenicity_calculator.model.openAPI.ClassificationsResponse;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.ClassByIdRequest;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.ClassByVariantRequest;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.RequestAuthData;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.TokenResponse;

public interface OpenAPIService {
    TokenResponse tokenRequest(RequestAuthData requestAuthData);
    ClassificationsResponse allClassificationsForUser(String username);
    ClassificationsResponse classificationsForVariant(ClassByVariantRequest classRequest, String username);
    ClassificationResponse classificationById(ClassByIdRequest classByIdReq, String username);
}
