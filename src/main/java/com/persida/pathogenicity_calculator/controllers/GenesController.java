package com.persida.pathogenicity_calculator.controllers;

import com.persida.pathogenicity_calculator.dto.CSpecEngineDTO;
import com.persida.pathogenicity_calculator.dto.GeneList;
import com.persida.pathogenicity_calculator.services.GenesService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/rest/genes")
public class GenesController {
    private static Logger logger = Logger.getLogger(GenesController.class);

    @Autowired
    private GenesService genesService;

    @PostMapping(value = "/engineDataForGenes",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public HashMap<String, CSpecEngineDTO> engineDataForGenes(@RequestBody GeneList geneList){
        if(geneList == null || geneList.getGenes() == null){
            return null;
        }
        return genesService.engineDataForGenes(geneList);
    }

    @RequestMapping(value = "/geneData/{geneNameID}", method= RequestMethod.GET)
    public String alleleAndGeneData(@PathVariable String geneNameID){
        if(geneNameID == null || geneNameID.isEmpty()){
            return null;
        }
        return genesService.getGeneData(geneNameID);
    }

    @RequestMapping(value = "/getGeneHGNCandNCBIids/{geneNameID}", method= RequestMethod.GET)
    public String[] getGeneHGNCandNCBIids(@PathVariable String geneNameID){
        if(geneNameID == null || geneNameID.isEmpty()){
            return null;
        }
        return genesService.getGeneHGNCandNCBIids(geneNameID);
    }
}
