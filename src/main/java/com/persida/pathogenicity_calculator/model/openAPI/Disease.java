package com.persida.pathogenicity_calculator.model.openAPI;

import lombok.Data;

@Data
public class Disease {
    private String id;
    private String term;

    public Disease(String id, String term){
            this.id = id;
            this.term = term;
    }
}
