package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class EngineRelatedGeneDTO {
    private String geneName;
    private ArrayList<ConditionsTermAndIdDTO> conditions;

    public EngineRelatedGeneDTO(String geneName){
        this.geneName = geneName;
    }

    public EngineRelatedGeneDTO(String geneName, ArrayList<ConditionsTermAndIdDTO> diseaseIDList){
        this.geneName = geneName;
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

    public String getDiseasesAsJSONArray(){
        if(conditions != null){
            String diseaseStrList = "";
            for(ConditionsTermAndIdDTO e : conditions){
                diseaseStrList += "{\"id\":\""+e.getConditionId()+"\",\"term\":\""+e.getTerm()+"\"},";
            }
            return diseaseStrList;
        }
        return null;
    }

    @Override
    public String toString(){
        String diseaseListJson = "["+getDiseasesAsJSONArray()+"]";
        return "{\"geneName\":\""+geneName+"\",\"diseaseIDList\":"+diseaseListJson+"}";
    }
}
