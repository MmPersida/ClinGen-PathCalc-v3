package com.persida.pathogenicity_calculator.controllers;

import com.persida.pathogenicity_calculator.dto.CSpecEngineIDRequest;
import com.persida.pathogenicity_calculator.dto.IheritanceDTO;
import com.persida.pathogenicity_calculator.dto.VariantInterpretationIDRequest;
import com.persida.pathogenicity_calculator.services.CalculatorService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest/calculator")
public class CalculatorController {
    private static Logger logger = Logger.getLogger(CalculatorController.class);

    @Autowired
    private CalculatorService calculatorService;

    @RequestMapping(value = "/alleleAndGeneData/{variantCID}", method= RequestMethod.GET)
    public String alleleAndGeneData(@PathVariable String variantCID){
        if(variantCID == null || variantCID.isEmpty()){
            return null;
        }
        return calculatorService.getAlleleAndGeneData(variantCID);
    }

    @RequestMapping(value = "/getInheritanceModes", method= RequestMethod.GET)
    public List<IheritanceDTO> getInheritanceModes(){
        return calculatorService.getInheritanceModes();
    }

    @PostMapping(value = "/cspecRuleSet",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public String getCSpecRuleSet(@RequestBody CSpecEngineIDRequest cSpecEngineIDRequest){
        return calculatorService.getCSpecRuleSet(cSpecEngineIDRequest);
    }

    @PostMapping(value = "/cspecEngineCaller",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public String cspecEngineCaller(@RequestBody String evidenceListStr){
        if(evidenceListStr == null || evidenceListStr.isEmpty()){
            return null;
        }
        return calculatorService.callScpecEngine(evidenceListStr);
    }
}
