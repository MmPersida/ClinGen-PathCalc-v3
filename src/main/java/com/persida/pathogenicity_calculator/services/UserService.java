package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.BasicUserDataDto;
import com.persida.pathogenicity_calculator.repository.entity.User;

public interface UserService {
    public User getUserByUsername(String username);
    public User getUserById(Integer id);
    public BasicUserDataDto getBacisUserDataByUsername(String username);
}
