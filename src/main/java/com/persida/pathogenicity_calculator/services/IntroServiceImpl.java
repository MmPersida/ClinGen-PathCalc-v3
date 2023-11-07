package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.config.AuthentificationManager;
import com.persida.pathogenicity_calculator.dto.VariantInterpretationDTO;
import com.persida.pathogenicity_calculator.repository.CustomUserDetails;
import com.persida.pathogenicity_calculator.repository.VariantInterpretationRepository;
import com.persida.pathogenicity_calculator.repository.entity.VariantInterpretation;
import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class IntroServiceImpl implements  IntroService{
    private static Logger logger = Logger.getLogger(IntroServiceImpl.class);

    @Autowired
    private AuthentificationManager authentificationManager;

    @Autowired
    private VariantInterpretationRepository variantInterpretationRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<String> getInterpretedVariantCAIDsLike(String partialCAID){
        CustomUserDetails cud = getCurrentUserCustomDetails();
        return variantInterpretationRepository.getInterpretedVariantCAIDsLike(cud.getUserId(), partialCAID);
    }

    public List<VariantInterpretationDTO> getRecentlyInterpretedVariants(){
        CustomUserDetails cud = getCurrentUserCustomDetails();

        List<VariantInterpretation>  variantInterpList = variantInterpretationRepository.getRecentlyInterpretedVariants(cud.getUserId());
        if(variantInterpList == null || variantInterpList.size() == 0){
            return null;
        }
        List<VariantInterpretationDTO> variantDTOList = new ArrayList<VariantInterpretationDTO>();
        for(VariantInterpretation varInterp :variantInterpList){
            VariantInterpretationDTO viTDO = new VariantInterpretationDTO();
            viTDO.setInterpretationId(varInterp.getId());
            viTDO.setCaid(varInterp.getVariant().getCaid());
            viTDO.setFinalCall(varInterp.getFinalCall().getTerm());
            viTDO.setCondition(varInterp.getCondition().getTerm());
            viTDO.setInheritance(varInterp.getInheritance().getTerm());
            variantDTOList.add(viTDO);
        }
        return variantDTOList;
    }

    private CustomUserDetails getCurrentUserCustomDetails(){
        Authentication authenticate  = authentificationManager.getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authenticate.getPrincipal();
        if(customUserDetails == null){
            logger.error("Unable to get current username!");
            return null;
        }
        return customUserDetails;
    }
}


