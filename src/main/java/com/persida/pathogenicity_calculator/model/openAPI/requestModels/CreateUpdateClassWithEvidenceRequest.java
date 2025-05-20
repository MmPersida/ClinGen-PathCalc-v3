package com.persida.pathogenicity_calculator.model.openAPI.requestModels;

import com.persida.pathogenicity_calculator.dto.IheritanceDTO;
import com.persida.pathogenicity_calculator.model.openAPI.Disease;
import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class CreateUpdateClassWithEvidenceRequest {
    @NotNull
    @Pattern(regexp = "^CA[0-9]+$")
    private String caid;

    @Pattern(regexp = "^[0-9]+$")
    private Integer classificationId; //will only be used when updating a classification

    @NotNull
    private String gene;
    @NotNull
    private Disease disease;
    @NotNull
    private String cspecId;
    @NotNull
    private IheritanceDTO modeOfInheritance;
    private List<EvideneTagRequest> evidenceTags;
}


