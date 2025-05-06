package com.persida.pathogenicity_calculator.services.openAPI;

import com.persida.pathogenicity_calculator.model.openAPI.*;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.*;

public interface OpenAPIService {
    SRVCResponse srvc();
    TokenResponse tokenRequest(RequestAuthData requestAuthData);
    ClassificationsResponse allClassificationsForUser(String username);
    ClassificationsResponse classificationsForVariant(ClassByVariantRequest classRequest, String username);
    ClassificationResponse classificationById(ClassByIdRequest classByIdReq, String username);
    DiseasesResponse getDiseasesLike(String partialDiseaseTerm);
    MOIResponse getModesOfInheritance();
    SpecificationsResponse getSpecifications();
    ClassificationResponse createClassification(CreateUpdateClassWithEvidenceRequest createClassRequest, String username);
    ClassificationResponse updateClassification(CreateUpdateClassWithEvidenceRequest updateClassRequest, String username);
    ClassificationResponse deleteClassification(ClassificationIDRequest classIdRequest);

}
