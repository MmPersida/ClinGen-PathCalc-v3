package com.persida.pathogenicity_calculator.model.openAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import lombok.Data;

import javax.validation.constraints.Null;
import java.util.ArrayList;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassificationsResponse {
    private ClassificationsResponseData data;
    private ResponseMetadata metadata;
    private ResponseStatus status;

    public ClassificationsResponse(){
        this.data = new ClassificationsResponseData();
        this.metadata = new ResponseMetadata();
        this.status = new ResponseStatus(Constants.HTTP_status_200, "OK");
    }

    //error case
    public ClassificationsResponse(String message, String name){
        this.metadata = new ResponseMetadata();
        this.status = new ResponseStatus(Constants.HTTP_status_403, message, name);
    }

    @Data
    public class ClassificationsResponseData{
        private String variant;
        private ArrayList<Classification> classifications;

        public boolean addClassification(Classification c){
            if(c == null) {
                return false;
            }
            try{
                if(classifications == null){
                    classifications = new ArrayList<Classification>();
                }
                classifications.add(c);
                return true;
            }catch(Exception e){
                return false;
            }
        }
    }
}
