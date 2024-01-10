package com.persida.pathogenicity_calculator.config;

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
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static Logger logger = Logger.getLogger(CustomAuthenticationProvider.class);

    private JSONParser jsonParser;

    @Value("${navigation.indexPage}")
    private String indexPage;

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if(username == null || password == null){
            return null;
        }

        List<String> authorities = new ArrayList<String>();
        authorities.add(Constants.USER_ROLLE_USER);

        String tokenValue = getTokenFromAuth(username, password);
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

        if (cus != null) {
            return new UsernamePasswordAuthenticationToken(cus, password,
                    authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private boolean shouldAuthenticateAgainstThirdPartySystem(){
        return true;
    }

    private String getTokenFromAuth(String username, String password){
        HashMap<String,String> httpProperties = new HashMap<String,String>();
        httpProperties.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_APP_JSON);

        String tokenRequestURL = "https://genboree.org/auth/usr/gb:"+username+"/auth";

        JSONObject obj = new JSONObject();
        obj.put("type","plain");
        obj.put("val",password);
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
