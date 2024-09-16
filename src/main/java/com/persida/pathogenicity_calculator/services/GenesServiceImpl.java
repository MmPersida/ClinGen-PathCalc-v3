package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.dto.CSpecEngineDTO;
import com.persida.pathogenicity_calculator.dto.GeneList;
import com.persida.pathogenicity_calculator.repository.GeneRepository;
import com.persida.pathogenicity_calculator.repository.entity.Gene;
import com.persida.pathogenicity_calculator.repository.jpa.EngineDataForGeneJPA;
import com.persida.pathogenicity_calculator.utils.HTTPSConnector;
import com.persida.pathogenicity_calculator.utils.StackTracePrinter;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class GenesServiceImpl implements GenesService{
    static Logger logger = Logger.getLogger(GenesServiceImpl.class);

    @Value("${geneDataUrl}")
    private String geneDataUrl;

    @Autowired
    private GeneRepository genesRepository;

    private JSONParser jsonParser;


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
    public String[] getGeneHGNCandNCBIids(String geneName) {
        String response = this.getGeneData(geneName);
        if (response == null || response.isEmpty()) {
            return null;
        }

        String hgncId = null;
        String ncbiId = null;
        try {
            if (jsonParser == null) {
                jsonParser = new JSONParser();
            }
            JSONObject geneResponse = (JSONObject) jsonParser.parse(response);
            JSONObject externalRecordsObj = (JSONObject) geneResponse.get("externalRecords");
            if (externalRecordsObj == null) {
                return null;
            }

            if (externalRecordsObj.get("HGNC") != null) {
                hgncId = String.valueOf(((JSONObject) externalRecordsObj.get("HGNC")).get("id"));
            }
            if (externalRecordsObj.get("NCBI") != null) {
                ncbiId = String.valueOf(((JSONObject) externalRecordsObj.get("NCBI")).get("id"));
            }
        } catch (Exception e) {
            logger.info(StackTracePrinter.printStackTrace(e));
        }

        return new String[]{hgncId, ncbiId};
    }

    @Override
    public void compareAndUpdateGene(Gene newGene){
        Optional<Gene> geneOpt = genesRepository.findById(newGene.getGeneId());
        if(geneOpt != null && geneOpt.isPresent()){
            Gene currentGene = geneOpt.get();
            boolean updated = false;
            if(newGene.getHgncId() != null && !newGene.getHgncId().equals(currentGene.getHgncId())) {
                currentGene.setHgncId(newGene.getHgncId());
                updated = true;
            }
            if(newGene.getNcbiId() != null && !newGene.getNcbiId().equals(currentGene.getNcbiId())){
                currentGene.setNcbiId(newGene.getNcbiId());
                updated = true;
            }

            if(updated){
                genesRepository.save(currentGene);
            }
        }
    }

}
