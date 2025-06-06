package com.persida.pathogenicity_calculator.config;

import com.persida.pathogenicity_calculator.model.JWTHeaderAndPayloadData;
import com.persida.pathogenicity_calculator.repository.CustomUserDetails;
import com.persida.pathogenicity_calculator.repository.entity.User;
import com.persida.pathogenicity_calculator.services.JWT.JWTservice;
import com.persida.pathogenicity_calculator.services.userServices.UserService;
import com.persida.pathogenicity_calculator.utils.StackTracePrinter;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static Logger logger = Logger.getLogger(CustomAuthenticationProvider.class);

    @Autowired
    private JWTservice jwtUtils;
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
        String tokenValue = jwtUtils.getTokenFromAuthAPI(username, password);
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

        //create CustomUserDetails from the token data, so we can start the session
        CustomUserDetails cus =  createCustomUserDetails(jwtData, authorities.get(0));

        if (cus != null) {
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

    private CustomUserDetails createCustomUserDetails(JWTHeaderAndPayloadData jwtData, String role){
        CustomUserDetails cud = null;
        if(userService == null) {
            return cud;
        }

        try {
            cud = (CustomUserDetails) userService.loadCustomUserDetailsByUsername(jwtData.getUsername());
            if(cud == null){
                //this is the first time the user is here, store his basic data to DB
                User user = new User(jwtData.getUsername(), jwtData.getFName(), jwtData.getLName(), role);
                userService.saveNewUser(user);
                cud = new CustomUserDetails(user);
            }
            return cud;
        }catch(Exception e){
            logger.error(StackTracePrinter.printStackTrace(e));
        }
        return cud;
    }
}
