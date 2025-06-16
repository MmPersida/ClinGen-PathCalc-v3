package com.persida.pathogenicity_calculator.model.openAPI;
import lombok.Data;

import java.util.ArrayList;
import java.util.Map;

@Data
public class AssertionsDTOResponse {
    private Map<String, ArrayList<RuleConditionResponse>> reachedAssertions;
    private Map<String, ArrayList<RuleConditionResponse>> failedAssertions;
}
