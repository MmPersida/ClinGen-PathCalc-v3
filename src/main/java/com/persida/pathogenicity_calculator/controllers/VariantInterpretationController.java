package com.persida.pathogenicity_calculator.controllers;

import com.persida.pathogenicity_calculator.dto.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.persida.pathogenicity_calculator.services.VariantInterpretationService;

import java.util.List;


@RestController
@RequestMapping("/rest/interpretation")
public class VariantInterpretationController {

    private static Logger logger = Logger.getLogger(VariantInterpretationController.class);

    @Autowired
    private VariantInterpretationService variantInterpretationService;

    @PostMapping(value = "/saveNewEvidence",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private VariantInterpretationSaveResponse saveNewEvidence(@RequestBody VariantInterpretationDTO saveInterpretationRequest) {
        return variantInterpretationService.saveNewEvidence(saveInterpretationRequest);
    }

    @PostMapping(value = "/loadInterpretation",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private VariantInterpretationDTO loadInterpretation(@RequestBody VariantInterpretationLoadRequest loadInterpretationRequest) {
        return variantInterpretationService.loadInterpretation(loadInterpretationRequest);
    }

    @PostMapping(value = "/saveNewInterpretation",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private VariantInterpretationSaveResponse saveNewInterpretation(@RequestBody VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq) {
        return variantInterpretationService.saveNewInterpretation(viSaveEvdUpdateReq);
    }

    @PostMapping(value = "/updateEvidenceDoc",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private VariantInterpretationSaveResponse updateEvidenceDoc(@RequestBody VarInterpSaveUpdateEvidenceDocRequest viSaveEvdUpdateReq) {
        return variantInterpretationService.updateEvidenceDoc(viSaveEvdUpdateReq);
    }

    @RequestMapping(value = "/getVIBasicDataForCaid/{variantCID}", method= RequestMethod.GET)
    public List<VIBasicDTO> getVIBasicDataForCaid(@PathVariable String variantCID){
        if(variantCID == null || variantCID.isEmpty()){
            return null;
        }
        return variantInterpretationService.getVIBasicDataForCaid(variantCID);
    }
}
