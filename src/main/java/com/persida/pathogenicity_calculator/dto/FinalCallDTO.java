package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

@Data
public class FinalCallDTO {
    private Integer id;
    private String term;

    public FinalCallDTO(Integer id, String term){
        this.id = id;
        this.term = term;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
