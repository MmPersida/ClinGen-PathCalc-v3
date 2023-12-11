package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class AssertionsDTO {
    private  Map<String, ArrayList<RuleConditionDTO>> reachedRuleSetMap;
    private Map<String, ArrayList<RuleConditionDTO>> failedRuleSetMap;

    public AssertionsDTO(){
        this.reachedRuleSetMap = new HashMap<String, ArrayList<RuleConditionDTO>>();
        this.failedRuleSetMap = new HashMap<String, ArrayList<RuleConditionDTO>>();
    }

    public void addToReachedRuleSet(String key, ArrayList<RuleConditionDTO> rcDTO){
        this.reachedRuleSetMap.put(key, rcDTO);
    }
    public void addReachedRuleSetConditionForKey(String key, RuleConditionDTO rcDTO){
        this.reachedRuleSetMap.get(key).add(rcDTO);
    }


    public void addToFailedRuleSet(String key, ArrayList<RuleConditionDTO> rcDTO){
        this.failedRuleSetMap.put(key, rcDTO);
    }
    public void addFailedRuleSetConditionForKey(String key, RuleConditionDTO rcDTO){
        this.failedRuleSetMap.get(key).add(rcDTO);
    }
}
