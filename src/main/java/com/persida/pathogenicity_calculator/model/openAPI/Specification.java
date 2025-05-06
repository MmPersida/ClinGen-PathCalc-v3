package com.persida.pathogenicity_calculator.model.openAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Specification {
    private String specificationId;
    private String specificationSummary;
    private String organizationName;
    private String organizationLink;
    private Integer ruleSetId;
    private String ruleSetURL;

    public Specification(String specificationId, String specificationSummary, String organizationName,
                         String organizationLink, Integer ruleSetId, String ruleSetURL){
        this.specificationId = specificationId;
        this.specificationSummary = specificationSummary;
        this.organizationName = organizationName;
        this.organizationLink = organizationLink;
        this.ruleSetId = ruleSetId;
        this.ruleSetURL = ruleSetURL;
    }
}
