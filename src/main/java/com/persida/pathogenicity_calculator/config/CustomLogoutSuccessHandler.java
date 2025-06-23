package com.persida.pathogenicity_calculator.config;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private Logger logger = Logger.getLogger(CustomLogoutSuccessHandler.class);

    @Value("${profileURL}")
    private String profileURL;

    @Value("${pcLandingEntryPage}")
    private String pcLandingEntryPage;

    @Value("${redmineLogoutPage}")
    private String redmineLogoutPage;

    private String profile;
    @Autowired
    private Environment environment;

    @PostConstruct
    public void prepareProfileData() {
        this.profile = (this.environment.getActiveProfiles())[0];
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if(!profile.equals("local")){
            String backURLParamEncoded = URLEncoder.encode(this.pcLandingEntryPage, StandardCharsets.UTF_8.toString());
            String logoutUrlWithBackURL = this.redmineLogoutPage+"?back_url="+backURLParamEncoded;
            response.sendRedirect(logoutUrlWithBackURL);
        }else{
            response.sendRedirect(this.profileURL);
        }
    }
}
