package com.persida.pathogenicity_calculator.controllers;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.SortedCSpecEnginesRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.VarInterpSaveUpdateEvidenceDocRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.VarInterpUpdateFinalCallRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.VariantInterpretationSaveResponse;
import com.persida.pathogenicity_calculator.dto.CSpecEngineDTO;
import com.persida.pathogenicity_calculator.dto.GeneList;
import com.persida.pathogenicity_calculator.dto.IheritanceDTO;
import com.persida.pathogenicity_calculator.dto.SortedCSpecEnginesDTO;
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

    @RequestMapping(value = "/", method= RequestMethod.GET)
    public String engioneDataForGenes(@PathVariable String variantCID){
        if(variantCID == null || variantCID.isEmpty()){
            return null;
        }
        return null;
    }

    @PostMapping(value = "/engineDataForGenes",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HashMap<String, CSpecEngineDTO> engineDataForGenes(@RequestBody GeneList geneList){
        if(geneList == null || geneList.getGenes() == null){
            return null;
        }
        return calculatorService.engineDataForGenes(geneList);
    }

    @RequestMapping(value = "/geneData/{geneNameID}", method= RequestMethod.GET)
    public String alleleAndGeneData(@PathVariable String geneNameID){
        if(geneNameID == null || geneNameID.isEmpty()){
            return null;
        }
        return calculatorService.getGeneData(geneNameID);
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
}
