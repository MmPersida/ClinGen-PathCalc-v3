package com.persida.pathogenicity_calculator.model.openAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpecificationsResponse {
    private SpecificationsResponseData data;
    private ResponseMetadata metadata;
    private ResponseStatus status;

    public SpecificationsResponse(){
        this.data = new SpecificationsResponseData();
        this.metadata = new ResponseMetadata();
        this.status = new ResponseStatus(Constants.HTTP_status_200, "OK");
    }

    //error case
    public SpecificationsResponse(String message, String name){
        this.metadata = new ResponseMetadata();
        this.status = new ResponseStatus(Constants.HTTP_status_403, message, name);
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class SpecificationsResponseData{
        private List<Specification> specifications;

        public boolean addSpecification(Specification s){
            if(s == null) {
                return false;
            }
            try{
                if(this.specifications == null){
                    this.specifications = new ArrayList<Specification>();
                }
                this.specifications.add(s);
                return true;
            }catch(Exception e){
                return false;
            }
        }
    }
}
