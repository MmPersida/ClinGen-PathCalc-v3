package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.CSpecEngineDTO;
import com.persida.pathogenicity_calculator.dto.CSpecEngineIDRequest;
import com.persida.pathogenicity_calculator.dto.EngineRelatedGene;
import com.persida.pathogenicity_calculator.dto.IheritanceDTO;
import com.persida.pathogenicity_calculator.repository.CSpecRuleSetRepository;
import com.persida.pathogenicity_calculator.repository.InheritanceRepository;
import com.persida.pathogenicity_calculator.repository.entity.CSpecRuleSet;
import com.persida.pathogenicity_calculator.repository.entity.Gene;
import com.persida.pathogenicity_calculator.repository.entity.Inheritance;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import com.persida.pathogenicity_calculator.utils.HTTPSConnector;
import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CalculatorServiceImpl implements CalculatorService {
    static Logger logger = Logger.getLogger(CalculatorServiceImpl.class);

    @Value("${alleleRegistryUrl}")
    private String alleleRegistryUrl;

    @Value("${cspecRuleSetNoIdUrl}")
    private String cspecRuleSetNoIdUrl;

    @Value("${cspecAssertionsURL}")
    private String cspecAssertionsURL;

    @Autowired
    private InheritanceRepository inheritanceRepository;

    @Autowired
    private CSpecRuleSetRepository cSpecRuleSetRepository;

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
        Integer ruleSetID = null;

        CSpecRuleSet cspec = cSpecRuleSetRepository.getCSpecRuleSetById( cSpecEngineIDRequest.getCspecengineId());
        if(cspec != null){
            ruleSetID = cspec.getRuleSetId();
        }

        if(ruleSetID == null){
            return null;
        }

        HashMap<String,String> httpProperties = new HashMap<String,String>();
        httpProperties.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_APP_JSON);

        String cspecRuleSetWithIdUrl = cspecRuleSetNoIdUrl+ruleSetID;

        HTTPSConnector https = new HTTPSConnector();
        String response = https.sendHttpsRequest(cspecRuleSetWithIdUrl, Constants.HTTP_GET, null, httpProperties);
        return response;
    }

    @Override
    public ArrayList<CSpecEngineDTO> getCSpecEnginesInfo(){
        List<CSpecRuleSet> allEnginesInfo = cSpecRuleSetRepository.findAll();
        if(allEnginesInfo == null || allEnginesInfo.size() == 0){
            return null;
        }

        EngineRelatedGene erGene = null;
        Set<EngineRelatedGene> erGenesSet = null;
        ArrayList<CSpecEngineDTO> enginesDTOList = new ArrayList<CSpecEngineDTO>();
        for(CSpecRuleSet e : allEnginesInfo){
            if(e.getGenes() != null && e.getGenes().size() > 0){
                erGenesSet = new HashSet<EngineRelatedGene>();
                Set<Gene> gList = e.getGenes();
                for(Gene g : gList){
                    if(g.getConditionNames() != null && !g.getConditionNames().equals("")){
                        String[] temStringArray = g.getConditionNames().split(",");
                        ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(temStringArray));
                        if(stringList != null && stringList.size() > 0){
                            erGene = new EngineRelatedGene(g.getGeneId(), stringList);
                        }

                    }else{
                        erGene = new EngineRelatedGene(g.getGeneId());
                    }
                    if(erGene != null){
                        erGenesSet.add(erGene);
                    }
                }
            }

            enginesDTOList.add(new CSpecEngineDTO(e.getEngineId(), e.getEngineSummary(), e.getOrganizationName(),
                    e.getRuleSetId(), e.getRuleSetURL(), erGenesSet));
        }
        return enginesDTOList;
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
