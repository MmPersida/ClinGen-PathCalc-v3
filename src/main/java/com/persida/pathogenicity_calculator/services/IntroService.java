package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.DetermineCAIDRequest;
import com.persida.pathogenicity_calculator.dto.NumOfCAIDsDTO;
import com.persida.pathogenicity_calculator.dto.VariantInterpretationDTO;

import java.util.ArrayList;
import java.util.List;

public interface IntroService {
    List<String> getInterpretedVariantCAIDsLike(String partialCAID);
    List<VariantInterpretationDTO> getRecentlyInterpretedVariants();
    String determineCIAD(DetermineCAIDRequest determineCIADRequest);
    ArrayList<NumOfCAIDsDTO[]> getSummaryOfClassifiedVariants();

}
