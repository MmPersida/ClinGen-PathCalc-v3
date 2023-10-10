package com.persida.pathogenicity_calculator.services;
import com.persida.pathogenicity_calculator.dto.ConditionsTermAndIdDTO;
import com.persida.pathogenicity_calculator.repository.ConditionRepository;
import com.persida.pathogenicity_calculator.repository.jpa.ConditionTermIdJPA;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConditionsServiceImpl implements ConditionsService{

    static Logger logger = Logger.getLogger(ConditionsServiceImpl.class);

    @Autowired
    private ConditionRepository conditionRepository;

    public List<ConditionsTermAndIdDTO> getConditionsLike(String partialCAID){
        List<ConditionsTermAndIdDTO> conditionsTermAndIdDTO = new ArrayList<ConditionsTermAndIdDTO>();
        List<ConditionTermIdJPA> conJpaList =  conditionRepository.getConditionTermsLike(partialCAID);
        if(conJpaList == null || conJpaList.size() == 0){
            return null;
        }
        for(ConditionTermIdJPA cJPA : conJpaList){
            conditionsTermAndIdDTO.add(new ConditionsTermAndIdDTO(cJPA.getConditionId(), cJPA.getTerm()));
        }
        return conditionsTermAndIdDTO;
    }
}
