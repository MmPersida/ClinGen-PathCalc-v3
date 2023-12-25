package com.persida.pathogenicity_calculator.RequestAndResponseModels;

import lombok.Data;

@Data
public class EvidenceSummaryRequest {
    private Integer interpretationId ;
    private String[] evidenceTags;
}
