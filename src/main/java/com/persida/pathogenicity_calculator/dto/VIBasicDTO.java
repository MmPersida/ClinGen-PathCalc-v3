package com.persida.pathogenicity_calculator.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class VIBasicDTO {
    private String caid;
    private String condition;
    private String inheritance;
    private String finalCall;

    public VIBasicDTO(String caid, String condition, String inheritance, String finalCall){
        this.caid = caid;
        this.condition = condition;
        this.inheritance = inheritance;
        this.finalCall = finalCall;
    }
}
