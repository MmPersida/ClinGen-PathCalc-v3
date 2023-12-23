package com.persida.pathogenicity_calculator.controllers;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.CSpecEngineRuleSetRequest;
import com.persida.pathogenicity_calculator.RequestAndResponseModels.SortedCSpecEnginesRequest;
import com.persida.pathogenicity_calculator.dto.*;
import com.persida.pathogenicity_calculator.services.CSpecEngineService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rest/cspecengines")
public class CSpecEnginesController {
    private static Logger logger = Logger.getLogger(CSpecEnginesController.class);

    @Autowired
    private CSpecEngineService cSpecEngineService;

    @RequestMapping(value = "/getCSpecEngineInfo/{cspecengineId}", method= RequestMethod.GET)
    public CSpecEngineDTO getCSpecEngineInfo(@PathVariable String cspecengineId){
        if(cspecengineId == null || cspecengineId.isEmpty()){
            return null;
        }
        return cSpecEngineService.getCSpecEngineInfo(cspecengineId);
    }

    @PostMapping(value = "/getAssertionsFromRuleSet",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AssertionsDTO getCSpecRuleSet(@RequestBody CSpecEngineRuleSetRequest cSpecEngineRuleSetRequest){
        return cSpecEngineService.getCSpecRuleSet(cSpecEngineRuleSetRequest);
    }

    @PostMapping(value = "/getSortedCSpecEngines",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public SortedCSpecEnginesDTO getSortedCSpecEngines(@RequestBody SortedCSpecEnginesRequest sortedCSpecEnginesRequest){
        if(sortedCSpecEnginesRequest == null && sortedCSpecEnginesRequest.getGene() != null
                && sortedCSpecEnginesRequest.getCondition() != null){
            return null;
        }
        return cSpecEngineService.getSortedCSpecEngines(sortedCSpecEnginesRequest);
    }

    @PostMapping(value = "/cspecEngineCaller",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public String cspecEngineCaller(@RequestBody CSpecEngineRuleSetRequest cSpecEngineRuleSetRequest){
        if(cSpecEngineRuleSetRequest == null){
            return null;
        }
        return cSpecEngineService.callScpecEngine(cSpecEngineRuleSetRequest);
    }
}
