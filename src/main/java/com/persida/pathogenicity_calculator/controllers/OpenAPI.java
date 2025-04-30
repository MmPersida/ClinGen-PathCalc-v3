package com.persida.pathogenicity_calculator.controllers;

import com.persida.pathogenicity_calculator.config.JWTutils;
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
    public ClassificationsResponse allClassificationsForUser(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String tokenValue){
        JWTHeaderAndPayloadData jwtData = jwtUtils.decodeAndValidateToken(tokenValue);
        if(jwtData == null){///pcalc/api/srvc
            return new ClassificationsResponse("Unable to validate token, please check is the token expiration date passed!", Constants.NAME_FORBIDDEN);
        }

        return openAPIService.allClassificationsForUser(jwtData.getUsername());
    }

    @RequestMapping(value = "/classifications/variant/{caid}", method= RequestMethod.GET)
    public ClassificationsResponse classificationsForVariant(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String tokenValue,
                                                   @PathVariable String caid){
        if(caid == null || caid.isEmpty()){
            return new ClassificationsResponse("Invalid CAID provided!", Constants.NAME_INVALID);
        }

        JWTHeaderAndPayloadData jwtData = jwtUtils.decodeAndValidateToken(tokenValue);
        if(jwtData == null){
            return new ClassificationsResponse("Unable to validate token, please check is the token expiration date passed!", Constants.NAME_FORBIDDEN);
        }

        return openAPIService.classificationsForVariant(new ClassByVariantRequest(caid), jwtData.getUsername());
    }

    @RequestMapping(value = "/classification/{classId}", method= RequestMethod.GET)
    public ClassificationResponse classificationById(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String tokenValue,
                                                     @PathVariable Integer classId){
        if(classId == null || classId <= 0){
            return new ClassificationResponse("Invalid classification ID provided!", Constants.NAME_INVALID);
        }

        JWTHeaderAndPayloadData jwtData = jwtUtils.decodeAndValidateToken(tokenValue);
        if(jwtData == null){
            return new ClassificationResponse("Unable to validate token, please check is the token expiration date passed!", Constants.NAME_FORBIDDEN);
        }

        ClassificationResponse cr = openAPIService.classificationById(new ClassByIdRequest(classId), jwtData.getUsername());
        return cr;
    }

    @RequestMapping(value = "/diseases/{partialDiseaseTerm}", method= RequestMethod.GET)
    public DiseasesResponse getDiseasesLike(@PathVariable String partialDiseaseTerm){
        if(partialDiseaseTerm == null || partialDiseaseTerm.isEmpty()){
            return null;
        }
        return openAPIService.getDiseasesLike(partialDiseaseTerm);
    }

    @RequestMapping(value = "/modesOfInheritance", method= RequestMethod.GET)
    public MOIResponse getModesOfInheritance(){
        return openAPIService.getModesOfInheritance();
    }

    @PostMapping(value = "/classification/create",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public String createClassification(@RequestBody CreateClassWithEvidenceRequest requestAuthData){
        return openAPIService.createClassification();
    }

    @PutMapping(value = "/classification/update",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public String updateClassification(@RequestBody CreateClassWithEvidenceRequest requestAuthData){
        return openAPIService.updateClassification();
    }
}
