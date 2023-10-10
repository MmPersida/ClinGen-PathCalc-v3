package com.persida.pathogenicity_calculator.config.appPropertiesHandler;

public interface PCWebConfigurationManager { ;
    String getPropertyByNameAsString(String propertyName);
    Integer getPropertyByNameAsInt(String propertyName);
    String[] getPropertyByNameAsArray(String propertyName);
}
