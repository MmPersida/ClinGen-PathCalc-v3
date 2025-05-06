package com.persida.pathogenicity_calculator.model.openAPI;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EvideneTagRequest {
    @NotNull
    private String type;
    @NotNull
    private String modifier;
    //private String fullLabelForFE; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private String summary;
    //private List<EvidenceLinkRequest> evidenceLinks;

    /*
    @Data
    public class EvidenceLinkRequest{
        //private Integer linkId; //???????????????????
        private String link;
        private LinkCode linkCode;
        private String comment;

    }

    public enum LinkCode{
        Supports,
        Unknown,
        Disputes
    }*/
}
