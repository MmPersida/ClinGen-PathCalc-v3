package com.persida.pathogenicity_calculator.controllers;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.DetermineCAIDRequest;
import com.persida.pathogenicity_calculator.dto.NumOfCAIDsDTO;
import com.persida.pathogenicity_calculator.dto.VariantInterpretationDTO;
import com.persida.pathogenicity_calculator.services.IntroService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/intro")
public class IntroController {
    private static Logger logger = Logger.getLogger(IntroController.class);

    @Autowired
    public IntroService introService;

    @RequestMapping(value = "/getInterpretedCaIDs/{partialName}", method= RequestMethod.GET)
    private List<String> getInterpretedVariantCAIDsLike(@PathVariable("partialName") String  partialCAID) {
        if(partialCAID == null || partialCAID.isEmpty() || partialCAID.length() < 2){
            return null;
        }
        return introService.getInterpretedVariantCAIDsLike(partialCAID);
    }

    @RequestMapping(value = "/getRecentlyInterVariants", method= RequestMethod.GET)
    private  List<VariantInterpretationDTO> getRecentlyInterpretedVariants() {
        return introService.getRecentlyInterpretedVariants();
    }

    @PostMapping(value = "/determineCIAD",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private String determineCIAD(@RequestBody DetermineCAIDRequest determineCIADRequest) {
        return introService.determineCIAD(determineCIADRequest);
    }

    @RequestMapping(value = "/getSummaryOfClassifiedVariants", method= RequestMethod.GET)
    private ArrayList<NumOfCAIDsDTO[]> getSummaryOfClassifiedVariants(){
        return introService.getSummaryOfClassifiedVariants();
    }
}
