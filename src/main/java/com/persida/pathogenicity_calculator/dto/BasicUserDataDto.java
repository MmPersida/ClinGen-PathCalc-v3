package com.persida.pathogenicity_calculator.dto;

public class BasicUserDataDto {
    private int id;
    private String username;
    private String email;
    private boolean enabled;

    public BasicUserDataDto(int id, String username, String email, boolean enabled){
        this.id = id;
        this.username = username;
        this.email = email;
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
