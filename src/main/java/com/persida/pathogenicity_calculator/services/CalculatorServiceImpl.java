package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.CSpecEngineIDRequest;
import com.persida.pathogenicity_calculator.dto.IheritanceDTO;
import com.persida.pathogenicity_calculator.repository.InheritanceRepository;
import com.persida.pathogenicity_calculator.repository.entity.Inheritance;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import com.persida.pathogenicity_calculator.utils.HTTPSConnector;
import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class CalculatorServiceImpl implements CalculatorService {
    static Logger logger = Logger.getLogger(CalculatorServiceImpl.class);

    @Value("${alleleRegistryUrl}")
    private String alleleRegistryUrl;

    @Value("${cspecRuleSetNoIdUrl}")
    private String cspecRuleSetNoIdUrl;

    @Value("${listOfAllCSpecEngines}")
    private String listOfAllCSpecEngines;

    @Value("${cspecAssertionsURL}")
    private String cspecAssertionsURL;

    @Autowired
    private InheritanceRepository inheritanceRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public String getAlleleAndGeneData(String variantCID){
        HashMap<String,String> httpProperties = new HashMap<String,String>();
        httpProperties.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_APP_JSON);

        String url = alleleRegistryUrl+variantCID;

        HTTPSConnector https = new HTTPSConnector();
        String response = https.sendHttpsRequest(url, Constants.HTTP_GET, null, httpProperties);
        return response;
    }

    @Override
    public List<IheritanceDTO> getInheritanceModes(){
        List<Inheritance> iList = inheritanceRepository.getInheritanceModes();
        if(iList == null || iList.size() == 0){
            return null;
        }

        List<IheritanceDTO> iDTOs = new ArrayList<IheritanceDTO>();
        for(Inheritance iObj : iList){
            IheritanceDTO iDTO = new IheritanceDTO();
            modelMapper.map(iObj, iDTO);
            iDTOs.add(iDTO);
        }
        return iDTOs;
    }

    @Override
    public String getCSpecRuleSet(CSpecEngineIDRequest cSpecEngineIDRequest){
        HashMap<String,String> httpProperties = new HashMap<String,String>();
        httpProperties.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_APP_JSON);

        String cspecRuleSetWithIdUrl = cspecRuleSetNoIdUrl+cSpecEngineIDRequest.getCspecEngineLdhId();

        HTTPSConnector https = new HTTPSConnector();
        String response = https.sendHttpsRequest(cspecRuleSetWithIdUrl, Constants.HTTP_GET, null, httpProperties);
        return response;
    }

    @Override
    public String callScpecEngine(String evidenceListStr){
        HashMap<String,String> httpProperties = new HashMap<String,String>();
        httpProperties.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_APP_JSON);

        HTTPSConnector https = new HTTPSConnector();
        String response = https.sendHttpsRequest(cspecAssertionsURL, Constants.HTTP_POST, evidenceListStr, httpProperties);
        return response;
    }
}
