package com.persida.pathogenicity_calculator.model.openAPI.requestModels;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class CreateClassWithEvidenceRequest {
    @NotNull
    @Pattern(regexp = "^CA[0-9]+$")
    private String caid;
    @NotNull
    private String gene;
    @NotNull
    private String disease;
    @NotNull
    private String modeOfInheritance;
    private List<EvideneTagRequest> evidenceTags;

    @Data
    public class EvideneTagRequest{
        private String type;
        private String modifier;
        private String fullLabelForFE;
        private String summary;
        private List<EvidenceLinkRequest> evidenceLinks;
    }

    @Data
    public class EvidenceLinkRequest{
        private Integer linkId;
        private String link;
        private String linkCode;
        private String comment;

    }
}


