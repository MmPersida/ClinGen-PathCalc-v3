package com.persida.pathogenicity_calculator.model.openAPI.requestModels;
import com.persida.pathogenicity_calculator.model.openAPI.ResponseMetadata;
import com.persida.pathogenicity_calculator.model.openAPI.ResponseStatus;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import lombok.Data;
@Data
public class TokenResponse {
    private TokenResponseData data;
    private ResponseMetadata metadata;
    private ResponseStatus status;

    public TokenResponse(String tokenValue, long tokenExpTimeInMillis){
        this.data = new TokenResponseData(tokenValue, tokenExpTimeInMillis);
        this.metadata = new ResponseMetadata();
        this.status = new ResponseStatus(Constants.HTTP_status_200, "OK");
    }

    //error case
    public TokenResponse(String msg){
        this.metadata = new ResponseMetadata();
        this.status = new ResponseStatus(Constants.HTTP_status_403, msg, "OK");
    }

    @Data
    private class TokenResponseData{
        private String jwt;
        private long expTimeInMillis;

        public TokenResponseData(String tokenValue, long tokenExpTimeInMillis){
            this.jwt = tokenValue;
            this.expTimeInMillis = tokenExpTimeInMillis;
        }
    }
}
