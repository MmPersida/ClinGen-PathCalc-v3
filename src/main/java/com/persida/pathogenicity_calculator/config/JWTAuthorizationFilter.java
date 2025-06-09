package com.persida.pathogenicity_calculator.config;

import com.persida.pathogenicity_calculator.model.JWTHeaderAndPayloadData;
import com.persida.pathogenicity_calculator.repository.CustomUserDetails;
import com.persida.pathogenicity_calculator.repository.entity.User;
import com.persida.pathogenicity_calculator.services.JWT.JWTservice;
import com.persida.pathogenicity_calculator.services.userServices.UserService;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import io.jsonwebtoken.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter  {

    private Logger logger = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

    private final String REQUEST_COOKIE_TOKEN_KEY = "_redmine_session_genboree_staging="; //_redmine_session_genboree_
    private final String JWT_KEY = "gbAuthJwt";

    @Autowired
    private JWTservice jwtUtils;
    @Autowired
    private UserService userService;

    public JWTAuthorizationFilter(ApplicationContext ctx) {
        this.jwtUtils = ctx.getBean(JWTservice.class);
        this.userService = ctx.getBean(UserService.class);
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException,
                ServletException {
        try {
            if (checkCookieHeaderExists(request, response)) {
                JWTHeaderAndPayloadData tokenPayload = validateTokenAndExtractDataFromIt(request);
                if (tokenPayload != null) {
                    setUpSpringAuthentication(request, response, tokenPayload);
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

    /*
    The request wil send a cookie property in the header that contains a bunch of key=value pairs
    that are separated by ";". The key that we are looking for is the one in REQUEST_COOKIE_TOKEN_KEY.
    Once we extract the value this is in fact an encoded string. Once decoded with Base64 (UTF8), what we get is
    actually a Rubi Object that has no straight forward way of being parsed in Java.
    Inside this object the actual JWT is under the param "gbAuthJwt".
    This is shit, and can only be solved as such.
    * */
    private JWTHeaderAndPayloadData validateTokenAndExtractDataFromIt(HttpServletRequest request) {
        String cookieStr = request.getHeader( HttpHeaders.COOKIE);

        String redmineCookieObj = null;
        String[] cookieContentArray = cookieStr.split(";");
        for(String content : cookieContentArray){
            content = content.trim();
            if(content.startsWith(REQUEST_COOKIE_TOKEN_KEY)){
                redmineCookieObj = content.substring(REQUEST_COOKIE_TOKEN_KEY.length(),content.length());
                break;
            }
        }

        if(redmineCookieObj != null){
            try{
                String urlDecodedCookieObj = URLDecoder.decode(redmineCookieObj, StandardCharsets.UTF_8.toString());
                String str = new String(Base64.decodeBase64(urlDecodedCookieObj));

                //this is the best I can do right now,
                //if this works I'm not waisting a single second more of my life on this
                int i = str.indexOf(JWT_KEY);
                String jwtToken = str.substring((i+JWT_KEY.length()+4), str.length());

                return jwtUtils.decodeAndValidateToken(jwtToken);
            }catch(Exception e ){
                logger.error("Unable to decode the redmineCookieObj!");
            }
        }
        return null;
    }

    private void setUpSpringAuthentication(HttpServletRequest request, HttpServletResponse response,
                                           JWTHeaderAndPayloadData tokenPayload) {
        @SuppressWarnings("unchecked")
        List<String> authorities = new ArrayList<String>();
        authorities.add(Constants.USER_ROLLE_USER);

        CustomUserDetails cus = null;
        try {
            User user = userService.getUserByUsername(tokenPayload.getUsername());
            if (user == null) {
                logger.error("Could not find user with name: "+tokenPayload.getUsername());
                throw new UsernameNotFoundException("Could not find user by username: "+tokenPayload.getUsername());
            }
           cus = new CustomUserDetails(user);
        }catch(Exception e){}

        UsernamePasswordAuthenticationToken auth = null;
        if(cus != null){
            auth = new UsernamePasswordAuthenticationToken(cus, null,
                    authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        }else{
            auth = new UsernamePasswordAuthenticationToken(tokenPayload.getUsername(), null,
                    authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        }
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);

        //Save the SecurityContext to the session (if using Spring Security's session management)
        SecurityContextRepository repository = new HttpSessionSecurityContextRepository();
        repository.saveContext(sc, request, response);
    }

    private boolean checkCookieHeaderExists(HttpServletRequest request, HttpServletResponse res) {
        String cookieHeader = request.getHeader(HttpHeaders.COOKIE);
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return false;
        }
        return true;
    }
}
