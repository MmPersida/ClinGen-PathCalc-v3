package com.persida.pathogenicity_calculator.model.openAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvidenceR {
    private Integer id;
    @NotNull
    private String type;
    private String modifier;
    private String fullLabel;
    private String summary;

    public EvidenceR(){}
    public EvidenceR(Integer id, String type, String modifier, String fullLabel, String summary){
        this.id = id;
        this.type = type;
        this.fullLabel = fullLabel;
        this.modifier= modifier;
        this.summary = summary;
    }
}
