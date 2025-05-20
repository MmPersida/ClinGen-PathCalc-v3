package com.persida.pathogenicity_calculator.model.openAPI.requestModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.persida.pathogenicity_calculator.model.openAPI.EvidenceR;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassificationEntContent {
    private String specificationId;
    private String gene; // "SHOC2"
    private String condition; // "congenital heart defects, multiple types",
    private String inheritance; // "other",
    private String finalClass; //"pathogenic",
    private String expertClass; //"pathogenic"
    private List<EvidenceR> evidences;

    public  ClassificationEntContent(){}

    public ClassificationEntContent(String specificationId, String gene, String condition, String inheritance,
                                    String finalClass, String expertClass){
        this.specificationId = specificationId;
        this.gene = gene;
        this.condition = condition;
        this.inheritance = inheritance;
        this.finalClass = finalClass;
        this.expertClass = expertClass;
    }

    public ClassificationEntContent(String specificationId, String gene, String condition, String inheritance,
                                    String finalClass, String expertClass, List<EvidenceR> evidences){
        this.specificationId = specificationId;
        this.gene = gene;
        this.condition = condition;
        this.inheritance = inheritance;
        this.finalClass = finalClass;
        this.expertClass = expertClass;
        this.evidences = evidences;
    }
}


