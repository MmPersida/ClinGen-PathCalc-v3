package com.persida.pathogenicity_calculator.dto;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.Map;

@Data
public class CSpecEngineRuleSetRequest {
    @Pattern(regexp = "^[0-9]+$", message = "cSpecEngine LdhId must fit the format.")
    private Integer rulesetId;
    @NotNull
    @Pattern(regexp = "^GN[0-9]+$", message = "cSpecEngine EntId must start with GN and fit the format.")
    private String cspecengineId;
    @NotNull
    private Map<String,Integer> evidenceMap;
}
