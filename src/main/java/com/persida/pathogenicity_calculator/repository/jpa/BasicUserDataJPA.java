package com.persida.pathogenicity_calculator.repository.jpa;

public interface BasicUserDataJPA {
    public int getId();
    public String  getUsername();
    public String  getEmail();
    public boolean  getEnabled();
}
