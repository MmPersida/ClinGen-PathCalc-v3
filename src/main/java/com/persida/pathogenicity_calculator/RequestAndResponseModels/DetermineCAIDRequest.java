package com.persida.pathogenicity_calculator.RequestAndResponseModels;

import lombok.Data;

@Data
public class DetermineCAIDRequest {
    private String identifierValue;
    private String identifierType;
}
