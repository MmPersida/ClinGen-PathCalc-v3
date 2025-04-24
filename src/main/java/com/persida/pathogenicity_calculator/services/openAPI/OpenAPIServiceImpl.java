package com.persida.pathogenicity_calculator.services.openAPI;

import com.persida.pathogenicity_calculator.config.JWTutils;
import com.persida.pathogenicity_calculator.dto.VIBasicDTO;
import com.persida.pathogenicity_calculator.model.JWTHeaderAndPayloadData;
import com.persida.pathogenicity_calculator.model.openAPI.Classification;
import com.persida.pathogenicity_calculator.model.openAPI.ClassificationResponse;
import com.persida.pathogenicity_calculator.model.openAPI.ClassificationsResponse;
import com.persida.pathogenicity_calculator.model.openAPI.requestModels.*;
import com.persida.pathogenicity_calculator.repository.UserRepository;
import com.persida.pathogenicity_calculator.repository.entity.Gene;
import com.persida.pathogenicity_calculator.repository.entity.User;
import com.persida.pathogenicity_calculator.repository.entity.VariantInterpretation;
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
    public ClassificationsResponse allClassificationsForUser(String username){
        User user = userRepository.getUserByUsername(username);
        if(user == null){
            return new ClassificationsResponse("Unable to determine user!", Constants.NAME_INVALID);
        }

        List<VIBasicDTO> viBasicDtoList = variantInterpretationService.getAllInterpretedVariantsByUser(user.getId());
        if(viBasicDtoList == null || viBasicDtoList.isEmpty()){
            return new ClassificationsResponse("No classifications can be found for this user!", Constants.NAME_NOT_FOUND);
        }
        return convertFromViDTOtoCLassReponse(viBasicDtoList, null, user);
    }

    @Override
    public ClassificationsResponse classificationsForVariant(ClassByVariantRequest classRequest, String username){
        String caid = classRequest.getCaid();
        User user = userRepository.getUserByUsername(username);
        if(user == null){
            return new ClassificationsResponse("Unable to determine user!", Constants.NAME_INVALID);
        }

        List<VIBasicDTO> viBasicDtoList = variantInterpretationService.getUserVIBasicDataForCaid(user.getId(), caid);
        if(viBasicDtoList == null || viBasicDtoList.isEmpty()){
            return new ClassificationsResponse("No classifications can be found using the provided CAID: "+caid, Constants.NAME_NOT_FOUND);
        }
        return convertFromViDTOtoCLassReponse(viBasicDtoList, caid, user);
    }

    @Override
    public ClassificationResponse classificationById(ClassByIdRequest classByIdReq, String username){
        Integer classId = classByIdReq.getClassId();
        User user = userRepository.getUserByUsername(username);
        if(user == null){
            return new ClassificationResponse("Unable to determine user!", Constants.NAME_INVALID);
        }

        VariantInterpretation vi = variantInterpretationService.getInterpretationById(classId);
        if(vi == null){
            return new ClassificationResponse("No classification can be found using the provided ID: "+classId, Constants.NAME_NOT_FOUND);
        }

        String dfcValue = null;
        if (vi.getDeterminedFinalCall() != null) {
            dfcValue = vi.getDeterminedFinalCall().getTerm();
        }

        String rgName = null;
        Gene g = vi.getVariant().getGene();
        if (g != null) {
            rgName = g.getGeneId();
        }

        ClassificationEntContent cec = new ClassificationEntContent(rgName, vi.getCondition().getTerm(),
                vi.getInheritance().getTerm(), vi.getFinalCall().getTerm(), dfcValue);

        Classification c = new Classification(cec, classId,
                DateUtils.dateToStringParser(vi.getCreatedOn()),
                DateUtils.dateToStringParser(vi.getModifiedOn()), username);
        ClassificationResponse cr = new ClassificationResponse(c);
        return cr;
    }

    private ClassificationsResponse convertFromViDTOtoCLassReponse(List<VIBasicDTO> viBasicDtoList, String caid, User user){
        ClassificationsResponse cr = new ClassificationsResponse();
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
