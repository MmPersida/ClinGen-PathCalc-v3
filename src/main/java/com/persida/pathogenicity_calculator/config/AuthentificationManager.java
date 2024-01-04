package com.persida.pathogenicity_calculator.config;

import com.persida.pathogenicity_calculator.repository.CustomUserDetails;
import org.springframework.security.core.Authentication;

public interface AuthentificationManager {
    Authentication getAuthentication();
    String getCurrentUserName();
    String getCurrentUserFullName();
    Integer getCurrentUserId();
}
