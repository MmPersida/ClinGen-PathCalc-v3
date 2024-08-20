package com.persida.pathogenicity_calculator.RequestAndResponseModels;

import com.persida.pathogenicity_calculator.dto.FinalCallDTO;
import com.persida.pathogenicity_calculator.repository.entity.FinalCall;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class VarInterpUpdateFCResponse {
    @NotNull
    @Pattern(regexp = "^[0-9]+$")
    private Integer interpretationId;
    private FinalCallDTO finalCall;
    private String message;

    public VarInterpUpdateFCResponse(Integer newInterpretationId, FinalCall fc){
        this.interpretationId = newInterpretationId;
        this.finalCall = new FinalCallDTO(fc.getId(), fc.getTerm());
    }

    public VarInterpUpdateFCResponse(Integer newInterpretationId, String message){
        this.interpretationId = newInterpretationId;
        this.message = message;
    }
}
