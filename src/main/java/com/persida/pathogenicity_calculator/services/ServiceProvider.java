package com.persida.pathogenicity_calculator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceProvider {

    @Autowired
    private UserService userService;

    public UserService getUserService() { return userService = new UserServiceImpl(); }
}
