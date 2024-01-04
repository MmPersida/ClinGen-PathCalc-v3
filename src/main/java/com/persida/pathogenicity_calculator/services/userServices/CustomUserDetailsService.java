package com.persida.pathogenicity_calculator.services.userServices;

import com.persida.pathogenicity_calculator.repository.entity.User;

public interface CustomUserDetailsService {
    //this interface exists only to provide this method during token processing
    boolean createUser(User user);
}
