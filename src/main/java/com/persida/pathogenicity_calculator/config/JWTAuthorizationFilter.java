package com.persida.pathogenicity_calculator.config;

import com.persida.pathogenicity_calculator.model.JWTHeaderAndPayloadData;
import com.persida.pathogenicity_calculator.repository.CustomUserDetails;
import com.persida.pathogenicity_calculator.repository.UserRepository;
import com.persida.pathogenicity_calculator.repository.entity.User;
import com.persida.pathogenicity_calculator.services.userServices.UserService;
import com.persida.pathogenicity_calculator.utils.StackTracePrinter;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import io.jsonwebtoken.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private static Logger logger = Logger.getLogger(JWTAuthorizationFilter.class);

    private final String AUTHORIZATION = HttpHeaders.AUTHORIZATION;
    private final String PREFIX = "Bearer ";

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            if (checkJWTToken(request, response)) {
                JWTHeaderAndPayloadData jwtData = decodeTokenNoValidation(request);
                if (jwtData != null ) {
                    setUpSpringAuthentication(request, jwtData);
                } else {
                    SecurityContextHolder.clearContext();
                }
            }
            chain.doFilter(request, response);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        }
    }

    private JWTHeaderAndPayloadData decodeTokenNoValidation(HttpServletRequest request) {
        String jwtToken = request.getHeader(AUTHORIZATION).replace(PREFIX, "");

        String[] chunks = jwtToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        return new JWTHeaderAndPayloadData(header, payload);
    }

    /**
     * Authentication method in Spring flow
     *
     * @param
     */
    private void setUpSpringAuthentication(HttpServletRequest req,  JWTHeaderAndPayloadData jwtData) {
        @SuppressWarnings("unchecked")
        List<String> authorities = new ArrayList<String>();
        authorities.add(Constants.USER_ROLLE_USER);

        String userName = jwtData.getUsername();

        CustomUserDetails cus = null;
        if(userService != null){
            try {
                cus = (CustomUserDetails) userService.loadCustomUserDetailsByUsername(userName);
                if(cus == null){
                    User user = new User(jwtData.getUsername(), jwtData.getFName(), jwtData.getLName(), authorities.get(0));
                    userService.saveNewUser(user);
                    cus = new CustomUserDetails(user);
                }
            }catch(Exception e){
                logger.error(StackTracePrinter.printStackTrace(e));
            }
        }

        UsernamePasswordAuthenticationToken auth = null;
        if(cus != null){
            auth = new UsernamePasswordAuthenticationToken(cus, null,
                    authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        }else{
            auth = new UsernamePasswordAuthenticationToken(userName, null,
                    authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        }

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
    }

    private boolean checkJWTToken(HttpServletRequest request, HttpServletResponse res) {
        String authenticationHeader = request.getHeader(AUTHORIZATION);
        if (authenticationHeader == null || !authenticationHeader.startsWith(PREFIX)) {
            return false;
        }
        return true;
    }
}
