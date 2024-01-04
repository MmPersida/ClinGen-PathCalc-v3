package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.config.AuthentificationManager;
import com.persida.pathogenicity_calculator.dto.BasicUserDataDto;
import com.persida.pathogenicity_calculator.repository.CustomUserDetails;
import com.persida.pathogenicity_calculator.repository.UserRepository;
import com.persida.pathogenicity_calculator.repository.entity.User;
import com.persida.pathogenicity_calculator.repository.jpa.BasicUserDataJPA;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService{

    private static Logger logger = Logger.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthentificationManager authentificationManager;

    @Override
    public String getCurrentUserFullName(){
        return authentificationManager.getCurrentUserFullName();
    }

    @Override
    public Integer getCurrentUserId(){
        return authentificationManager.getCurrentUserId();
    }

    @Override
    public User getUserByUsername(String username){
        return userRepository.getUserByUsername(username);
    }

    @Override
    public User getUserById(Integer id){
        return userRepository.getUserById(id);
    }

    @Override
    public BasicUserDataDto getBacisUserDataByUsername(String username){
        BasicUserDataJPA obj = userRepository.getBacisUserDataByUsername(username);
        return new BasicUserDataDto(obj.getId(), obj.getUsername(), obj.getEmail(), obj.getEnabled());
    }
}
