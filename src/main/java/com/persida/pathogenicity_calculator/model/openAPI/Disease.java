package com.persida.pathogenicity_calculator.model.openAPI;

import lombok.Data;

@Data
public class Disease {
    private String diseaseId;
    private String term;

    public Disease(String diseaseId, String term){
            this.diseaseId = diseaseId;
            this.term = term;
    }
}
