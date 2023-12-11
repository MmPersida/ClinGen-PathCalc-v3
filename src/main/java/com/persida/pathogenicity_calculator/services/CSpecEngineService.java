package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.CSpecEngineDTO;
import com.persida.pathogenicity_calculator.dto.CSpecEngineRuleSetRequest;
import com.persida.pathogenicity_calculator.dto.AssertionsDTO;

import java.util.ArrayList;

public interface CSpecEngineService {
    ArrayList<CSpecEngineDTO> getCSpecEnginesInfoByCall();
    CSpecEngineDTO getCSpecEngineInfo(String cspecengineId);
    AssertionsDTO getCSpecRuleSet(CSpecEngineRuleSetRequest cSpecEngineIDRequest);
    ArrayList<CSpecEngineDTO> getCSpecEnginesInfo();
    String callScpecEngine(String evidenceListStr);
}
