package com.persida.pathogenicity_calculator.controllers;

import com.persida.pathogenicity_calculator.config.AuthentificationManager;
import com.persida.pathogenicity_calculator.repository.CustomUserDetails;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainContent {
    private static Logger logger = Logger.getLogger(MainContent.class);

    @Autowired
    private AuthentificationManager authentificationManager;

    @RequestMapping(value = "views/pc_main", method= RequestMethod.GET)
    public String getPCmainView(Model model) {
        model.addAttribute("currentUserName", getCurrentUserFullName());
        return "views/pc_main";
    }

    @RequestMapping(value = "views/calculator", method= RequestMethod.GET)
    public String getCalculatorView(Model model) {
        model.addAttribute("currentUserName", getCurrentUserFullName());
        return "views/calculator";
    }

    public String getCurrentUserFullName(){
        Authentication authenticate  = authentificationManager.getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authenticate.getPrincipal();
        if(customUserDetails == null){
            logger.error("Unable to get current username!");
            return null;
        }
        return customUserDetails.getUserFullName();
    }
}
