package com.persida.pathogenicity_calculator.model.openAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvidenceResponse {
    private EvidenceResponseData data;
    private ResponseMetadata metadata;
    private ResponseStatus status;

    public EvidenceResponse(){
        this.data = new EvidenceResponseData();
        this.metadata = new ResponseMetadata();
        this.status = new ResponseStatus(Constants.HTTP_status_200, "OK");
    }

    //error case
    public EvidenceResponse(String message, String name){
        this.metadata = new ResponseMetadata();
        this.status = new ResponseStatus(Constants.HTTP_status_403, message, name);
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class EvidenceResponseData{
        private int classId;
        private List<EvidenceR> evidences;

        public boolean addEvidence(EvidenceR evd){
            if(evd == null) {
                return false;
            }
            try{
                if(this.evidences == null){
                    this.evidences = new ArrayList<EvidenceR>();
                }
                this.evidences.add(evd);
                return true;
            }catch(Exception e){
                return false;
            }
        }
    }
}
