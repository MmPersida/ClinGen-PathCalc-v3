package com.persida.pathogenicity_calculator.controllers;

import com.persida.pathogenicity_calculator.dto.EvidenceDocUpdateEvent;
import com.persida.pathogenicity_calculator.dto.VariantInterpretationDTO;
import com.persida.pathogenicity_calculator.dto.VariantInterpretationLoadRequest;
import com.persida.pathogenicity_calculator.dto.VariantInterpretationSaveResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.persida.pathogenicity_calculator.services.VariantInterpretationService;


@RestController
@RequestMapping("/rest/interpretation")
public class VariantInterpretationController {

    private static Logger logger = Logger.getLogger(VariantInterpretationController.class);

    @Autowired
    private VariantInterpretationService variantInterpretationService;

    @PostMapping(value = "/saveNewInterpretation",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private VariantInterpretationSaveResponse saveNewInterpretation(@RequestBody VariantInterpretationDTO saveInterpretationRequest) {
        return variantInterpretationService.saveNewInterpretation(saveInterpretationRequest);
    }

    @PostMapping(value = "/loadInterpretation",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private VariantInterpretationDTO loadInterpretation(@RequestBody VariantInterpretationLoadRequest loadInterpretationRequest) {
        return variantInterpretationService.loadInterpretation(loadInterpretationRequest);
    }

    @PostMapping(value = "/updateEvidenceDoc",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private VariantInterpretationSaveResponse updateEvidenceDoc(@RequestBody EvidenceDocUpdateEvent evidenceDocUpdateEvent) {
        return variantInterpretationService.updateEvidenceDoc(evidenceDocUpdateEvent);
    }

    @RequestMapping(value = "/getFinalCallForCaID/{variantCID}", method= RequestMethod.GET)
    public String getFinalCallForCaID(@PathVariable String variantCID){
        if(variantCID == null || variantCID.isEmpty()){
            return null;
        }
        return variantInterpretationService.getFinalCallForCaID(variantCID);
    }
}
