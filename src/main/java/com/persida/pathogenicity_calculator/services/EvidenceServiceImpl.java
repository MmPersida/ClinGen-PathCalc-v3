package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.repository.EvidenceRepository;
import com.persida.pathogenicity_calculator.repository.entity.Evidence;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class EvidenceServiceImpl implements EvidenceService{

    private static Logger logger = Logger.getLogger(EvidenceServiceImpl.class);

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Override
    public void saveEvidenceSet(Set<Evidence> evidenceSet){
        if(evidenceSet == null || evidenceSet.size() == 0){
            return;
        }
        for(Evidence e : evidenceSet){
            evidenceRepository.save(e);
        }
    }
}
