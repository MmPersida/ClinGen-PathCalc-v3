package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class EvidenceLinksDTO {
    @NotNull
    @Pattern(regexp = "^[0-9]+$")
    private Integer interpretationId;
    @NotNull(message = "EvidenceTag must not be null.")
    private String evidenceTag;
    private String evidenceModifier;
    @NotNull
    private List<EvidenceLinkDTO> evidenceLinks;
}
