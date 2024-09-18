package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.RequestAndResponseModels.DetermineCAIDRequest;
import com.persida.pathogenicity_calculator.dto.FinalCallDTO;
import com.persida.pathogenicity_calculator.dto.VariantInterpretationDTO;
import com.persida.pathogenicity_calculator.repository.FinalCallRepository;
import com.persida.pathogenicity_calculator.repository.VariantInterpretationRepository;
import com.persida.pathogenicity_calculator.repository.entity.FinalCall;
import com.persida.pathogenicity_calculator.repository.entity.VariantInterpretation;
import com.persida.pathogenicity_calculator.repository.jpa.SummaryOfClassifiedVariantsJPA;
import com.persida.pathogenicity_calculator.services.userServices.UserService;
import com.persida.pathogenicity_calculator.utils.HTTPSConnector;
import com.persida.pathogenicity_calculator.utils.StackTracePrinter;
import com.persida.pathogenicity_calculator.utils.URIEncoder;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Service
public class IntroServiceImpl implements  IntroService{
    private static Logger logger = Logger.getLogger(IntroServiceImpl.class);

    @Value("${dbSNP_url}")
    private String dbSNP_url;

    @Value("${hgvs_url}")
    private String hgvs_url;

    @Value("${clinvar_url}")
    private String clinvar_url;

    @Value("${clinvarRCV_url}")
    private String clinvarRCV_url;

    @Value("${gnomad_2_url}")
    private String gnomad_2_url;
    @Value("${gnomad_3_url}")
    private String gnomad_3_url;
    @Value("${gnomad_4_url}")
    private String gnomad_4_url;

    @Value("${myvariantinfohg38_url}")
    private String myvariantinfohg38_url;

    @Autowired
    private UserService userService;
    @Autowired
    private VariantInterpretationRepository variantInterpretationRepository;
    @Autowired
    private FinalCallRepository finalCallRepository;

    @Autowired
    private ModelMapper modelMapper;

    private JSONParser jsonParser;

    @Override
    public List<String> getInterpretedVariantCAIDsLike(String partialCAID){
        Integer userId = userService.getCurrentUserId();
        return variantInterpretationRepository.getInterpretedVariantCAIDsLike(userId, partialCAID);
    }

    @Override
    public List<VariantInterpretationDTO> getRecentlyInterpretedVariants(){
        Integer userId = userService.getCurrentUserId();
        List<VariantInterpretation>  variantInterpList = variantInterpretationRepository.getRecentlyInterpretedVariants(userId);
        if(variantInterpList == null || variantInterpList.size() == 0){
            return null;
        }
        List<VariantInterpretationDTO> variantDTOList = new ArrayList<VariantInterpretationDTO>();
        for(VariantInterpretation varInterp :variantInterpList){
            VariantInterpretationDTO viTDO = new VariantInterpretationDTO();
            viTDO.setInterpretationId(varInterp.getId());
            viTDO.setCaid(varInterp.getVariant().getCaid());
            viTDO.setCalculatedFinalCall(new FinalCallDTO(varInterp.getFinalCall().getId(), varInterp.getFinalCall().getTerm()));
            if(varInterp.getDeterminedFinalCall() != null){
                viTDO.setDeterminedFinalCall(new FinalCallDTO(varInterp.getDeterminedFinalCall().getId(), varInterp.getDeterminedFinalCall().getTerm()));
            }
            viTDO.setCondition(varInterp.getCondition().getTerm());
            viTDO.setInheritance(varInterp.getInheritance().getTerm());
            variantDTOList.add(viTDO);
        }
        return variantDTOList;
    }

    @Override
    public String determineCIAD(DetermineCAIDRequest determineCIADRequest){
        String url = null;
        String encodedValue = URIEncoder.encodeURIComponent(determineCIADRequest.getIdentifierValue());

        if(determineCIADRequest.getIdentifierType().equals(Constants.VAR_IDENTIFIER_RSID_dbSNP)){
            url = dbSNP_url+encodedValue;
        }else if(determineCIADRequest.getIdentifierType().equals(Constants.VAR_IDENTIFIER_HGVS)){
            url = hgvs_url+encodedValue;
        }else if(determineCIADRequest.getIdentifierType().equals(Constants.VAR_IDENTIFIER_ClinVar)){
            url = clinvar_url+encodedValue;
        }else if(determineCIADRequest.getIdentifierType().equals(Constants.VAR_IDENTIFIER_ClinVarRCV)){
            url = clinvarRCV_url+encodedValue;
        }else if(determineCIADRequest.getIdentifierType().equals(Constants.VAR_IDENTIFIER_GnomAD_2)) {
            url = gnomad_2_url + encodedValue;
        }else if(determineCIADRequest.getIdentifierType().equals(Constants.VAR_IDENTIFIER_GnomAD_3)) {
            url = gnomad_3_url + encodedValue;
        }else if(determineCIADRequest.getIdentifierType().equals(Constants.VAR_IDENTIFIER_GnomAD_4)){
            url = gnomad_4_url + encodedValue;
        }else if(determineCIADRequest.getIdentifierType().equals(Constants.VAR_IDENTIFIER_MyVarInfoHG38)){
            url = myvariantinfohg38_url+encodedValue;
        }

        if(url == null){
            return null;
        }

        HashMap<String,String> httpProperties = new HashMap<String,String>();
        httpProperties.put(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_APP_JSON);
        HTTPSConnector https = new HTTPSConnector();
        String response = https.sendHttpsRequest(url, Constants.HTTP_GET, null, httpProperties);

        if(response == null || response.equals("")){
            return null;
        }

        try {
            if(jsonParser == null){
                jsonParser = new JSONParser();
            }
            JSONObject jsonObj = null;
            Object obj = jsonParser.parse(response);
            if(obj instanceof JSONArray){
                JSONArray jsonArray = (JSONArray) obj;
                jsonObj = (JSONObject) jsonArray.get(0);
            }else{
                jsonObj = (JSONObject) obj;
            }

            if(jsonObj == null){
                return null;
            }

            String alleleRegistryLink = String.valueOf(jsonObj.get("@id"));
            String[] alleleRegistrylinkArray = alleleRegistryLink.split("/");
            String caid = alleleRegistrylinkArray[(alleleRegistrylinkArray.length)-1].trim();
            return caid;
        }catch(Exception e){
            logger.error(StackTracePrinter.printStackTrace(e));
        }
        return null;
    }

    @Override
    public ArrayList<String[]> getSummaryOfClassifiedVariants(){
        Integer userId = userService.getCurrentUserId();

        List<FinalCall> fcOrdered = finalCallRepository.getFinalCallsOrdered();
        int n = fcOrdered.size()+1;

        List<SummaryOfClassifiedVariantsJPA> socfJPA = variantInterpretationRepository.getSummaryOfClassifiedVariants(userId);
        if(socfJPA == null || socfJPA.size() == 0) {
            return null;
        }

        String[] tempArray = null;

        //create the table header
        ArrayList<String[]> table = new ArrayList<String[]>();
        tempArray = new String[n];
        for(FinalCall fc : fcOrdered){
            tempArray[fc.getId()] = fc.getTerm();
        }
        table.add(tempArray);

        //create the table rows
        for(SummaryOfClassifiedVariantsJPA obj: socfJPA){
            //fill each row with basic values, gene names and zeros
            tempArray = new String[n];
            tempArray[0] = obj.getGeneId();
            for(int i=1; i<n; i++){
                tempArray[i] = "0";
            }

            String[] finalCallIDs = (obj.getFinalcallIds()).split(",");
            //String[] caids = (obj.getCaids()).split(",");

            int m = finalCallIDs.length;
            for(int i =0; i<m; i++){
                int indx = Integer.parseInt(finalCallIDs[i]);
                int val = Integer.parseInt(tempArray[indx]);
                tempArray[indx] = (++val)+"";
            }
            table.add(tempArray);
        }
        return table;
    }
}


