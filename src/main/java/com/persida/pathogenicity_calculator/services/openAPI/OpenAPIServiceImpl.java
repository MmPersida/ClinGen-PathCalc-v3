package com.persida.pathogenicity_calculator.services.openAPI;

import com.persida.pathogenicity_calculator.config.JWTutils;
import com.persida.pathogenicity_calculator.dto.VIBasicDTO;
import com.persida.pathogenicity_calculator.model.JWTHeaderAndPayloadData;
import com.persida.pathogenicity_calculator.model.openAPI.Classification;
import com.persida.pathogenicity_calculator.model.openAPI.ClassificationsResponse;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.ClassRequest;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.ClassificationEntContent;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.RequestAuthData;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.TokenResponse;
import com.persida.pathogenicity_calculator.repository.UserRepository;
import com.persida.pathogenicity_calculator.repository.entity.User;
import com.persida.pathogenicity_calculator.services.CalculatorServiceImpl;
import com.persida.pathogenicity_calculator.services.VariantInterpretationService;
import com.persida.pathogenicity_calculator.utils.DateUtils;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OpenAPIServiceImpl implements OpenAPIService{
    static Logger logger = Logger.getLogger(CalculatorServiceImpl.class);

    @Autowired
    private JWTutils jwtUtils;
    @Autowired
    private VariantInterpretationService variantInterpretationService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public TokenResponse tokenRequest(RequestAuthData requestAuthData){
        String tokenValue = jwtUtils.getTokenFromAuth(requestAuthData.getUsername(), requestAuthData.getPass());

        if(tokenValue == null || tokenValue.equals("")){
            return new TokenResponse("Invalid credentials!");
        }

        JWTHeaderAndPayloadData jwtData = jwtUtils.decodeAndValidateToken(tokenValue);
        if(jwtData == null){
            logger.error("Unable to validate token for user : "+requestAuthData.getUsername()+"!");
            return new TokenResponse("Unable to validate token for user : "+requestAuthData.getUsername()+"!");
        }

        if(jwtData.getUsername().equals(requestAuthData.getUsername())){
            return new TokenResponse(tokenValue, jwtData.getTokenExpTime());
        }else{
            return null;
        }
    }

    @Override
    public ClassificationsResponse classifications(ClassRequest classRequest, String username){
        String caid = classRequest.getCaid();
        User user = userRepository.getUserByUsername(username);
        if(user == null){
            return new ClassificationsResponse("Unable to determine user!", Constants.NAME_INVALID);
        }

        ClassificationsResponse cr = new ClassificationsResponse();

        List<VIBasicDTO> viBasicDtoList = variantInterpretationService.getUserVIBasicDataForCaid(user.getId(), caid);
        if(viBasicDtoList == null || viBasicDtoList.isEmpty()){
            return new ClassificationsResponse("No classification can be using the CAID: "+caid, Constants.NAME_NA);
        }

        cr.getData().setVariant(caid);
        for(VIBasicDTO viDTO : viBasicDtoList){
            String dfcValue = null;
            if (viDTO.getDeterminedFinalCall() != null) {
                dfcValue = viDTO.getDeterminedFinalCall().getTerm();
            }

            String rgName = null;
            if (viDTO.getRelatedGene() != null) {
                rgName = viDTO.getRelatedGene().getGeneName();
            }

            ClassificationEntContent cec = new ClassificationEntContent(rgName, viDTO.getCondition(), viDTO.getInheritance(),
                                                        viDTO.getCalculatedFinalCall().getTerm(), dfcValue);
            Classification c = new Classification(cec, viDTO.getInterpretationId(),
                                            DateUtils.dateToStringParser(viDTO.getCreateOn()),
                                            DateUtils.dateToStringParser(viDTO.getModifiedOn()), user.getUsername());
            cr.getData().addClassification(c);
        }
        return cr;
    }
}
