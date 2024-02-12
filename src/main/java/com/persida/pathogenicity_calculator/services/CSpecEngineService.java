package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.CSpecEngineRuleSetRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.SortedCSpecEnginesRequest;
import com.persida.pathogenicity_calculator.dto.*;

import java.util.ArrayList;

public interface CSpecEngineService {
    ArrayList<CSpecEngineDTO> getCSpecEnginesInfoByCall();
    CSpecEngineDTO getCSpecEngineInfo(String cspecengineId);
    AssertionsDTO getCSpecRuleSet(CSpecEngineRuleSetRequest cSpecEngineRuleSetRequest);
    SortedCSpecEnginesDTO getSortedCSpecEngines(SortedCSpecEnginesRequest sortedCSpecEnginesRequest);
    String callScpecEngine(CSpecEngineRuleSetRequest cSpecEngineRuleSetRequest);
    String getRuleSetCriteriaCodes(String cspecengineId);
}
