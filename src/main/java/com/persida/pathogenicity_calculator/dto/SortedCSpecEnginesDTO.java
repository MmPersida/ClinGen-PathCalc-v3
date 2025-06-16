package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class SortedCSpecEnginesDTO {
    ArrayList<CSpecEngineDTO> geneAndConditionList;
    ArrayList<CSpecEngineDTO> conditionList;
    ArrayList<CSpecEngineDTO> geneList;
    ArrayList<CSpecEngineDTO> othersList;

    public void addToGeneAndConditionList(CSpecEngineDTO cseDTO){
        if(cseDTO == null){
            return;
        }
        if(geneAndConditionList == null){
            geneAndConditionList = new ArrayList<CSpecEngineDTO>();
        }
        geneAndConditionList.add(cseDTO);
    }

    public void addToConditionList(CSpecEngineDTO cseDTO){
        if(cseDTO == null){
            return;
        }
        if(conditionList == null){
            conditionList = new ArrayList<CSpecEngineDTO>();
        }
        conditionList.add(cseDTO);
    }

    public void addToGeneList(CSpecEngineDTO cseDTO){
        if(cseDTO == null){
            return;
        }
        if(geneList == null){
            geneList = new ArrayList<CSpecEngineDTO>();
        }
        geneList.add(cseDTO);
    }

    public void addToOthersList(CSpecEngineDTO cseDTO){
        if(cseDTO == null){
            return;
        }
        if(othersList == null){
            othersList = new ArrayList<CSpecEngineDTO>();
        }
        othersList.add(cseDTO);
    }
}
