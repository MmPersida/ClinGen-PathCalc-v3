package com.persida.pathogenicity_calculator.controllers;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.LoginRequest;
import com.persida.pathogenicity_calculator.model.JWTHeaderAndPayloadData;
import com.persida.pathogenicity_calculator.repository.CustomUserDetails;
import com.persida.pathogenicity_calculator.repository.entity.User;
import com.persida.pathogenicity_calculator.services.userServices.UserService;
import com.persida.pathogenicity_calculator.utils.HTTPSConnector;
import com.persida.pathogenicity_calculator.utils.StackTracePrinter;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/rest/login")
public class LoginController {
    private static Logger logger = Logger.getLogger(LoginController.class);

    private final String AUTHORIZATION = HttpHeaders.AUTHORIZATION;
    private final String PREFIX = "Bearer ";

    private JSONParser jsonParser;

    @Value("${navigation.indexPage}")
    private String indexPage;

    @Autowired
    private UserService userService;

    @PostMapping(value = "/processLoginCredentials",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public String processLoginCredentials(@RequestBody LoginRequest loginRequest){
        if(loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null){
            return null;
        }

        List<String> authorities = new ArrayList<String>();
        authorities.add(Constants.USER_ROLLE_USER);

        String tokenValue = getTokenFromAuth(loginRequest);
        if(tokenValue == null || tokenValue.equals("")){
            return null;
        }
        JWTHeaderAndPayloadData jwtData = decodeTokenNoValidation(tokenValue);

        CustomUserDetails cus = null;
        if(userService != null){
            try {
                cus = (CustomUserDetails) userService.loadCustomUserDetailsByUsername(jwtData.getUsername());
                if(cus == null){
                    User user = new User(jwtData.getUsername(), jwtData.getFName(), jwtData.getLName(), authorities.get(0));
                    userService.saveNewUser(user);
                    cus = new CustomUserDetails(user);
                }
            }catch(Exception e){
                logger.error(StackTracePrinter.printStackTrace(e));
            }
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(cus, null,
                    authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        return "views/pc_main.html";
    }

    private String getTokenFromAuth(LoginRequest loginRequest){
        HashMap<String,String> httpProperties = new HashMap<String,String>();
        httpProperties.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_APP_JSON);

        String tokenRequestURL = "https://genboree.org/auth/usr/gb:"+loginRequest.getUsername()+"/auth";

        JSONObject obj = new JSONObject();
        obj.put("type","plain");
        obj.put("val",loginRequest.getPassword());
        String jsonData = obj.toJSONString();

        HTTPSConnector https = new HTTPSConnector();
        String response = https.sendHttpsRequest(tokenRequestURL, Constants.HTTP_POST, jsonData, httpProperties);
        if(response == null || response.equals("")){
            return null;
        }

        String jwt = null;
        try{
            if(jsonParser == null){
                jsonParser = new JSONParser();
            }
            JSONObject headerObj = (JSONObject) jsonParser.parse(response);
            JSONObject datObj = (JSONObject) headerObj.get("data");
            jwt = String.valueOf(datObj.get("jwt"));
        }catch(Exception e){
            logger.error(StackTracePrinter.printStackTrace(e));
            return null;
        }
        return jwt;
    }

    private JWTHeaderAndPayloadData decodeTokenNoValidation(String jwtToken) {
        String[] chunks = jwtToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        return new JWTHeaderAndPayloadData(header, payload);
    }
}
