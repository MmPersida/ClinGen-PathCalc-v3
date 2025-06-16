package com.persida.pathogenicity_calculator.dto;

import java.util.List;

public class CurrentUserPrincipleDTO {
    private String username;
    private List<String> authorities;

    public CurrentUserPrincipleDTO() {
    }

    public CurrentUserPrincipleDTO(String username, List<String> authorities) {
        this.username = username;
        this.authorities = authorities;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getAuthorities() {
        return this.authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}
