package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.CSpecEngineDTO;

import java.util.ArrayList;

public interface CSpecEngineService {
    ArrayList<CSpecEngineDTO> getCSpecEnginesInfoByCall();
}
