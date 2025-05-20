package com.persida.pathogenicity_calculator.model.openAPI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.persida.pathogenicity_calculator.dto.IheritanceDTO;
import com.persida.pathogenicity_calculator.model.JWTHeaderAndPayloadData;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.constraints.NotNull;
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
                    Constants.HTTP_GET, true, Constants.AUTH_TYPE_TOKEN, createAnAuthHeaderList(), null));

            endpoints.add(new Endpoint("Get all classifications for this User",
                    "/api/classifications",
                    Constants.HTTP_GET, true, Constants.AUTH_TYPE_TOKEN, createAnAuthHeaderList(), null));

            endpoints.add(new Endpoint("Get all classifications by variant ID (CAID) for this User",
                    "/api/classifications/variant/{caid}",
                    Constants.HTTP_GET, true, Constants.AUTH_TYPE_TOKEN, createAnAuthHeaderList(), null));

            endpoints.add(new Endpoint("Get all diseases, a list of id's and terms, by providing a partial name/value.\n"+
                    "The value must contain at least 4 characters!",
                    "/api/diseases/{partialDiseaseTerm}",
                    Constants.HTTP_GET, true, Constants.AUTH_TYPE_TOKEN, createAnAuthHeaderList(), null));

            endpoints.add(new Endpoint("Get all the Modes Of Inheritance",
                    "/api/modesOfInheritance",
                    Constants.HTTP_GET, true, Constants.AUTH_TYPE_TOKEN, createAnAuthHeaderList(), null));

            endpoints.add(new Endpoint("Get all Specification, VCEP's, data",
                    "/api/specifications",
                    Constants.HTTP_GET, true, Constants.AUTH_TYPE_TOKEN, createAnAuthHeaderList(), null));

            endpoints.add(new Endpoint("Create a classification with or without and evidence set added.\n" +
                    "The classificationId property is left null for the use of this endpoint.\n"+
                    "When defining the disease and modeOfInheritance objects the property term must not be null. " +
                    "Setting the id property for the disease and modeOfInheritance objects will make the process faster.\n" +
                    "Property (array) evidenceTags can be null.",
                    "/api/classification/create",
                    Constants.HTTP_POST, true, Constants.AUTH_TYPE_TOKEN, createAnAuthHeaderList(), createBodyExampleCreateClass()));

            endpoints.add(new Endpoint("Update a classification with or without and evidence set added.\n" +
                    "The classificationId property must not be null for the use of this endpoint.\n"+
                    "When defining the disease and modeOfInheritance objects the property term must not be null. " +
                    "Setting the id property for the disease and modeOfInheritance objects will make the process faster.\n" +
                    "Property (array) evidenceTags can be null.",
                    "/api/classification/update",
                    Constants.HTTP_PUT, true, Constants.AUTH_TYPE_TOKEN, createAnAuthHeaderList(), createBodyExampleUpdateClass()));

            endpoints.add(new Endpoint("Delete a classification using it's ID.\n" +
                    "The classificationId property must not be null for the use of this endpoint.",
                    "/api/classification/delete",
                    Constants.HTTP_POST, true, Constants.AUTH_TYPE_TOKEN, createAnAuthHeaderList(), createBodyExampleDeleteClass()));

            endpoints.add(new Endpoint("Add one or more evidences to an existing classification.",
                    "/api/classification/addEvidence",
                    Constants.HTTP_POST, true, Constants.AUTH_TYPE_TOKEN, createAnAuthHeaderList(), createBodyExampleAddEvidence()));

            endpoints.add(new Endpoint("Remove one or more evidences from an existing classification.",
                    "/api/classification/removeEvidence",
                    Constants.HTTP_POST, true, Constants.AUTH_TYPE_TOKEN, createAnAuthHeaderList(), createBodyExampleRemoveEvidence()));
        }

        private List<CustomHeader> createAnAuthHeaderList(){
            List<CustomHeader> chList = new ArrayList<CustomHeader>();
            chList.add(new CustomHeader("Authorization", "Contains the token value"));
            return chList;
        }


        private RequestBody createBodyExampleCreateClass(){
            List<PropertyDesc> propertyList = new ArrayList<PropertyDesc>();
            propertyList.add(new PropertyDesc("caid", "string", "Not Null, Example: CA12345"));
            propertyList.add(new PropertyDesc("gene", "string", "Not Null, Example: BRCA1"));
            propertyList.add(new PropertyDesc("cspecId", "string", "Not Null, Example: GN001"));
            propertyList.add(createExampleDisease());
            propertyList.add(createExampleMOI());
            propertyList.add(createExampleEvidence());
            RequestBody rb = new RequestBody(propertyList);
            return rb;
        }
        private RequestBody createBodyExampleUpdateClass(){
            List<PropertyDesc> propertyList = new ArrayList<PropertyDesc>();
            propertyList.add(new PropertyDesc("caid", "string", "Not Null, Example: CA12345"));
            propertyList.add(new PropertyDesc("classificationId", "integer", "Not Null, Example: 12345"));
            propertyList.add(new PropertyDesc("gene", "string", "Not Null, Example: BRCA1"));
            propertyList.add(new PropertyDesc("cspecId", "string", "Not Null, Example: GN001"));
            propertyList.add(createExampleDisease());
            propertyList.add(createExampleMOI());
            propertyList.add(createExampleEvidence());
            RequestBody rb = new RequestBody(propertyList);
            return rb;
        }
        private RequestBody createBodyExampleDeleteClass(){
            RequestBody rb = new RequestBody();
            rb.addProperties(new PropertyDesc("classificationId", "integer", "Not Null, Example: 12345"));
            return rb;
        }
        private RequestBody createBodyExampleAddEvidence(){
            RequestBody rb = new RequestBody();
            rb.addProperties(new PropertyDesc("classificationId", "integer", "Not Null, Example: 12345"));
            rb.addProperties(createExampleEvidence());

            return rb;
        }
        private RequestBody createBodyExampleRemoveEvidence(){
            RequestBody rb = new RequestBody();
            rb.addProperties(new PropertyDesc("classificationId", "integer", "Not Null, Example: 12345"));
            rb.addProperties(new PropertyDesc("evidenceIDs", "integer-array", "Not Null, Example: [12,4,73]"));
            return rb;
        }


        private PropertyDesc createExampleDisease(){
            PropertyDesc disease = new PropertyDesc("disease", "obj", "Not Null");
            disease.addProperties(new PropertyDesc("id", "integer", "Nullable, Example: 12345"));
            disease.addProperties(new PropertyDesc("term", "string", "Not Null, Example: Thumb deformity"));
            return disease;
        }
        private PropertyDesc createExampleMOI(){
            PropertyDesc moi = new PropertyDesc("modeOfInheritance", "obj", "Not Null");
            moi.addProperties(new PropertyDesc("id", "integer", "Nullable, Example: 1"));
            moi.addProperties(new PropertyDesc("term", "string", "Not Null, Example: Autosomal Dominant"));
            return moi;
        }
        private PropertyDesc createExampleEvidence(){
            PropertyDesc et = new PropertyDesc("evidenceTags", "obj-array", "Nullable");
            PropertyDesc pdEvdTag = new PropertyDesc(null, "obj", "Evidence data object");
            pdEvdTag.addProperties(new PropertyDesc("type", "string", "Not Null, Example: BS1, PM1, PS2, BP2"));
            pdEvdTag.addProperties(new PropertyDesc("modifier", "string",
                    "Not Null, Values: Supporting, Moderate, Strong, Very Strong, Stand Alone or empty string"));
            pdEvdTag.addProperties(new PropertyDesc("summary", "string", "Nullable, text description."));
            et.setElement(pdEvdTag);
            return et;
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
        private RequestBody requestBody;

        public Endpoint(String name, String path, String method, boolean auth, String authType,
                        List<CustomHeader> necessaryHeaders, RequestBody requestBody){
             this.name = name;
             this.path = path;
             this.method = method;
             this.requiresAuthorization = auth;
             this.authorizationType = authType;
             this.necessaryHeaders = necessaryHeaders;
             this.requestBody = requestBody;
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class RequestBody{
        private List<PropertyDesc> propertyList;

        public RequestBody(){}
        public RequestBody(List<PropertyDesc> propertyList){
            this.propertyList = propertyList;
        }

        public boolean addProperties(PropertyDesc pd){
            if(pd == null) {
                return false;
            }
            try{
                if(this.propertyList == null){
                    this.propertyList = new ArrayList<PropertyDesc>();
                }
                this.propertyList.add(pd);
                return true;
            }catch(Exception e){
                return false;
            }
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class PropertyDesc{
        private String name;
        private String type;
        private String description;
        private List<PropertyDesc> propertyList;
        private PropertyDesc element;

        PropertyDesc(String name, String type, String description){
            this.name = name;
            this.type = type;
            this.description = description;
        }

        public boolean addProperties(PropertyDesc pd){
            if(pd == null) {
                return false;
            }
            try{
                if(this.propertyList == null){
                    this.propertyList = new ArrayList<PropertyDesc>();
                }
                this.propertyList.add(pd);
                return true;
            }catch(Exception e){
                return false;
            }
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
