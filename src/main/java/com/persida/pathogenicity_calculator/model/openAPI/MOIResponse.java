package com.persida.pathogenicity_calculator.model.openAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.persida.pathogenicity_calculator.dto.IheritanceDTO;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MOIResponse {
    private MOIResponseData data;
    private ResponseMetadata metadata;
    private ResponseStatus status;

    public MOIResponse(){
        this.data = new MOIResponseData();
        this.metadata = new ResponseMetadata();
        this.status = new ResponseStatus(Constants.HTTP_status_200, "OK");
    }

    //error case
    public MOIResponse(String message, String name){
        this.metadata = new ResponseMetadata();
        this.status = new ResponseStatus(Constants.HTTP_status_403, message, name);
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class MOIResponseData{
        private List<IheritanceDTO> modesOfInheritance;
    }
}
