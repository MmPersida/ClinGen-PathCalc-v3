package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

@Data
public class ReportDTO {
    private VariantInterpretationDTO viDTO;
    private AssertionsDTO assertionDTO;
    private String message;

    public ReportDTO(){}
    public ReportDTO(VariantInterpretationDTO viDTO){
        this.viDTO = viDTO;
    }
}
