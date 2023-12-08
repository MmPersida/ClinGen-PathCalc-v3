package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.CSpecEngineDTO;
import com.persida.pathogenicity_calculator.dto.CSpecEngineIDRequest;

import java.util.ArrayList;

public interface CSpecEngineService {
    CSpecEngineDTO getCSpecEngineInfo(String cspecengineId);
    String getCSpecRuleSet(CSpecEngineIDRequest cSpecEngineIDRequest);
    ArrayList<CSpecEngineDTO> getCSpecEnginesInfo();
    String callScpecEngine(String evidenceListStr);
    ArrayList<CSpecEngineDTO> getCSpecEnginesInfoByCall();
}
