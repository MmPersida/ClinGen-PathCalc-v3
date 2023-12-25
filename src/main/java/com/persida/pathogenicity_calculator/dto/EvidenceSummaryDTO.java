package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

@Data
public class EvidenceSummaryDTO {
    private Integer summaryId;
    private String summary;

    public EvidenceSummaryDTO(Integer summaryId, String summary){
        this.summaryId = summaryId;
        this.summary = summary;
    }
}
