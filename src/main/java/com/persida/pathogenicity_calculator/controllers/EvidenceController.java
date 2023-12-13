package com.persida.pathogenicity_calculator.controllers;

import com.persida.pathogenicity_calculator.dto.EvidenceListDTO;
import com.persida.pathogenicity_calculator.dto.VariantInterpretationSaveResponse;
import com.persida.pathogenicity_calculator.services.EvidenceService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/evidence")
public class EvidenceController {
    private static Logger logger = Logger.getLogger(EvidenceController.class);

    @Autowired
    private EvidenceService evidenceService;

    @PostMapping(value = "/saveNewEvidence",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private VariantInterpretationSaveResponse saveNewEvidence(@RequestBody EvidenceListDTO saveEvidenceSetDTO) {
        return evidenceService.saveNewEvidence(saveEvidenceSetDTO);
    }

    @PostMapping(value = "/deleteEvidence",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private VariantInterpretationSaveResponse deleteEvidence(@RequestBody EvidenceListDTO deleteEvidenceSetDTO) {
        return evidenceService.deleteEvidence(deleteEvidenceSetDTO);
    }
}
