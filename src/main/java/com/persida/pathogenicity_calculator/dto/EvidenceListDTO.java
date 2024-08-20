package com.persida.pathogenicity_calculator.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class EvidenceListDTO {
    @NotNull
    @Pattern(regexp = "^[0-9]+$")
    private Integer interpretationId;
    @NotNull(message = "FinalCall ID must not be null.")
    private FinalCallDTO finalCall;
    @NotNull
    private List<EvidenceDTO> evidenceList;
}
