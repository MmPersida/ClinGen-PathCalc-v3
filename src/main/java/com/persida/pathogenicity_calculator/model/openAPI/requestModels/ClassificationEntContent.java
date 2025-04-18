package com.persida.pathogenicity_calculator.model.openAPI.requestModels;

import lombok.Data;

@Data
public class ClassificationEntContent {
    private String gene; // "SHOC2"
    private String condition; // "congenital heart defects, multiple types",
    private String inheritance; // "other",
    private String finalClass; //"pathogenic",
    private String expertClass; //"pathogenic"

    public ClassificationEntContent(String gene, String condition, String inheritance,
                                    String finalClass, String expertClass){
        this.gene = gene;
        this.condition = condition;
        this.inheritance = inheritance;
        this.finalClass = finalClass;
        this.expertClass = expertClass;
    }
}


