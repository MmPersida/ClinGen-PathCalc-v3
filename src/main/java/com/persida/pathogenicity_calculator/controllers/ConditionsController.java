package com.persida.pathogenicity_calculator.controllers;

import com.persida.pathogenicity_calculator.dto.ConditionsTermAndIdDTO;
import com.persida.pathogenicity_calculator.services.ConditionsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/rest/conditions")
public class ConditionsController {

    private static Logger logger = Logger.getLogger(ConditionsController.class);

    @Autowired
    private ConditionsService conditionsService;

    @RequestMapping(value = "/getConditionsLike/{partialCondTerm}", method= RequestMethod.GET)
    public List<ConditionsTermAndIdDTO> getConditionsLike(@PathVariable String partialCondTerm){
        if(partialCondTerm == null || partialCondTerm.isEmpty()){
            return null;
        }
        return conditionsService.getConditionsLike(partialCondTerm);
    }
}
