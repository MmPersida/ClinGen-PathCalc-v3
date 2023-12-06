package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.CSpecEngineDTO;
import com.persida.pathogenicity_calculator.dto.CSpecEngineIDRequest;
import com.persida.pathogenicity_calculator.dto.IheritanceDTO;

import java.util.ArrayList;
import java.util.List;

public interface CalculatorService {
    String getAlleleAndGeneData(String variantCID);
    List<IheritanceDTO> getInheritanceModes();
    String getCSpecRuleSet(CSpecEngineIDRequest cSpecEngineIDRequest);
    ArrayList<CSpecEngineDTO> getCSpecEnginesInfo();
    String callScpecEngine(String evidenceListStr);
}
