package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EvidenceDTO {
    private Integer evidenceId;
    @NotNull
    @NotBlank(message = "Evidence type must not be blank!")
    private String type;
    private String modifier;
    private String fullLabelForFE;
    private String summary;
    private List<EvidenceLinkDTO> evidenceLinks;

    public EvidenceDTO(){};

    public EvidenceDTO(Integer evidenceId){
        this.evidenceId = evidenceId;
    };

    public EvidenceDTO(String type, String modifier){
        this.type = type;
        this.modifier = modifier;
    };

    public EvidenceDTO(String type, String modifier, String summary){
        this.type = type;
        this.modifier = modifier;
        this.summary = summary;
    };

    public EvidenceDTO(Integer evidenceId, String type, String modifier, String fullLabelForFE, String summary, List<EvidenceLinkDTO> evidenceLinks){
        this.evidenceId = evidenceId;
        this.type = type;
        this.modifier = modifier;
        this.fullLabelForFE = fullLabelForFE;
        if(summary != null){
            this.summary = summary;
        }
        if(evidenceLinks != null && evidenceLinks.size() > 0){
            this.evidenceLinks = evidenceLinks;
        }
    };
}
