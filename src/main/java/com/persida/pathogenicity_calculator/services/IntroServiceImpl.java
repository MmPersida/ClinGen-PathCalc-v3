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
    private UserService userService;
    @Autowired
    private VariantInterpretationRepository variantInterpretationRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<String> getInterpretedVariantCAIDsLike(String partialCAID){
        Integer userId = userService.getCurrentUserId();
        return variantInterpretationRepository.getInterpretedVariantCAIDsLike(userId, partialCAID);
    }

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
            viTDO.setFinalCall(varInterp.getFinalCall().getTerm());
            viTDO.setCondition(varInterp.getCondition().getTerm());
            viTDO.setInheritance(varInterp.getInheritance().getTerm());
            variantDTOList.add(viTDO);
        }
        return variantDTOList;
    }
}


