package com.persida.pathogenicity_calculator.config;

import org.springframework.security.core.Authentication;

public interface AuthentificationManager {
    Authentication getAuthentication();
    String getCurrentUserName();
}
