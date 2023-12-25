package com.persida.pathogenicity_calculator.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class VariantInterpretationDTO {
    @Pattern(regexp = "^[0-9]+$")
    private Integer interpretationId;

    @Pattern(regexp = "^CA[0-9]+$", message = "CAID must start with CA and fit the format.")
    private String caid;

    @NotBlank(message = "Condition ID must not be blank.")
    private String conditionId;

    @NotBlank(message = "Condition must not be blank.")
    private String condition;

    @NotNull(message = "Inheritance ID must not be null.")
    private Integer inheritanceId;

    @NotNull(message = "Inheritance must not be null.")
    private String inheritance;

    @NotNull(message = "EvidenceSet must not be null.")
    private  List<EvidenceDTO> evidenceList;

    @NotNull(message = "FinalCall ID must not be null.")
    private Integer finalCallId;

    @NotNull(message = "FinalCall must not be null.")
    private String finalCall;

    private CSpecEngineDTO cspecEngineDTO;

    private String viDescription;

    private String message;
}
