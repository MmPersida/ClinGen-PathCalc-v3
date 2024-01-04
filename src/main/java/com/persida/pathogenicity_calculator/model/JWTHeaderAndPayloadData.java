package com.persida.pathogenicity_calculator.model;

import com.persida.pathogenicity_calculator.utils.StackTracePrinter;
import lombok.Data;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Data
public class JWTHeaderAndPayloadData {
    static Logger logger = Logger.getLogger(JWTHeaderAndPayloadData.class);

    private String algorithm;
    private String username;
    private String fName;
    private String lName;
    private long tokenExpTime;
    private JSONParser jsonParser;

    public JWTHeaderAndPayloadData(String header, String payload){
        if(jsonParser == null){
            jsonParser = new JSONParser();
        }
        try {
            JSONObject headerObj = (JSONObject) jsonParser.parse(header);
            this.algorithm = String.valueOf(headerObj.get("alg"));

            JSONObject payloadObj = (JSONObject) jsonParser.parse(payload);
            this.username = String.valueOf(payloadObj.get("idStr"));
                JSONObject nameObj = (JSONObject) payloadObj.get("name");
            this.fName = String.valueOf(nameObj.get("first"));
            this.lName = String.valueOf(nameObj.get("last"));
            this.tokenExpTime = Long.parseLong(String.valueOf(payloadObj.get("exp")));
        }catch(Exception e){
            logger.error("Unable to parse JWT Header and/or Payload!");
            logger.error(StackTracePrinter.printStackTrace(e));
        }
    }
}
