package com.persida.pathogenicity_calculator.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class EvidenceDTO {
    @NotNull
    @NotBlank(message = "Condition ID must not be blank.")
    private String name;

    @NotNull
    @NotBlank(message = "Condition ID must not be blank.")
    @Pattern(regexp = "^[01PMSV]$", message = "Modifier must fit the format.")
    private Character modifier;

    public EvidenceDTO(){};

    public EvidenceDTO(String name, Character modifier){
        this.name = name;
        this.modifier = modifier;
    };
}
