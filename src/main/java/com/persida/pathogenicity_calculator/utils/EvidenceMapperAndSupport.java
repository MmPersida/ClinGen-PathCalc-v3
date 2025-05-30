package com.persida.pathogenicity_calculator.utils;

import com.persida.pathogenicity_calculator.dto.EvidenceDTO;
import com.persida.pathogenicity_calculator.dto.EvidenceLinkDTO;
import com.persida.pathogenicity_calculator.model.openAPI.EvidenceR;
import com.persida.pathogenicity_calculator.repository.entity.Evidence;
import com.persida.pathogenicity_calculator.repository.entity.EvidenceLink;
import com.persida.pathogenicity_calculator.repository.entity.VariantInterpretation;
import com.persida.pathogenicity_calculator.utils.constants.Constants;
import lombok.Data;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@Data
public class EvidenceMapperAndSupport {

    private static Logger logger = Logger.getLogger(EvidenceMapperAndSupport.class);

    private static HashMap<String,TagGroup> tagToGroupMap = new HashMap<String,TagGroup>();

    @Data
    private class TagGroup{
        private String type;
        private String modifier;
        public TagGroup(String type, String modifier){
            this.type = type;
            this.modifier = modifier;
        }
    }

    @PostConstruct
    public void prepareEvidenceTagMap(){
        tagToGroupMap.put("BP1", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("BP2", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("BP3", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("BP4", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("BP5", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("BP6", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("BP7", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("BS1", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_STRONG));
        tagToGroupMap.put("BS2", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_STRONG));
        tagToGroupMap.put("BS3", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_STRONG));
        tagToGroupMap.put("BS4", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_STRONG));
        tagToGroupMap.put("BA1", new TagGroup(Constants.TYPE_BENIGN,Constants.MODIFIER_STAND_ALONE));
        tagToGroupMap.put("PP1", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("PP2", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("PP3", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("PP4", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("PP5", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_SUPPORTING));
        tagToGroupMap.put("PM1", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_MODERATE));
        tagToGroupMap.put("PM2", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_MODERATE));
        tagToGroupMap.put("PM3", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_MODERATE));
        tagToGroupMap.put("PM4", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_MODERATE));
        tagToGroupMap.put("PM5", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_MODERATE));
        tagToGroupMap.put("PM6", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_MODERATE));
        tagToGroupMap.put("PS1", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_STRONG));
        tagToGroupMap.put("PS2", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_STRONG));
        tagToGroupMap.put("PS3", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_STRONG));
        tagToGroupMap.put("PS4", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_STRONG));
        tagToGroupMap.put("PVS1", new TagGroup(Constants.TYPE_PATHOGENIC,Constants.MODIFIER_VERY_STRONG));
    }

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

            List<EvidenceLinkDTO> evidenceLinks = null;
            if(evd.getEvidenceLinks() != null && evd.getEvidenceLinks().size() > 0){
                evidenceLinks = new ArrayList<EvidenceLinkDTO>();
                Set<EvidenceLink> evdLinkSet = evd.getEvidenceLinks();
                for(EvidenceLink evdL : evdLinkSet){
                    evidenceLinks.add(new EvidenceLinkDTO(evdL.getId(), evdL.getEvdLink(), evdL.getLinkCode(), evdL.getComment()));
                }
            }

            evdDTOList.add(new EvidenceDTO(evd.getId(), evd.getEvdType(), evd.getEvdModifier(), fullEvidenceLabel, summary, evidenceLinks));
        }
        return evdDTOList;
    }

    public List<EvidenceR> mapEvidenceSetToEvidenceRList(Set<Evidence> evidenceSet) {
        if (evidenceSet == null || evidenceSet.size() == 0) {
            logger.warn("Evidence Set in null or empty!");
        }

        EvidenceR evdR = null;
        List<EvidenceR> evdRList = new ArrayList<EvidenceR>();
        for (Evidence evd : evidenceSet) {
            String summary = null;
            if (evd.getEvidenceSummary() != null && evd.getEvidenceSummary().getSummary() != null &&
                    !evd.getEvidenceSummary().getSummary().equals("")) {
                summary = evd.getEvidenceSummary().getSummary();
            }

            String fullEvidenceLabel = evd.getEvdType();
            if (evd.getEvdModifier() != null && !evd.getEvdModifier().equals("")) {
                fullEvidenceLabel += " - " + evd.getEvdModifier();
            }

            evdRList.add(new EvidenceR(evd.getId(), evd.getEvdType(), evd.getEvdModifier(), fullEvidenceLabel, summary));
        }
        return evdRList;
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

    public Map<String,Integer> formatEvdDTOListToCSpecEvdMap(List<EvidenceDTO> evidenceDTOList){
        HashMap<String,Integer> evidenceMap = new HashMap<String,Integer>();

        for(EvidenceDTO evdDTO: evidenceDTOList){
            String tagCategory = null;

            TagGroup tg = tagToGroupMap.get(evdDTO.getType());
            if(tg == null){
                logger.error("Unknown evidence DTO type/tag "+evdDTO.getType()+" or evidence group map is empty!");
                continue;
            }
            tagCategory = tg.type;

            if(evdDTO.getModifier() != null && !evdDTO.getModifier().isEmpty()){
                tagCategory = tagCategory +"."+evdDTO.getModifier();
            }else{
                tagCategory = tagCategory +"."+tg.getModifier();
            }

            if(evidenceMap.get(tagCategory) == null){
                evidenceMap.put(tagCategory, 1);
            }else{
                Integer n = evidenceMap.get(tagCategory);
                evidenceMap.put(tagCategory, (n+1));
            }
        }
        return evidenceMap;
    }

    public Map<String,Integer> formatEvdSetToCSpecEvdMap(Set<Evidence> evidences){
        HashMap<String,Integer> evidenceMap = new HashMap<String,Integer>();

        for(Evidence evd: evidences){
            String tagCategory = null;

            TagGroup tg = tagToGroupMap.get(evd.getEvdType());
            if(tg == null){
                logger.error("Unknown evidence type/tag "+evd.getEvdType()+" or evidence group map is empty!");
                continue;
            }
            tagCategory = tg.type;

            if(evd.getEvdModifier() != null && !evd.getEvdModifier().isEmpty()){
                tagCategory = tagCategory +"."+evd.getEvdModifier();
            }else{
                tagCategory = tagCategory +"."+tg.getModifier();
            }

            if(evidenceMap.get(tagCategory) == null){
                evidenceMap.put(tagCategory, 1);
            }else{
                Integer n = evidenceMap.get(tagCategory);
                evidenceMap.put(tagCategory, (n+1));
            }
        }
        return evidenceMap;
    }
}
