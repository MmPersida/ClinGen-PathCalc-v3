package com.persida.pathogenicity_calculator.RequestAndResponseModels;

import lombok.Data;

@Data
public class SortedCSpecEnginesRequest {
    private String condition;
    private String gene;
}
