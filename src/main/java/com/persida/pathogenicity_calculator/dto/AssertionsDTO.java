package com.persida.pathogenicity_calculator.dto;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Data
public class AssertionsDTO {
    private  Map<String, ArrayList<RuleConditionDTO>> reachedAssertions;
    private Map<String, ArrayList<RuleConditionDTO>> failedAssertions;

    public AssertionsDTO(){
        this.reachedAssertions = new HashMap<String, ArrayList<RuleConditionDTO>>();
        this.failedAssertions = new HashMap<String, ArrayList<RuleConditionDTO>>();
    }

    public void addToReachedAssertions(String key, ArrayList<RuleConditionDTO> rcDTO){
        this.reachedAssertions.put(key, rcDTO);
    }
    public void addReachedAssertionConditionForKey(String key, RuleConditionDTO rcDTO){
        this.reachedAssertions.get(key).add(rcDTO);
    }


    public void addToFailedAssertions(String key, ArrayList<RuleConditionDTO> rcDTO){
        this.failedAssertions.put(key, rcDTO);
    }
    public void addFailedAssertionConditionForKey(String key, RuleConditionDTO rcDTO){
        this.failedAssertions.get(key).add(rcDTO);
    }
}
