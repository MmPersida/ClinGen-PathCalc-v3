package com.persida.pathogenicity_calculator.model.openAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.persida.pathogenicity_calculator.dto.RuleConditionDTO;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import lombok.Data;

import java.util.ArrayList;
import java.util.Map;

@Data
public class AssertionsResponse {
    private AssertionsResponseData data;
    private ResponseMetadata metadata;
    private ResponseStatus status;

    public AssertionsResponse(){
        this.data = new AssertionsResponseData();
        this.metadata = new ResponseMetadata();
        this.status = new ResponseStatus(Constants.HTTP_status_200, "OK");
    }

    //error case
    public AssertionsResponse(String message, String name){
        this.metadata = new ResponseMetadata();
        this.status = new ResponseStatus(Constants.HTTP_status_403, message, name);
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class AssertionsResponseData{
        private String caid;
        private Integer classificationId;
        private AssertionsDTOResponse assertions;
    }
}
