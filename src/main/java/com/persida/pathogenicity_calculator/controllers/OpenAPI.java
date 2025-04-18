package com.persida.pathogenicity_calculator.controllers;

import com.persida.pathogenicity_calculator.config.JWTutils;
import com.persida.pathogenicity_calculator.model.JWTHeaderAndPayloadData;
import com.persida.pathogenicity_calculator.model.openAPI.ClassificationsResponse;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.RequestAuthData;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.ClassRequest;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.TokenResponse;
import com.persida.pathogenicity_calculator.services.openAPI.OpenAPIService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rest/pc_api")
public class OpenAPI {
    private static Logger logger = Logger.getLogger(CalculatorController.class);

    @Autowired
    private JWTutils jwtUtils;
    @Autowired
    private OpenAPIService openAPIService;

    @PostMapping(value = "/tokenRequest",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public TokenResponse tokenRequest(@RequestBody RequestAuthData requestAuthData){
        return openAPIService.tokenRequest(requestAuthData);
    }

    @PostMapping(value = "/class",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ClassificationsResponse classifications(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String tokenValue,
                                                   @RequestBody ClassRequest classRequest){

        JWTHeaderAndPayloadData jwtData = jwtUtils.decodeAndValidateToken(tokenValue);
        if(jwtData == null){
            return new ClassificationsResponse("Unable to validate token, please check is the token expiration date passed!", Constants.NAME_FORBIDDEN);
        }

        if(classRequest.getCaid() == null || classRequest.getCaid().equals("")){
            return new ClassificationsResponse("Invalid CAID provided!", Constants.NAME_INVALID);
        }
        return openAPIService.classifications(classRequest, jwtData.getUsername());
    }
}
