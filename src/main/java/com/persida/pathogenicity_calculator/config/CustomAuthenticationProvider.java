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
import org.springframework.core.env.Environment;
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

    @Autowired
    private JWTutils jwtUtils;
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
        String tokenValue = jwtUtils.getTokenFromAuth(username, password);
        if(tokenValue == null || tokenValue.equals("")){
            throw new BadCredentialsException("Invalid login details");
        }

        JWTHeaderAndPayloadData jwtData = jwtUtils.decodeAndValidateToken(tokenValue);
        if(jwtData == null){
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
}
