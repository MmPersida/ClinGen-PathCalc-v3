package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.CSpecEngineRuleSetRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.SortedCSpecEnginesRequest;
import com.persida.pathogenicity_calculator.dto.*;
import com.persida.pathogenicity_calculator.repository.entity.FinalCall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface CSpecEngineService {
    ArrayList<CSpecEngineDTO> getVCEPsDataByCall();
    CSpecEngineDTO getCSpecEngineInfo(String cspecengineId);
    ArrayList<CSpecEngineDTO> getVCEPsInfoByName(String vcepNamePartial);
    AssertionsDTO getCSpecRuleSet(CSpecEngineRuleSetRequest cSpecEngineRuleSetRequest);
    SortedCSpecEnginesDTO getSortedAndEnabledCSpecEngines(SortedCSpecEnginesRequest sortedCSpecEnginesRequest);
    FinalCallDTO callScpecEngine(CSpecEngineRuleSetRequest cSpecEngineRuleSetRequest);
    String getRuleSetCriteriaCodes(String cspecengineId);
    HashMap<String,String> getEvidenceCommentByEvdNameList(String cspecengineId, List<EvidenceDTO> evidenceList);
}
