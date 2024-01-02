package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

@Data
public class EvidenceLinkDTO {
    private Integer linkId;
    private String link;
    private String linkCode;
    private String comment;

    public EvidenceLinkDTO(){}

    public EvidenceLinkDTO(Integer linkId, String link, String linkCode, String comment){
        this.linkId = linkId;
        this.link = link;
        this.linkCode = linkCode;
        this.comment = comment;

    }
}
