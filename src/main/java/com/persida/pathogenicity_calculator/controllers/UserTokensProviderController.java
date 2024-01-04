package com.persida.pathogenicity_calculator.controllers;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.UserTokenRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.UserTokenResponse;
import com.persida.pathogenicity_calculator.repository.CustomUserDetails;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "Authentication")
@RestController
@RequestMapping(value = "/rest/userTokens")
public class UserTokensProviderController {

    private static Logger logger = Logger.getLogger(UserTokensProviderController.class);

    private final AuthenticationManager authenticationManager;

    public UserTokensProviderController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(value = "/authenticateAndProvideToken",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserTokenResponse authenticateUserAndCreateToken(@RequestBody UserTokenRequest userTokenRequest){
        if(userTokenRequest == null || userTokenRequest.getUsername() == null || userTokenRequest.getPassword() == null){
            return null;
        }

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userTokenRequest.getUsername(), userTokenRequest.getPassword())
        );
        CustomUserDetails customUserDetails = (CustomUserDetails) authenticate.getPrincipal();

        if(customUserDetails == null || customUserDetails.getUsername() == null){
            return null;
        }

        String token = getJWTToken(customUserDetails);
        UserTokenResponse userTR = new UserTokenResponse();
        userTR.setUsername(userTokenRequest.getUsername());
        userTR.setToken(token);
        return userTR;
    }

    @PostMapping(value = "/login",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public String login(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal == null){
            return "Error: You are not a known user!";
        }

        String message = "Hello user: ";
        if (principal instanceof UserDetails) {
            return message+((UserDetails)principal).getUsername();
        } else {
            return message+principal.toString();
        }
    }

    private String getJWTToken(CustomUserDetails customUserDetails) {
        List<GrantedAuthority> grantedAuthorities = (List<GrantedAuthority>) customUserDetails.getAuthorities();

        String token = Jwts
                .builder()
                .setSubject(customUserDetails.getUsername())
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1800000))
                .signWith(SignatureAlgorithm.HS512,
                        Constants.JWT_SECRET_KEY.getBytes()).compact();
        return "Bearer " + token;
    }
}
