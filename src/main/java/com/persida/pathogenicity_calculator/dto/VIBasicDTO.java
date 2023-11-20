package com.persida.pathogenicity_calculator.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class VIBasicDTO {
    private String caid;
    private Integer interpretationId;
    private String condition;
    private String inheritance;
    private String finalCall;
    private Date createOn;
    private Date modifiedOn;

    public VIBasicDTO(String caid, Integer interpretationId, String condition, String inheritance, String finalCall, Date createOn, Date modifiedOn){
        this.caid = caid;
        this.interpretationId = interpretationId;
        this.condition = condition;
        this.inheritance = inheritance;
        this.finalCall = finalCall;
        this.createOn = createOn;
        this.modifiedOn = modifiedOn;
    }
}
