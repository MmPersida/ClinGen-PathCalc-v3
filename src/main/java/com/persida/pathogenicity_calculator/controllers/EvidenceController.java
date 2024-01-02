package com.persida.pathogenicity_calculator.controllers;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.DeleteEvdLinkRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.EvidenceLinksRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.EvidenceSummaryRequest;
import com.persida.pathogenicity_calculator.dto.EvidenceLinkDTO;
import com.persida.pathogenicity_calculator.dto.EvidenceLinksDTO;
import com.persida.pathogenicity_calculator.dto.EvidenceListDTO;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.VariantInterpretationSaveResponse;
import com.persida.pathogenicity_calculator.dto.EvidenceSummaryDTO;
import com.persida.pathogenicity_calculator.services.EvidenceService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

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

    @PostMapping(value = "/getEvdSummaryForVIIdAndEvdTags",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private  HashMap<String, EvidenceSummaryDTO> getEvdSummaryForVIIdAndEvdTags(@RequestBody EvidenceSummaryRequest evdSummaryReq) {
        return evidenceService.getEvdSummaryForVIIdAndEvdTags(evdSummaryReq);
    }

    @PostMapping(value = "/getLinksFroVIIdAndEvdTag",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private List<EvidenceLinkDTO> getLinksFroVIIdAndEvdTag(@RequestBody EvidenceLinksRequest evdLinksReq) {
        return evidenceService.getLinksFroVIIdAndEvdTag(evdLinksReq);
    }

    @PostMapping(value = "/deleteEvidenceLinkById",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private String deleteEvidenceLinkById(@RequestBody DeleteEvdLinkRequest deleteEvdLinkRequest) {
        return evidenceService.deleteEvidenceLinkById(deleteEvdLinkRequest);
    }

    @PostMapping(value = "/saveEvidenceLinks",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private String saveEvidenceLinks(@RequestBody EvidenceLinksDTO evidenceLinksDTO) {
        return evidenceService.saveEvidenceLinks(evidenceLinksDTO);
    }
}
