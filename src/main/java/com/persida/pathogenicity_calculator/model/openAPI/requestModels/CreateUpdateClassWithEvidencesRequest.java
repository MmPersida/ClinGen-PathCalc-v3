package com.persida.pathogenicity_calculator.model.openAPI.requestModels;

import com.persida.pathogenicity_calculator.dto.EvidenceDTO;
import com.persida.pathogenicity_calculator.dto.IheritanceDTO;
import com.persida.pathogenicity_calculator.model.openAPI.Disease;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateUpdateClassWithEvidencesRequest extends CreateUpdateClassRequest{
    private List<EvidenceDTO> evidenceList;

    @Builder
    public CreateUpdateClassWithEvidencesRequest(String caid, Integer classificationId, String gene,
                                                 Disease disease, String cspecId, IheritanceDTO modeOfInheritance,
                                                 List<EvidenceDTO> evidenceList){
        super(caid, classificationId, gene, disease, cspecId, modeOfInheritance);
        this.evidenceList = evidenceList;
    }

}
