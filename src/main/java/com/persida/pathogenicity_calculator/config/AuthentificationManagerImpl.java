package com.persida.pathogenicity_calculator.config;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthentificationManagerImpl implements AuthentificationManager {

    @Override
    public Authentication getAuthentication() {
            return getAuthenticationObj();
    }

    @Override
    public String getCurrentUserName() {
        Authentication a = getAuthenticationObj();
        if (!(a instanceof AnonymousAuthenticationToken) && a.isAuthenticated()) {
            return a.getName();
        }else{
            return null;
        }
    }

    private Authentication getAuthenticationObj(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
