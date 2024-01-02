package com.persida.pathogenicity_calculator.RequestAndResponseModels;

import lombok.Data;

@Data
public class EvidenceLinksRequest {
    private Integer interpretationId;
    private String evidenceTag;
}
