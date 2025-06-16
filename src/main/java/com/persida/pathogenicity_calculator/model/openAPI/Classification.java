package com.persida.pathogenicity_calculator.model.openAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.ClassificationEntContent;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Classification {
    private ClassificationEntContent entContent;
    private int classId;
    private String caid;
    private String created;
    private String modified;
    private String modifier;
    private String rev;

    public Classification(int viId){
        this.classId = viId;
    }

    public Classification(ClassificationEntContent entContent, int classId, String caid,
                          String created, String modified, String modifier){
        this.entContent = entContent;
        this.classId = classId;
        this.caid = caid;
        this.created = created;
        this.modified = modified;
        this.modifier = modifier;
        this.rev = "3.1";
    }
}