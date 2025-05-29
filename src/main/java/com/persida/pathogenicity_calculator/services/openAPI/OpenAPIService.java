package com.persida.pathogenicity_calculator.services.openAPI;

import com.persida.pathogenicity_calculator.model.openAPI.*;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.*;

public interface OpenAPIService {
    SRVCResponse srvc();
    TokenResponse tokenRequest(RequestAuthData requestAuthData);
    ClassificationsResponse allClassificationsForUser(String username, boolean useHighDetail);
    ClassificationsResponse classificationsForVariant(ClassByVariantRequest classRequest, String username, boolean useHighDetail);
    ClassificationResponse classificationById(ClassByIdRequest classByIdReq, String username, boolean useHighDetail);
    DiseasesResponse getDiseasesLike(String partialDiseaseTerm);
    MOIResponse getModesOfInheritance();
    SpecificationsResponse getSpecifications();
    ClassificationResponse createClassification(CreateUpdateClassWithEvidenceRequest createClassRequest, String username);
    ClassificationResponse updateClassification(CreateUpdateClassWithEvidenceRequest updateClassRequest, String username);
    ClassificationResponse deleteClassification(ClassificationIDRequest classIdRequest);
    ClassificationResponse addEvidence(AddEvidencesRequest evdRequest, String username);
    ClassificationResponse removeEvidence(RemoveEvidencesRequest evdRequest, String username);
}
