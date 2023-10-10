package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.VariantCAIdDTO;
import com.persida.pathogenicity_calculator.dto.VariantInterpretationDTO;

import java.util.List;

public interface IntroService {
    public List<VariantCAIdDTO> getInterpretedVariantCAIDsLike(String partialCAID);
    public  List<VariantInterpretationDTO> getRecentlyInterpretedVariants();
}
