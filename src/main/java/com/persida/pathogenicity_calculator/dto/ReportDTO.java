package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ReportDTO {
    private VariantInterpretationDTO viDTO;
    private AssertionsDTO assertionDTO;
    private Map<String, String> evidenceCommentsMap;
    private String message;

    public ReportDTO(){}
    public ReportDTO(VariantInterpretationDTO viDTO){
        this.viDTO = viDTO;
    }
}
