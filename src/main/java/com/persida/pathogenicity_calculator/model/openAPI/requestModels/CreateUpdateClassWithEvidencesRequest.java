package com.persida.pathogenicity_calculator.model.openAPI.requestModels;

import com.persida.pathogenicity_calculator.dto.EvidenceDTO;
import lombok.Data;

import java.util.List;

@Data
public class CreateUpdateClassWithEvidencesRequest extends CreateUpdateClassRequest{
    private List<EvidenceDTO> evidenceList;
}
