package com.persida.pathogenicity_calculator.model.openAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiseasesResponse {
    private DiseasesResponseData data;
    private ResponseMetadata metadata;
    private ResponseStatus status;

    public DiseasesResponse(){
        this.data = new DiseasesResponseData();
        this.metadata = new ResponseMetadata();
        this.status = new ResponseStatus(Constants.HTTP_status_200, "OK");
    }

    //error case
    public DiseasesResponse(String message, String name){
        this.metadata = new ResponseMetadata();
        this.status = new ResponseStatus(Constants.HTTP_status_403, message, name);
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class DiseasesResponseData{
        private List<Disease> diseases;

        public boolean addDisease(Disease d){
            if(d == null) {
                return false;
            }
            try{
                if(this.diseases == null){
                    this.diseases = new ArrayList<Disease>();
                }
                this.diseases.add(d);
                return true;
            }catch(Exception e){
                return false;
            }
        }
    }
}
