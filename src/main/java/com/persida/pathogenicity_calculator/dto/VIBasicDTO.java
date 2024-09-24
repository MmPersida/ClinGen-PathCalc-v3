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
    private String conditionId;
    private String inheritance;
    private EngineRelatedGeneDTO relatedGene;
    private FinalCallDTO calculatedFinalCall;
    private FinalCallDTO determinedFinalCall;
    private String cspecengineId;
    private Date createOn;
    private Date modifiedOn;

    public VIBasicDTO(String caid, Integer interpretationId, String conditionId, String condition, String inheritance,
                      EngineRelatedGeneDTO relatedGene, FinalCallDTO calculatedFinalCall, FinalCallDTO determinedFinalCall,
                      String cspecengineId, Date createOn, Date modifiedOn){
        this.caid = caid;
        this.interpretationId = interpretationId;
        this.conditionId = conditionId;
        this.condition = condition;
        this.inheritance = inheritance;
        this.relatedGene = relatedGene;
        this.calculatedFinalCall = calculatedFinalCall;
        this.determinedFinalCall = determinedFinalCall;
        this.cspecengineId = cspecengineId;
        this.createOn = createOn;
        this.modifiedOn = modifiedOn;
    }
}
