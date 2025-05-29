package com.persida.pathogenicity_calculator.controllers;

import com.persida.pathogenicity_calculator.config.JWTutils;
import com.persida.pathogenicity_calculator.model.Detail;
import com.persida.pathogenicity_calculator.model.JWTHeaderAndPayloadData;
import com.persida.pathogenicity_calculator.model.openAPI.*;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.*;
import com.persida.pathogenicity_calculator.services.openAPI.OpenAPIService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
public class OpenAPI {
    private static Logger logger = Logger.getLogger(CalculatorController.class);

    @Autowired
    private JWTutils jwtUtils;
    @Autowired
    private OpenAPIService openAPIService;

    private String validateTokenErrorMsg = "Unable to validate token. Please check is the token value sent with the Bearer key word or has the expiration date passed!";

    @RequestMapping(value = "/srvc", method= RequestMethod.GET)
    public SRVCResponse srvc(){
        return openAPIService.srvc();
    }

    @PostMapping(value = "/tokenRequest",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public TokenResponse tokenRequest(@RequestBody RequestAuthData requestAuthData){
        return openAPIService.tokenRequest(requestAuthData);
    }

    @RequestMapping(value = "/classifications", method= RequestMethod.GET)
    public ClassificationsResponse allClassificationsForUser(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String tokenValue,
                                                             @RequestParam(required=false) Detail detail){
        JWTHeaderAndPayloadData jwtData = jwtUtils.decodeAndValidateTokenFromNativeAPI(tokenValue);
        if(jwtData == null){
            return new ClassificationsResponse(validateTokenErrorMsg, Constants.NAME_FORBIDDEN);
        }

        boolean useHighDetail = false;
        if(detail != null && detail.equals(Detail.high)){
            useHighDetail = true;
        }

        return openAPIService.allClassificationsForUser(jwtData.getUsername(), useHighDetail);
    }

    @RequestMapping(value = "/classifications/variant/{caid}", method= RequestMethod.GET)
    public ClassificationsResponse classificationsForVariant(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String tokenValue,
                                                   @PathVariable String caid,
                                                   @RequestParam(required=false) Detail detail){
        if(caid == null || caid.isEmpty()){
            return new ClassificationsResponse("Invalid CAID provided!", Constants.NAME_INVALID);
        }

        boolean useHighDetail = false;
        if(detail != null && detail.equals(Detail.high)){
            useHighDetail = true;
        }

        JWTHeaderAndPayloadData jwtData = jwtUtils.decodeAndValidateTokenFromNativeAPI(tokenValue);
        if(jwtData == null){
            return new ClassificationsResponse(validateTokenErrorMsg, Constants.NAME_FORBIDDEN);
        }

        return openAPIService.classificationsForVariant(new ClassByVariantRequest(caid), jwtData.getUsername(), useHighDetail);
    }

    @RequestMapping(value = "/classification/{classId}", method= RequestMethod.GET)
    public ClassificationResponse classificationById(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String tokenValue,
                                                     @PathVariable Integer classId,
                                                     @RequestParam(required=false) Detail detail){
        if(classId == null || classId <= 0){
            return new ClassificationResponse("Invalid classification ID provided!", Constants.NAME_INVALID);
        }

        JWTHeaderAndPayloadData jwtData = jwtUtils.decodeAndValidateTokenFromNativeAPI(tokenValue);
        if(jwtData == null){
            return new ClassificationResponse(validateTokenErrorMsg, Constants.NAME_FORBIDDEN);
        }

        boolean useHighDetail = false;
        if(detail != null && detail.equals(Detail.high)){
            useHighDetail = true;
        }

        ClassificationResponse cr = openAPIService.classificationById(new ClassByIdRequest(classId), jwtData.getUsername(), useHighDetail);
        return cr;
    }

    @RequestMapping(value = "/diseases/{partialDiseaseTerm}", method= RequestMethod.GET)
    public DiseasesResponse getDiseasesLike(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String tokenValue,
                                            @PathVariable String partialDiseaseTerm){
        if(partialDiseaseTerm == null || partialDiseaseTerm.isEmpty() || partialDiseaseTerm.length() < 4){
            return new DiseasesResponse("Input data invalid!", Constants.NAME_INVALID);
        }

        JWTHeaderAndPayloadData jwtData = jwtUtils.decodeAndValidateTokenFromNativeAPI(tokenValue);
        if(jwtData == null){
            return new DiseasesResponse(validateTokenErrorMsg, Constants.NAME_FORBIDDEN);
        }

        return openAPIService.getDiseasesLike(partialDiseaseTerm);
    }

    @RequestMapping(value = "/modesOfInheritance", method= RequestMethod.GET)
    public MOIResponse getModesOfInheritance(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String tokenValue){
        JWTHeaderAndPayloadData jwtData = jwtUtils.decodeAndValidateTokenFromNativeAPI(tokenValue);
        if(jwtData == null){
            return new MOIResponse(validateTokenErrorMsg, Constants.NAME_FORBIDDEN);
        }
        return openAPIService.getModesOfInheritance();
    }

    @RequestMapping(value = "/specifications", method= RequestMethod.GET)
    public SpecificationsResponse getSpecifications(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String tokenValue){
        JWTHeaderAndPayloadData jwtData = jwtUtils.decodeAndValidateTokenFromNativeAPI(tokenValue);
        if(jwtData == null){
            return new SpecificationsResponse(validateTokenErrorMsg, Constants.NAME_FORBIDDEN);
        }
        return openAPIService.getSpecifications();
    }

    @PostMapping(value = "/classification/create",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ClassificationResponse createClassification(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String tokenValue,
                                                       @RequestBody CreateUpdateClassWithEvidenceRequest createClassRequest){
        JWTHeaderAndPayloadData jwtData = jwtUtils.decodeAndValidateTokenFromNativeAPI(tokenValue);
        if(jwtData == null){
            return new ClassificationResponse(validateTokenErrorMsg, Constants.NAME_FORBIDDEN);
        }
        return openAPIService.createClassification(createClassRequest, jwtData.getUsername());
    }

    @PutMapping(value = "/classification/update",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ClassificationResponse updateClassification(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String tokenValue,
                                                       @RequestBody CreateUpdateClassWithEvidenceRequest updateClassRequest){
        JWTHeaderAndPayloadData jwtData = jwtUtils.decodeAndValidateTokenFromNativeAPI(tokenValue);
        if(jwtData == null){
            return new ClassificationResponse(validateTokenErrorMsg, Constants.NAME_FORBIDDEN);
        }
        return openAPIService.updateClassification(updateClassRequest, jwtData.getUsername());
    }

    @PostMapping(value = "/classification/delete",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ClassificationResponse deleteClassification(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String tokenValue,
                                                       @RequestBody ClassificationIDRequest classIdRequest){
        JWTHeaderAndPayloadData jwtData = jwtUtils.decodeAndValidateTokenFromNativeAPI(tokenValue);
        if(jwtData == null){
            return new ClassificationResponse(validateTokenErrorMsg, Constants.NAME_FORBIDDEN);
        }
        return openAPIService.deleteClassification(classIdRequest);
    }

    @PostMapping(value = "/classification/addEvidence",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ClassificationResponse addEvidence(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String tokenValue,
                                                 @RequestBody AddEvidencesRequest evdRequest){
        JWTHeaderAndPayloadData jwtData = jwtUtils.decodeAndValidateTokenFromNativeAPI(tokenValue);
        if(jwtData == null){
            return new ClassificationResponse(validateTokenErrorMsg, Constants.NAME_FORBIDDEN);
        }
        return openAPIService.addEvidence(evdRequest, jwtData.getUsername());
    }

    @PostMapping(value = "/classification/removeEvidence",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ClassificationResponse removeEvidence(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String tokenValue,
                                                       @RequestBody RemoveEvidencesRequest evdRequest){
        JWTHeaderAndPayloadData jwtData = jwtUtils.decodeAndValidateTokenFromNativeAPI(tokenValue);
        if(jwtData == null){
            return new ClassificationResponse(validateTokenErrorMsg, Constants.NAME_FORBIDDEN);
        }
        return openAPIService.removeEvidence(evdRequest, jwtData.getUsername());
    }
}
