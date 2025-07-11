package com.persida.pathogenicity_calculator.config;

import com.persida.pathogenicity_calculator.model.JWTHeaderAndPayloadData;
import com.persida.pathogenicity_calculator.repository.CustomUserDetails;
import com.persida.pathogenicity_calculator.repository.entity.User;
import com.persida.pathogenicity_calculator.services.JWT.JWTservice;
import com.persida.pathogenicity_calculator.services.userServices.UserService;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import io.jsonwebtoken.*;
import org.apache.log4j.Logger;
import org.jruby.embed.ScriptingContainer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
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
    private Logger logger = Logger.getLogger(JWTAuthorizationFilter.class);

    private String cookieTokenObjKey;
    private final String JWT_KEY = "gbAuthJwt";

    @Autowired
    private JWTservice jwtUtils;
    @Autowired
    private UserService userService;
    private JSONParser jsonParser;

    public JWTAuthorizationFilter(ApplicationContext ctx) {
        this.jwtUtils = ctx.getBean(JWTservice.class);
        this.userService = ctx.getBean(UserService.class);

        Environment environment = ctx.getEnvironment();
        cookieTokenObjKey = environment.getProperty("cookieTokenObjKey");
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException,
                ServletException {
        try {
            if (checkCookieHeaderExists(request, response)) {
                JWTHeaderAndPayloadData tokenPayload = validateTokenAndExtractDataFromIt(request);
                if (tokenPayload != null) {
                    setUpSpringAuthentication(request, response, tokenPayload);
                }
            }
            chain.doFilter(request, response);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        }
    }

    private JWTHeaderAndPayloadData validateTokenAndExtractDataFromIt(HttpServletRequest request) {
        String cookieStr = request.getHeader( HttpHeaders.COOKIE);
        String jwtValue = extractAndUnpackRubyObjFromCookie(cookieStr);
        if(jwtValue != null){
            return jwtUtils.decodeAndValidateToken(jwtValue);
        }
        return null;
    }

    private void setUpSpringAuthentication(HttpServletRequest request, HttpServletResponse response,
                                           JWTHeaderAndPayloadData tokenPayload) {
        @SuppressWarnings("unchecked")
        List<String> authorities = new ArrayList<String>();
        authorities.add(Constants.USER_ROLLE_USER);

        CustomUserDetails cud = jwtUtils.createUserIfFirstTimeLoginAndReturnCUD(tokenPayload, authorities.get(0));

        UsernamePasswordAuthenticationToken auth = null;
        if(cud != null){
            auth = new UsernamePasswordAuthenticationToken(cud, null,
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

    private String extractAndUnpackRubyObjFromCookie(String cookieStr){
        String redmineCookieObj = null;
        String[] cookieContentArray = cookieStr.split(";");
        for(String content : cookieContentArray){
            content = content.trim();
            if(content.startsWith(cookieTokenObjKey)){
                redmineCookieObj = content.substring(cookieTokenObjKey.length(),content.length());
                break;
            }
        }

        if(redmineCookieObj != null) {
            try {
                String urlDecodedCookieObj = URLDecoder.decode(redmineCookieObj, StandardCharsets.UTF_8.toString());

                String scriptToRun = " require 'json'; require 'base64'; Marshal.load(Base64.decode64(\"" + urlDecodedCookieObj + "\")).to_json; ";
                ScriptingContainer container = new ScriptingContainer();
                Object obj = container.runScriptlet(scriptToRun);
                if (obj == null) {
                    return null;
                }

                if (jsonParser == null) {
                    jsonParser = new JSONParser();
                }
                JSONObject jsonObj = (JSONObject) jsonParser.parse(obj.toString());
                return String.valueOf(jsonObj.get(JWT_KEY));
            } catch (Exception e) {
                logger.error("Unable to decode the redmineCookieObj!");
            }
        }
        return null;
    }

    private boolean checkCookieHeaderExists(HttpServletRequest request, HttpServletResponse res) {
        String cookieHeader = request.getHeader(HttpHeaders.COOKIE);
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return false;
        }
        return true;
    }
}
