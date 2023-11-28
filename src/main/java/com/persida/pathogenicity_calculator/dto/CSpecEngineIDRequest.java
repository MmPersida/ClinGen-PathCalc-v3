package com.persida.pathogenicity_calculator.dto;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class CSpecEngineIDRequest {
    @NotNull
    @Pattern(regexp = "^[0-9]+$", message = "cSpecEngine LdhId must fit the format.")
    private Integer cspecEngineLdhId;
    @Pattern(regexp = "^GN[0-9]+$", message = "cSpecEngine EntId must start with GN and fit the format.")
    private String cspecEngineEntId;
}
