package com.persida.pathogenicity_calculator.model.openAPI.requestModels;

import com.persida.pathogenicity_calculator.dto.EvidenceDTO;
import com.sun.istack.NotNull;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class AddEvidencesRequest {
    @NotNull
    @Pattern(regexp = "^[0-9]+$")
    private Integer classificationId;
    @NotNull
    List<EvidenceDTO> evidenceList;
}
