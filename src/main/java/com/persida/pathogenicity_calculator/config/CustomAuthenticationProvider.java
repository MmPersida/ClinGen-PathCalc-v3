package com.persida.pathogenicity_calculator.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.persida.pathogenicity_calculator.model.JWTHeaderAndPayloadData;
import com.persida.pathogenicity_calculator.repository.CustomUserDetails;
import com.persida.pathogenicity_calculator.repository.entity.User;
import com.persida.pathogenicity_calculator.services.userServices.UserService;
import com.persida.pathogenicity_calculator.utils.HTTPSConnector;
import com.persida.pathogenicity_calculator.utils.StackTracePrinter;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static Logger logger = Logger.getLogger(CustomAuthenticationProvider.class);

    private JSONParser jsonParser;

    @Value("${navigation.indexPage}")
    private String indexPage;

    @Value("${genboreeAuthApi}")
    private String genboreeAuthApi;

    private final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----" +
            "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA11dsvlpgfUdNxwxbZ5nA" +
            "st9ePe85h+3o+DVUEZQ5riBoJsYd2+js8OliWggZebH0Lw8ifgzVl/PpYW6aThNP" +
            "yEax54Pp19tj9gYGpaPRxYs6wSP2jwESFamp+5Z6oac7/FIuqmD2cHqBSGAVPjiD" +
            "Zvo6ZRZPnieZNlK6F4bEuN/v3KCYyscqBH5nVvlaBRHY9JNJtDx3MkpQeseewxFZ" +
            "/xeVLqNt66PeNxow0x6eYcwNj7Pc5vPKwHqFbdzXOdIFOPWcVxgWYU4h7+AuAoll" +
            "Z6r9JgpT0Rc72gC9JIJc/RcsJXIATP+fRt5tjfoNECVq/+32p70nmJRi9ZYdONxu" +
            "uPLFxy3d1mZpRIZF0+HPNI3nNHh+z3rqK78r8NZUKkZvxKIS4Mg9Ayx9ZXVCBMPp" +
            "zy/UHh8PunqbMn8hfDoN4IhjAQwfOOn5eLig0C5qCENGMlPSHPqLjGUWYbugZM3A" +
            "U3XdmS8ShAagRaCRacN0NcojnKdOEcA56ZsWQdA2lOk530VhlUPM7l76LuQzYtPn" +
            "qzPTJ/6sZpfZ9/APrXElVLTI7s9B+lQsid3qw6gmk6RlIWDOzdObVKA8LHW5++HM" +
            "u9hIzJGyBcpj6NNQRU1z2dNJgDmZdKgJ1p9QPk/LWOGjitRc99YmQl5A3MFw3/NZ" +
            "JQpQH/UrFanJZep6sZOtyFsCAwEAAQ==" +
            "-----END PUBLIC KEY-----";

    private Algorithm preparedPKAlgorithm;

    @PostConstruct
    public void preparePublicKey() {
        String publicKey = new String(PUBLIC_KEY);
        String publicKeyPEM = publicKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.decodeBase64(publicKeyPEM);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            PublicKey rsa512PublicKey = keyFactory.generatePublic(keySpec);
            preparedPKAlgorithm = Algorithm.RSA512((RSAPublicKey) rsa512PublicKey, null);
            logger.info("Public key based algorithm ("+preparedPKAlgorithm.getName()+") prepared for token validation!");
        }catch(Exception e){
            StackTracePrinter.printStackTrace(e);
        }
    }

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if(username == null || password == null){
            throw new BadCredentialsException("Invalid login details");
        }

        logger.info("Attempting to authenticate user (username): "+username);
        String tokenValue = getTokenFromAuth(username, password);
        if(tokenValue == null || tokenValue.equals("")){
            throw new BadCredentialsException("Invalid login details");
        }

        JWTHeaderAndPayloadData jwtData = decodeAndValidateToken(tokenValue);
        if(jwtData.getUsername() == null || jwtData.getFName() == null || jwtData.getLName() == null){
            logger.error("Unable to validate token for user : "+username+" with the  provided token!");
            throw new BadCredentialsException("Unable to validate token for user : "+username+" with the  provided token!");
        }

        List<String> authorities = new ArrayList<String>();
        authorities.add(Constants.USER_ROLLE_USER);

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
            logger.info("User \""+username+"\" authenticated!");
            return new UsernamePasswordAuthenticationToken(cus, null,
                    authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        }
        logger.error("Unable to create CustomUserDetails for user: "+username);
        throw new BadCredentialsException("Invalid login details, unable to create CustomUserDetails for user: "+username);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private String getTokenFromAuth(String username, String password){
        HashMap<String,String> httpProperties = new HashMap<String,String>();
        httpProperties.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_APP_JSON);

        String tokenRequestURL = genboreeAuthApi;
        tokenRequestURL = tokenRequestURL.replace(Constants.USERNAME_PLACEHOLDER, username);

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

    private JWTHeaderAndPayloadData decodeAndValidateToken(String jwtToken) {
        if(preparedPKAlgorithm == null){
            logger.error("Unable to get the pubic key value, not prepared into type PublicKey!");
            return null;
        }
        try {
            DecodedJWT decodedJWT = JWT.decode(jwtToken);
            preparedPKAlgorithm.verify(decodedJWT);

            String decodedHeader = new String(Base64.decodeBase64(decodedJWT.getHeader()), StandardCharsets.UTF_8);
            String decodedPayload = new String(Base64.decodeBase64(decodedJWT.getPayload()), StandardCharsets.UTF_8);

            return new JWTHeaderAndPayloadData(decodedHeader, decodedPayload);
        } catch (SignatureVerificationException e) {
            logger.error("Token is invalid!");
            return null;
        } catch (Exception  e){
            logger.error(StackTracePrinter.printStackTrace(e));
            return null;
        }
    }
}
