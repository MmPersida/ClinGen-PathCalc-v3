package com.persida.pathogenicity_calculator.model.openAPI;

import com.persida.pathogenicity_calculator.model.openAPI.requestModels.ClassificationEntContent;
import lombok.Data;

@Data
public class Classification {
    private ClassificationEntContent entContent;
    private int viId;
    private String created;
    private String modified;
    private String modifier;
    private String rev;

    public Classification(ClassificationEntContent entContent, int viId,
                          String created, String modified, String modifier){
        this.entContent = entContent;
        this.viId = viId;
        this.created = created;
        this.modified = modified;
        this.modifier = modifier;
        this.rev = "1";
    }
}