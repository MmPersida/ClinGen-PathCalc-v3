package com.persida.pathogenicity_calculator.repository;

import com.persida.pathogenicity_calculator.repository.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        return Arrays.asList(authority);
    }

    public int getUserId() { return user.getId(); }

    @Override
    public String getUsername() { return user.getUsername(); }

    @Override
    public String getPassword() { return user.getPassword(); }

    public String getUserFullName() { return user.getFirstName()+" "+user.getLastName(); }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if(user.getEnabled()){
            return true;
        }
        return false;
    };

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        if(user.getEnabled()){
            return true;
        }
        return false;
    }
}
