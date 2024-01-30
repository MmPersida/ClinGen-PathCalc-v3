package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.DetermineCAIDRequest;
import com.persida.pathogenicity_calculator.dto.VariantInterpretationDTO;

import java.util.List;

public interface IntroService {
    public List<String> getInterpretedVariantCAIDsLike(String partialCAID);
    public  List<VariantInterpretationDTO> getRecentlyInterpretedVariants();
    public String determineCIAD(DetermineCAIDRequest determineCIADRequest);
}
