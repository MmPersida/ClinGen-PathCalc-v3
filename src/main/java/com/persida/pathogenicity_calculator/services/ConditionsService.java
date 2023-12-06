package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.ConditionsTermAndIdDTO;

import java.util.ArrayList;
import java.util.List;

public interface ConditionsService {
    public List<ConditionsTermAndIdDTO> getConditionsLike(String partialCAID);
    public ArrayList<ConditionsTermAndIdDTO> getConditionsInfoByCall();
}
