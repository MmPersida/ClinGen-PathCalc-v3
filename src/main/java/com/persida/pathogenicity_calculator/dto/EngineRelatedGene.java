package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class EngineRelatedGene {
    private String geneName;
    private ArrayList<String> diseaseIDList;

    public EngineRelatedGene(String geneName){
        this.geneName = geneName;
    }

    public EngineRelatedGene(String geneName, ArrayList<String> diseaseIDList){
        this.geneName = geneName;
        this.diseaseIDList = diseaseIDList;
    }

    public void addDiseases(String diseaseId){
        if(diseaseId == null || diseaseId.equals("")){
            return;
        }
        if(diseaseIDList == null){
            diseaseIDList = new ArrayList<String>();
        }
        diseaseIDList.add(diseaseId);
    }

    public String getDisesesAsStringArray(){
        if(diseaseIDList != null){
            String diseaseStrList = "";
            for(String dId : diseaseIDList){
                diseaseStrList = diseaseStrList + "\""+dId+"\",";
            }
            return diseaseStrList;
        }
        return null;
    }

    @Override
    public String toString(){
        String diseaseListJson = "["+getDisesesAsStringArray()+"]";
        return "{\"geneName\":\""+geneName+"\",\"diseaseIDList\":"+diseaseListJson+"}";
    }
}
