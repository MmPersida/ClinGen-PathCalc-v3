package com.persida.pathogenicity_calculator.RequestAndResponseModels;

import com.persida.pathogenicity_calculator.dto.FinalCallDTO;
import com.sun.istack.NotNull;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class VariantInterpretationSaveResponse {
    @NotNull
    @Pattern(regexp = "^[0-9]+$")
    private Integer interpretationId;
    @Pattern(regexp = "^GN[0-9]+$", message = "cspecengineId must start with GN and fit the format.")
    private String cspecengineId;
    @Pattern(regexp = "^[0-9]+$", message = "rulesetId must fit the format.")
    private Integer rulesetId;
    private FinalCallDTO calculatedFinalCall;
    private FinalCallDTO determinedFinalCall;
    private String message;

    public VariantInterpretationSaveResponse(Integer newInterpretationId){
        this.interpretationId = newInterpretationId;
    }

    public VariantInterpretationSaveResponse(Integer newInterpretationId,  String cspecengineId,
                                             Integer rulesetId, FinalCallDTO calculatedFinalCall,
                                             FinalCallDTO determinedFinalCall){
        this.interpretationId = newInterpretationId;
        this.cspecengineId = cspecengineId;
        this.rulesetId = rulesetId;
        this.calculatedFinalCall = calculatedFinalCall;
        this.determinedFinalCall = determinedFinalCall;
    }

    public VariantInterpretationSaveResponse(Integer newInterpretationId, String message){
        this.interpretationId = newInterpretationId;
        this.message = message;
    }
}
