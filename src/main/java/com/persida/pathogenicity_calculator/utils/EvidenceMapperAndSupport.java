package com.persida.pathogenicity_calculator.utils;

import com.persida.pathogenicity_calculator.dto.EvidenceDTO;
import com.persida.pathogenicity_calculator.dto.EvidenceSetDTO;
import com.persida.pathogenicity_calculator.repository.entity.Evidence;
import com.persida.pathogenicity_calculator.repository.entity.VariantInterpretation;
import lombok.Data;
import org.apache.log4j.Logger;

import java.util.*;

@Data
public class EvidenceMapperAndSupport {

    private static Logger logger = Logger.getLogger(EvidenceMapperAndSupport.class);

    public void compareAndMapNewEvidences(VariantInterpretation vi, HashMap<String, Evidence> newEvidenceMap){
        if(vi.getEvidences() == null || vi.getEvidences().size() == 0) {
            logger.warn("currentEvidenceSet is empty!");
            return;
        }

        //there are still new evidence left to add
        if(newEvidenceMap != null && newEvidenceMap.size() > 0){
            Iterator<String> key = newEvidenceMap.keySet().iterator();
            while(key.hasNext()){
                String keyName = key.next();
                Evidence newEvdToUpdate = newEvidenceMap.get(keyName);
                boolean thisIsANewEvidence = true;

                inner:
                for(Evidence evd : vi.getEvidences()){
                    if(evd.getEvdType().equals(keyName)){
                        //evidence to update
                        thisIsANewEvidence = false;
                        if(newEvdToUpdate.getEvdValue() != null){
                            evd.setEvdValue(newEvdToUpdate.getEvdValue());
                        }
                        if(newEvdToUpdate.getEvidenceSummary() != null){
                            evd.setEvidenceSummary(newEvdToUpdate.getEvidenceSummary());
                        }
                        break inner;
                    }
                }

                if(thisIsANewEvidence){
                    //new evidence to add
                    vi.getEvidences().add(new Evidence(newEvdToUpdate.getEvdType(),
                            newEvdToUpdate.getEvdValue(),
                            newEvdToUpdate.getEvidenceSummary(),vi));
                }
            }
        }
    }

    public Set<Evidence> getEvidenceSet(HashMap<String, Evidence> evidenceMap){
        Set<Evidence> evidenceSet = null;
        if(evidenceMap == null || evidenceMap.size() == 0){
            return null;
        }
        evidenceSet = new HashSet<Evidence>();

        Iterator<String> key = evidenceMap.keySet().iterator();
        while(key.hasNext()){
            String keyName = key.next();
            evidenceSet.add(evidenceMap.get(keyName));
        }
        return evidenceSet;
    }

    public List<EvidenceDTO> mapEvidenceSetToDTO(Set<Evidence> evidenceSet){
        if(evidenceSet == null || evidenceSet.size() == 0){
            logger.warn("Evidence Set in null or empty!");
        }

        List<EvidenceDTO> evdDTOList = new ArrayList<EvidenceDTO>();
        for(Evidence evd : evidenceSet){
            evdDTOList.add(new EvidenceDTO(evd.getEvdType().toUpperCase(), evd.getEvdValue()));
        }
        return evdDTOList;
    }

    public HashMap<String, Evidence> mapEvidenceDTOListToEvdMap(List<EvidenceDTO> evidenceList){
        HashMap<String, Evidence> evidenceMap = new HashMap<String, Evidence>();
        if(evidenceList == null || evidenceList.size() == 0){
            logger.warn("New evidenceList in empty or null!");
            return evidenceMap;
        }

        for(EvidenceDTO eDTO : evidenceList){
            String evdName = eDTO.getName().toLowerCase();
            evidenceMap.put(evdName, new Evidence(evdName ,eDTO.getModifier()));
        }
        return evidenceMap;
    }
}
