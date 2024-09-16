package com.persida.pathogenicity_calculator.controllers;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.SortedCSpecEnginesRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.VarInterpSaveUpdateEvidenceDocRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.VarInterpUpdateFinalCallRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.VariantInterpretationSaveResponse;
import com.persida.pathogenicity_calculator.dto.*;
import com.persida.pathogenicity_calculator.services.CalculatorService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = "/rest/calculator")
public class CalculatorController {
    private static Logger logger = Logger.getLogger(CalculatorController.class);

    @Autowired
    private CalculatorService calculatorService;

    @RequestMapping(value = "/alleleRepository/{variantCID}", method= RequestMethod.GET)
    public String alleleRepository(@PathVariable String variantCID){
        if(variantCID == null || variantCID.isEmpty()){
            return null;
        }
        return calculatorService.getAlleleRepositoryData(variantCID);
    }

    @RequestMapping(value = "/getMyVariantInfoHG38Data/{myVariantInfoGH38Identifier:.+}", method= RequestMethod.GET)
    public String getMyVariantInfoHG38Data(@PathVariable String myVariantInfoGH38Identifier){
        if(myVariantInfoGH38Identifier == null || myVariantInfoGH38Identifier.isEmpty()){
            return null;
        }
        return calculatorService.getMyVariantInfoHG38Link(myVariantInfoGH38Identifier);
    }

    @RequestMapping(value = "/getInheritanceModes", method= RequestMethod.GET)
    public List<IheritanceDTO> getInheritanceModes(){
        return calculatorService.getInheritanceModes();
    }

    @RequestMapping(value = "/getFinalCalls", method= RequestMethod.GET)
    public List<FinalCallDTO> getFinalCalls(){
        return calculatorService.getFinalCalls();
    }
}
