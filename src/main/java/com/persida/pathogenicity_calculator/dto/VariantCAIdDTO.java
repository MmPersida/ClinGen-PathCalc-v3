package com.persida.pathogenicity_calculator.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class VariantCAIdDTO {
    private String caid;
    private Date modifiedOn;

    public VariantCAIdDTO(String caid, Date modifiedOn){
        this.caid = caid;
        this.modifiedOn = modifiedOn;
    }
}
