package com.persida.pathogenicity_calculator.controllers;

import com.persida.pathogenicity_calculator.services.userServices.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainContent {
    private static Logger logger = Logger.getLogger(MainContent.class);

    @Autowired
    private UserService userService;


    @RequestMapping(value = "views/pc_main", method= RequestMethod.GET)
    public String getPCmainView(Model model) {
        model.addAttribute("currentUserName",userService.getCurrentUserFullName());
        return "views/pc_main";
    }

    @RequestMapping(value = "views/calculator", method= RequestMethod.GET)
    public String getCalculatorView(Model model) {
        model.addAttribute("currentUserName", userService.getCurrentUserFullName());
        return "views/calculator";
    }
}
