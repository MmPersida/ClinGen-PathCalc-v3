package com.persida.pathogenicity_calculator.config;

import com.persida.pathogenicity_calculator.utils.HTTPSConnector;
import com.persida.pathogenicity_calculator.utils.StackTracePrinter;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

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
            String cookieHeader = request.getHeader(HttpHeaders.COOKIE);
            if(cookieHeader != null){
                /*
                try {
                    HashMap<String, String> httpProperties = new HashMap<String, String>();
                    httpProperties.put(HttpHeaders.COOKIE, cookieHeader);

                    HTTPSConnector https = new HTTPSConnector();
                    //will be implemented with GET in the future
                    String apiResponse = https.sendHttpsRequest(redmineLogoutPage, Constants.HTTP_POST, null, httpProperties);
                    logger.info("Logout response: " + apiResponse);
                }catch(Exception e){
                    logger.error(StackTracePrinter.printStackTrace(e));
                }*/
            }
            response.sendRedirect(this.pcLandingEntryPage);
        }else{
            response.sendRedirect(this.profileURL);
        }
    }
}
