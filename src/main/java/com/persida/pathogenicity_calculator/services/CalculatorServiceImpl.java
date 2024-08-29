package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.CSpecEngineDTO;
import com.persida.pathogenicity_calculator.dto.FinalCallDTO;
import com.persida.pathogenicity_calculator.dto.GeneList;
import com.persida.pathogenicity_calculator.dto.IheritanceDTO;
import com.persida.pathogenicity_calculator.repository.FinalCallRepository;
import com.persida.pathogenicity_calculator.repository.GeneRepository;
import com.persida.pathogenicity_calculator.repository.InheritanceRepository;
import com.persida.pathogenicity_calculator.repository.entity.FinalCall;
import com.persida.pathogenicity_calculator.repository.entity.Inheritance;
import com.persida.pathogenicity_calculator.repository.jpa.EngineDataForGeneJPA;
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

    @Value("${geneDataUrl}")
    private String geneDataUrl;

    @Value("${myVariantInfoHG38Link}")
    private String myVariantInfoHG38Link;

    @Autowired
    private InheritanceRepository inheritanceRepository;
    @Autowired
    private GeneRepository genesRepository;
    @Autowired
    private FinalCallRepository finalCallRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public String getAlleleRepositoryData(String variantCID){
        HashMap<String,String> httpProperties = new HashMap<String,String>();
        httpProperties.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_APP_JSON);

        String url = alleleRegistryUrl+variantCID;

        HTTPSConnector https = new HTTPSConnector();
        String response = https.sendHttpsRequest(url, Constants.HTTP_GET, null, httpProperties);
        return response;
    }

    @Override
    public HashMap<String, CSpecEngineDTO> engineDataForGenes(GeneList geneList){
        HashMap<String, CSpecEngineDTO> genesEngineInfoMap = new HashMap<String, CSpecEngineDTO>();
        EngineDataForGeneJPA edgJPA = null;
        for(String gName: geneList.getGenes()){
            edgJPA = genesRepository.getEngineDataForGene(gName);
            if(edgJPA != null && edgJPA.getEngineId() != null){
                genesEngineInfoMap.put(gName, new CSpecEngineDTO(edgJPA.getEngineId(), edgJPA.getEnabled(), edgJPA.getOrganization()));
            }
        }
        return genesEngineInfoMap;
    }

    @Override
    public String getGeneData(String geneNameID){
        HashMap<String,String> httpProperties = new HashMap<String,String>();
        httpProperties.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_APP_JSON);

        String url = geneDataUrl+geneNameID;

        HTTPSConnector https = new HTTPSConnector();
        String response = https.sendHttpsRequest(url, Constants.HTTP_GET, null, httpProperties);
        return response;
    }

    @Override
    public String getMyVariantInfoHG38Link(String myVariantInfoGH38Identifier){
        HashMap<String,String> httpProperties = new HashMap<String,String>();

        String url = myVariantInfoHG38Link.replace("IDENTIFIER",myVariantInfoGH38Identifier);

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
    public List<FinalCallDTO> getFinalCalls(){
        List<FinalCall> fcList = finalCallRepository.getFinalCallsOrdered();
        if(fcList == null || fcList.size() == 0){
            return null;
        }

        List<FinalCallDTO> fcDTOs = new ArrayList<FinalCallDTO>();
        for(FinalCall fcObj : fcList){
            fcDTOs.add(new FinalCallDTO(fcObj.getId(), fcObj.getTerm()));
        }
        return fcDTOs;
    }
}
