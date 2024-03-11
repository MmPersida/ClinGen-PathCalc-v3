package com.persida.pathogenicity_calculator.utils;

import com.persida.pathogenicity_calculator.dto.EvidenceDTO;
import com.persida.pathogenicity_calculator.dto.EvidenceLinkDTO;
import com.persida.pathogenicity_calculator.repository.entity.Evidence;
import com.persida.pathogenicity_calculator.repository.entity.EvidenceLink;
import com.persida.pathogenicity_calculator.repository.entity.VariantInterpretation;
import lombok.Data;
import org.apache.log4j.Logger;

import java.util.*;

@Data
public class EvidenceMapperAndSupport {

    private static Logger logger = Logger.getLogger(EvidenceMapperAndSupport.class);

    public void compareAndMapNewEvidences(VariantInterpretation vi, HashMap<String, Evidence> newEvidenceMap){
        if(vi.getEvidences() == null && newEvidenceMap != null) {
            //Just In Case! initialize for variants with no prior evidences
            vi.setEvidences(new HashSet<Evidence>());
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
                        if(newEvdToUpdate.getEvdModifier() != null){
                            evd.setEvdModifier(newEvdToUpdate.getEvdModifier());
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
                            newEvdToUpdate.getEvdModifier(), newEvdToUpdate.getEvidenceSummary(),vi));
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

        EvidenceDTO eDTO = null;
        List<EvidenceDTO> evdDTOList = new ArrayList<EvidenceDTO>();
        for(Evidence evd : evidenceSet){
            String summary = null;
            if( evd.getEvidenceSummary() != null && evd.getEvidenceSummary().getSummary() != null &&
                    !evd.getEvidenceSummary().getSummary().equals("")){
                summary = evd.getEvidenceSummary().getSummary();
            }

            String fullEvidenceLabel = evd.getEvdType();
            if(evd.getEvdModifier() != null && !evd.getEvdModifier().equals("")){
                fullEvidenceLabel += " - "+evd.getEvdModifier();
            }
            evdDTOList.add(new EvidenceDTO(evd.getId(), evd.getEvdType(), evd.getEvdModifier(), fullEvidenceLabel, summary));
        }
        return evdDTOList;
    }

    public HashMap<String, Evidence> mapEvidenceDTOListToEvdMap(List<EvidenceDTO> evidenceDTOList){
        HashMap<String, Evidence> evidenceMap = new HashMap<String, Evidence>();
        if(evidenceDTOList == null || evidenceDTOList.size() == 0){
            logger.warn("New evidenceList in empty or null!");
            return evidenceMap;
        }

        for(EvidenceDTO eDTO : evidenceDTOList){
            evidenceMap.put(eDTO.getType(), new Evidence(eDTO.getType(), eDTO.getModifier(), eDTO.getSummary()));
        }
        return evidenceMap;
    }
}
