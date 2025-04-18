package com.persida.pathogenicity_calculator.model.openAPI;

import com.persida.pathogenicity_calculator.utils.constants.Constants;
import lombok.Data;

@Data
public class ResponseStatus {
    private Integer code;
    private String msg;
    private String name;

    public ResponseStatus(int code, String name){
        this.code = code;
        this.name = name;
    }

    public ResponseStatus(int code, String msg, String name){
        this.code = code;
        this.msg = msg;
        this.name = name;
    }
}