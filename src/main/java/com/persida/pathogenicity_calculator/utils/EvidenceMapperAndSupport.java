package com.persida.pathogenicity_calculator.utils;

import com.persida.pathogenicity_calculator.dto.EvidenceDTO;
import com.persida.pathogenicity_calculator.repository.entity.Evidence;
import com.persida.pathogenicity_calculator.repository.entity.VariantInterpretation;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
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

    public List<String> mapEvidenceSetToDTO(Set<Evidence> evidenceSet){
        if(evidenceSet == null || evidenceSet.size() == 0){
            logger.warn("Evidence Set in null or empty!");
        }

        EvidenceDTO eDTO= null;
        List<String> evdStrList = new ArrayList<String>();
        for(Evidence evd : evidenceSet){
            String formatedValue = reformatEvidenceValueForFontEnd(evd.getEvdValue());

            String evidenceFullName = null;
            if(formatedValue == null || formatedValue.equals("")){
                evidenceFullName = evd.getEvdType().toUpperCase();
            }else{
                evidenceFullName = evd.getEvdType().toUpperCase()+" - "+formatedValue;
            }
            evdStrList.add(evidenceFullName);
        }
        return evdStrList;
    }

    public HashMap<String, Evidence> mapEvidenceDTOListToEvdMap(List<EvidenceDTO> evidenceDTOList){
        HashMap<String, Evidence> evidenceMap = new HashMap<String, Evidence>();
        if(evidenceDTOList == null || evidenceDTOList.size() == 0){
            logger.warn("New evidenceList in empty or null!");
            return evidenceMap;
        }

        for(EvidenceDTO eDTO : evidenceDTOList){
            String evdNameLower = eDTO.getName().toLowerCase();
            evidenceMap.put(evdNameLower, new Evidence(evdNameLower, eDTO.getModifier()));
        }
        return evidenceMap;
    }

    private String reformatEvidenceValueForFontEnd(Character evidenceValue){
        String evidenceModifier =  null;
        switch(evidenceValue){
            case '1': evidenceModifier = ""; break;
            case 'P': evidenceModifier = Constants.MODIFIER_SUPPORTING; break;
            case 'M': evidenceModifier = Constants.MODIFIER_MODERATE; break;
            case 'S': evidenceModifier = Constants.MODIFIER_STRONG; break;
            case 'V': evidenceModifier = Constants.MODIFIER_VERY_STRONG; break;
            default: evidenceModifier = null; logger.error("Unable to map (reformat) evidence value: "+evidenceValue);  break;
        }
        return evidenceModifier;
    }

    private EvidenceDTO reformatEvidenceToDTO(String evidenceValue){
        EvidenceDTO evdDTO =  null;

        String[] evidValArray = evidenceValue.split("\\-");
        String evidName = evidValArray[0].trim().toLowerCase();
        Character evidModifier = '0';

        if(evidValArray.length == 1){
            evidModifier = Constants.MODIFIER_1;
        }else if(evidValArray.length == 2){
            switch(evidValArray[1].trim()){
                case "Supporting":  evidModifier = Constants.MODIFIER_P; break;
                case "Moderate":    evidModifier = Constants.MODIFIER_M; break;
                case "Strong":      evidModifier = Constants.MODIFIER_S; break;
                case "Very Strong": evidModifier = Constants.MODIFIER_V; break;
                default: logger.error("Unable to map (reformat) modifier value: "+evidValArray[1].trim());  break;
            }
        }
        return new EvidenceDTO(evidName, evidModifier);
    }
}
