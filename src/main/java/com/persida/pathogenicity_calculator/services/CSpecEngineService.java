package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.CSpecEngineRuleSetRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.SortedCSpecEnginesRequest;
import com.persida.pathogenicity_calculator.dto.*;
import com.persida.pathogenicity_calculator.repository.entity.FinalCall;

import java.util.ArrayList;

public interface CSpecEngineService {
    ArrayList<CSpecEngineDTO> getVCEPsDataByCall();
    ArrayList<CSpecEngineDTO> getVCEPsInfoByName(String vcepNamePartial);
    CSpecEngineDTO getCSpecEngineInfo(String cspecengineId);
    AssertionsDTO getCSpecRuleSet(CSpecEngineRuleSetRequest cSpecEngineRuleSetRequest);
    SortedCSpecEnginesDTO getSortedAndEnabledCSpecEngines(SortedCSpecEnginesRequest sortedCSpecEnginesRequest);
    FinalCallDTO callScpecEngine(CSpecEngineRuleSetRequest cSpecEngineRuleSetRequest);
    String getRuleSetCriteriaCodes(String cspecengineId);
}
