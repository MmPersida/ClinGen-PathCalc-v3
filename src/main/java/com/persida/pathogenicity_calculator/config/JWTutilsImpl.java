package com.persida.pathogenicity_calculator.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.persida.pathogenicity_calculator.model.JWTHeaderAndPayloadData;
import com.persida.pathogenicity_calculator.utils.HTTPSConnector;
import com.persida.pathogenicity_calculator.utils.StackTracePrinter;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

@Component
public class JWTutilsImpl implements JWTutils{

    private static Logger logger = Logger.getLogger(JWTutilsImpl.class);

    @Value("${genboreeAuthApi}")
    private String genboreeAuthApi;

    private JSONParser jsonParser;
    private Algorithm preparedPKAlgorithm;

    private final String PUBLIC_KEY_PROD = "-----BEGIN PUBLIC KEY-----" +
            "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEApe4KaS5HDDreGvSZimkG" +
            "Un24bo8PJbHQO30Cei+y5yyD8uAcVMF7T1FYsg+Ku2DVjUgqdG1yMlXFNzromG+q" +
            "8CQs+J9VMsv5SLwelBA5k1udze8hx3tuO0vdJILe7RA3cHEG1TUig+nRwQGpVGvZ" +
            "d2ZfbI7VH6M2E6vB1Os7PqWjkltwH7cx7hgg8qOxuhFXrGynLqKmYzpSabmQRSTD" +
            "oXrK+MBd/5hFI6c4Pm6U+hDLV9zFa7G/E1uYTGUqXmj40lt/myyBJnG4Ohg9xUO9" +
            "XXEHxGfoKQsT5teNgtm0cXEGMVQaYNyswb6s92Z/7Kjsxmkhxk4/rsBB6/BjqVt2" +
            "WroE6kuHNnfEQ9JJ8vI1pRJJcQODzPRiIEfC0PSxBG06r3suhpzI4N1II8J0yPji" +
            "6/lb67/HWLQ48kYn5nCPucUSjxJho1dvYeIj6bkJ1HMwZewT7zz9Df4lmTAg0wEu" +
            "r7kb6f/zQUQ8UNRnqlB9KoIevVGJyrqDNv/ALVljZQsK2G3hHK3A78IGoVhc3ORU" +
            "d255OC+jpNyofES9H6u0xntsZh7rPEB4CMqXLLp8U0rj0K3ZezngPm+gzOXQZK8f" +
            "ch47L7oUpOuXUmm3l7nZUHH5On+TUwDoyUuJAjwiAgFyzb4AfHXIAk1Gd6NG80JH" +
            "F/vYsJMoFI2MKra2QCBdPRkCAwEAAQ==" +
            "-----END PUBLIC KEY-----";

    private final String PUBLIC_KEY_TEST = "-----BEGIN PUBLIC KEY-----" +
            "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEApe4KaS5HDDreGvSZimkG" +
            "Un24bo8PJbHQO30Cei+y5yyD8uAcVMF7T1FYsg+Ku2DVjUgqdG1yMlXFNzromG+q" +
            "8CQs+J9VMsv5SLwelBA5k1udze8hx3tuO0vdJILe7RA3cHEG1TUig+nRwQGpVGvZ" +
            "d2ZfbI7VH6M2E6vB1Os7PqWjkltwH7cx7hgg8qOxuhFXrGynLqKmYzpSabmQRSTD" +
            "oXrK+MBd/5hFI6c4Pm6U+hDLV9zFa7G/E1uYTGUqXmj40lt/myyBJnG4Ohg9xUO9" +
            "XXEHxGfoKQsT5teNgtm0cXEGMVQaYNyswb6s92Z/7Kjsxmkhxk4/rsBB6/BjqVt2" +
            "WroE6kuHNnfEQ9JJ8vI1pRJJcQODzPRiIEfC0PSxBG06r3suhpzI4N1II8J0yPji" +
            "6/lb67/HWLQ48kYn5nCPucUSjxJho1dvYeIj6bkJ1HMwZewT7zz9Df4lmTAg0wEu" +
            "r7kb6f/zQUQ8UNRnqlB9KoIevVGJyrqDNv/ALVljZQsK2G3hHK3A78IGoVhc3ORU" +
            "d255OC+jpNyofES9H6u0xntsZh7rPEB4CMqXLLp8U0rj0K3ZezngPm+gzOXQZK8f" +
            "ch47L7oUpOuXUmm3l7nZUHH5On+TUwDoyUuJAjwiAgFyzb4AfHXIAk1Gd6NG80JH" +
            "F/vYsJMoFI2MKra2QCBdPRkCAwEAAQ==" +
            "-----END PUBLIC KEY-----";

    @Autowired
    private Environment environment;

    @PostConstruct
    public void preparePublicKey() {
        String profile = (this.environment.getActiveProfiles())[0];
        String publicKey = null;

        if(profile.equals("prod")){
            publicKey = new String(PUBLIC_KEY_PROD);
            logger.info("Loaded PROD PK!");
        }else{
            publicKey = new String(PUBLIC_KEY_TEST);
            logger.info("Loaded TEST PK!");
        }

        if(publicKey == null || publicKey.equals("")){
            return;
        }

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

    @Override
    public String getTokenFromAuth(String username, String password){
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

    @Override
    public JWTHeaderAndPayloadData decodeAndValidateToken(String jwtToken) {
        if(preparedPKAlgorithm == null){
            logger.error("Unable to get the pubic key value, not prepared into type PublicKey!");
            return null;
        }
        try {
            DecodedJWT decodedJWT = JWT.decode(jwtToken);
            preparedPKAlgorithm.verify(decodedJWT);

            String decodedHeader = new String(Base64.decodeBase64(decodedJWT.getHeader()), StandardCharsets.UTF_8);
            String decodedPayload = new String(Base64.decodeBase64(decodedJWT.getPayload()), StandardCharsets.UTF_8);

            JWTHeaderAndPayloadData jwtData =  new JWTHeaderAndPayloadData(decodedHeader, decodedPayload);
            if(jwtData == null || jwtData.getUsername() == null || jwtData.getFName() == null || jwtData.getLName() == null){
                throw new Exception("Ubale to get user data from the previously validated token!");
            }
            return jwtData;
        } catch (SignatureVerificationException e) {
            logger.error("Token is invalid!");
            return null;
        } catch (Exception  e){
            logger.error(StackTracePrinter.printStackTrace(e));
            return null;
        }
    }
}
