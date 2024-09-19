package com.persida.pathogenicity_calculator.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;

@Data
public class NumOfCAIDsDTO {
    private int numberOfCaids;
    private String strValue;
    private HashSet<String> caidsList;

    public NumOfCAIDsDTO(String strValue){
       this.strValue = strValue;
    }

    public NumOfCAIDsDTO(int numberOfCaids, HashSet<String> caidsList){
        this.numberOfCaids = numberOfCaids;
        this.caidsList = caidsList;
    }

    public void incrementNumber(){
        ++this.numberOfCaids;
    }

    public void addCAID(String caid){
        if(caidsList != null){
            caidsList.add(caid);
        }
    }
}

