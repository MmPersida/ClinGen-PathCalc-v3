package com.persida.pathogenicity_calculator.model.openAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SRVCResponse {
    private SRVC data;
    private ResponseMetadata metadata;
    private ResponseStatus status;

    public SRVCResponse(){
        this.data = new SRVC();
        this.metadata = new ResponseMetadata();
        this.status = new ResponseStatus(Constants.HTTP_status_200, "OK");
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Component
    private class SRVC{

        @Value("${profileURL}")
        private String profileURL;

        private String rootURL;
        private List<Endpoint> endpoints;

        public SRVC(){
            this.rootURL = profileURL;
            if(this.rootURL == null){
                this.rootURL = "http://5.161.50.225:8067/pcalc";
            }

            if(endpoints == null){
                endpoints = new ArrayList<Endpoint>();
            }

            endpoints.add(new Endpoint("Get a classification by Id for this User",
                    "/api/classification/{classId}",
                    Constants.HTTP_GET, true, Constants.AUTH_TYPE_TOKEN, createAnAuthHeaderList()));

            endpoints.add(new Endpoint("Get all classifications for this User",
                    "/api/classifications",
                    Constants.HTTP_GET, true, Constants.AUTH_TYPE_TOKEN, createAnAuthHeaderList()));

            endpoints.add(new Endpoint("Get all classifications by variant ID (CAID) for this User",
                    "/api/classifications/variant/{caid}",
                    Constants.HTTP_GET, true, Constants.AUTH_TYPE_TOKEN, createAnAuthHeaderList()));

            endpoints.add(new Endpoint("Get all diseases, a list of id's and terms, by providing a partial name/value",
                    "/api/diseases/{partialDiseaseTerm}",
                    Constants.HTTP_GET, true, Constants.AUTH_TYPE_TOKEN, createAnAuthHeaderList()));

            endpoints.add(new Endpoint("Get all the Modes Of Inheritance",
                    "/api/modesOfInheritance",
                    Constants.HTTP_GET, true, Constants.AUTH_TYPE_TOKEN, createAnAuthHeaderList()));

            endpoints.add(new Endpoint("To be completed",
                    "/api/classification/create",
                    Constants.HTTP_POST, true, Constants.AUTH_TYPE_TOKEN, createAnAuthHeaderList()));

            endpoints.add(new Endpoint("To be completed",
                    "/api/classification/update",
                    Constants.HTTP_PUT, true, Constants.AUTH_TYPE_TOKEN, createAnAuthHeaderList()));
        }

        private List<CustomHeader> createAnAuthHeaderList(){
            List<CustomHeader> chList = new ArrayList<CustomHeader>();
            chList.add(new CustomHeader("Authorization", "Contains the token value"));
            return chList;
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Endpoint{
        private String name;
        private String path;
        private String method;
        private boolean requiresAuthorization;
        private String authorizationType;
        private List<CustomHeader> necessaryHeaders;

        public Endpoint(String name, String path, String method, boolean auth, String authType, List<CustomHeader> necessaryHeaders){
             this.name = name;
             this.path = path;
             this.method = method;
             this.requiresAuthorization = auth;
             this.authorizationType = authType;
             this.necessaryHeaders = necessaryHeaders;
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class CustomHeader{
        private String name;
        private String description;

        public CustomHeader(String name, String description){
            this.name = name;
            this.description = description;
        }
    }
}
