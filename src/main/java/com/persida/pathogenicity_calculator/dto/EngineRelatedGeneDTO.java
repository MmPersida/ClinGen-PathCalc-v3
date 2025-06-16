package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class EngineRelatedGeneDTO {
    private String geneName;
    private String hgncId;
    private String ncbiId;
    private ArrayList<ConditionsTermAndIdDTO> conditions;

    public EngineRelatedGeneDTO(){}

    public EngineRelatedGeneDTO(String geneName, String hgncId, String ncbiId){
        this.geneName = geneName;
        this.hgncId = hgncId;
        this.ncbiId = ncbiId;
    }

    public EngineRelatedGeneDTO(String geneName, String hgncId, String ncbiId, ArrayList<ConditionsTermAndIdDTO> diseaseIDList){
        this.geneName = geneName;
        this.hgncId = hgncId;
        this.ncbiId = ncbiId;
        this.conditions = diseaseIDList;
    }

    public void addConditions(ConditionsTermAndIdDTO condition){
        if(condition == null){
            return;
        }
        if(conditions == null){
            conditions = new ArrayList<ConditionsTermAndIdDTO>();
        }
        conditions.add(condition);
    }

}
