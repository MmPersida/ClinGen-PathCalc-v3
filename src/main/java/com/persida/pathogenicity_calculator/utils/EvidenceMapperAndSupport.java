package com.persida.pathogenicity_calculator.utils;

import com.persida.pathogenicity_calculator.dto.EvidenceSetDTO;
import com.persida.pathogenicity_calculator.repository.entity.Evidence;
import lombok.Data;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Data
public class EvidenceMapperAndSupport {

    private static Logger logger = Logger.getLogger(EvidenceMapperAndSupport.class);

    private HashMap<String, Evidence> evidenceMap = new HashMap<String, Evidence>();

    public void compareAndMapNewEvidences(Set<Evidence> currentEvidenceSet){
        if(currentEvidenceSet == null || currentEvidenceSet.size() == 0) {
            currentEvidenceSet = this.getEvidenceSet();
        }

        //evidence to update
        for(Evidence evd : currentEvidenceSet){
            Evidence newEvdToUpdate = evidenceMap.get(evd.getEvdType());
            if(newEvdToUpdate != null){
                evd.setEvdValue(newEvdToUpdate.getEvdValue());
                evd.setEvidenceSummary(newEvdToUpdate.getEvidenceSummary());
                evidenceMap.remove(newEvdToUpdate);
            }
        }

        //there are still new evidence left to add
        if(evidenceMap != null && evidenceMap.size() > 0){
            Iterator<String> key = this.evidenceMap.keySet().iterator();
            while(key.hasNext()){
                String keyName = key.next();
                Evidence newEvdToUpdate = this.evidenceMap.get(keyName);
                currentEvidenceSet.add(new Evidence(newEvdToUpdate.getEvdType(),
                                                    newEvdToUpdate.getEvdValue(),
                                                    newEvdToUpdate.getEvidenceSummary()));
            }
        }
    }

    public Set<Evidence> getEvidenceSet(){
        Set<Evidence> evidenceSet = null;
        if(this.evidenceMap == null || this.evidenceMap.size() == 0){
            return null;
        }
        evidenceSet = new HashSet<Evidence>();

        Iterator<String> key = this.evidenceMap.keySet().iterator();
        while(key.hasNext()){
            String keyName = key.next();
            evidenceSet.add(this.evidenceMap.get(keyName));
        }
        return evidenceSet;
    }

    public EvidenceSetDTO mapEvidenceSetToDTO(Set<Evidence> evidenceSet){
        if(evidenceSet == null || evidenceSet.size() == 0){
            logger.warn("Evidence Set in null or empty!");
        }

        EvidenceSetDTO evdSetDTO = new EvidenceSetDTO();
        for(Evidence evd : evidenceSet){
            if(evd.getEvdType().equals("bp1")){  evdSetDTO.setBp1(evd.getEvdValue());  }
            if(evd.getEvdType().equals("bp2")){  evdSetDTO.setBp2(evd.getEvdValue());  }
            if(evd.getEvdType().equals("bp3")){  evdSetDTO.setBp3(evd.getEvdValue());  }
            if(evd.getEvdType().equals("bp4")){  evdSetDTO.setBp4(evd.getEvdValue());  }
            if(evd.getEvdType().equals("bp5")){  evdSetDTO.setBp5(evd.getEvdValue());  }
            if(evd.getEvdType().equals("bp6")){  evdSetDTO.setBp6(evd.getEvdValue());  }
            if(evd.getEvdType().equals("bp7")){  evdSetDTO.setBp7(evd.getEvdValue());  }
            if(evd.getEvdType().equals("bs1")){  evdSetDTO.setBs1(evd.getEvdValue());  }
            if(evd.getEvdType().equals("bs2")){  evdSetDTO.setBs2(evd.getEvdValue());  }
            if(evd.getEvdType().equals("bs3")){  evdSetDTO.setBs3(evd.getEvdValue());  }
            if(evd.getEvdType().equals("bs4")){  evdSetDTO.setBs4(evd.getEvdValue());  }
            if(evd.getEvdType().equals("ba1")){  evdSetDTO.setBa1(evd.getEvdValue());  }
            if(evd.getEvdType().equals("pp1")){  evdSetDTO.setPp1(evd.getEvdValue());  }
            if(evd.getEvdType().equals("pp2")){  evdSetDTO.setPp2(evd.getEvdValue());  }
            if(evd.getEvdType().equals("pp3")){  evdSetDTO.setPp3(evd.getEvdValue());  }
            if(evd.getEvdType().equals("pp4")){  evdSetDTO.setPp4(evd.getEvdValue());  }
            if(evd.getEvdType().equals("pp5")){  evdSetDTO.setPp5(evd.getEvdValue());  }
            if(evd.getEvdType().equals("bp1")){  evdSetDTO.setPm1(evd.getEvdValue());  }
            if(evd.getEvdType().equals("pm2")){  evdSetDTO.setPm2(evd.getEvdValue());  }
            if(evd.getEvdType().equals("pm3")){  evdSetDTO.setPm3(evd.getEvdValue());  }
            if(evd.getEvdType().equals("pm4")){  evdSetDTO.setPm4(evd.getEvdValue());  }
            if(evd.getEvdType().equals("pm5")){  evdSetDTO.setPm5(evd.getEvdValue());  }
            if(evd.getEvdType().equals("pm6")){  evdSetDTO.setPm6(evd.getEvdValue());  }
            if(evd.getEvdType().equals("ps1")){  evdSetDTO.setPs1(evd.getEvdValue());  }
            if(evd.getEvdType().equals("ps2")){  evdSetDTO.setPs2(evd.getEvdValue());  }
            if(evd.getEvdType().equals("ps3")){  evdSetDTO.setPs3(evd.getEvdValue());  }
            if(evd.getEvdType().equals("ps4")){  evdSetDTO.setPs4(evd.getEvdValue());  }
            if(evd.getEvdType().equals("pvs1")){  evdSetDTO.setPvs1(evd.getEvdValue());  }
        }
        return evdSetDTO;
    }


    public HashMap<String, Evidence>  mapEvidenceDTOtoEvdSet(EvidenceSetDTO esDTO){
        if(esDTO.getBp1() != null){  this.evidenceMap.put("bp1", new Evidence("bp1" ,esDTO.getBp1()));  }
        if(esDTO.getBp2() != null){  this.evidenceMap.put("bp2", new Evidence("bp2" ,esDTO.getBp2()));  }
        if(esDTO.getBp3() != null){  this.evidenceMap.put("bp3", new Evidence("bp3" ,esDTO.getBp3()));  }
        if(esDTO.getBp4() != null){  this.evidenceMap.put("bp4", new Evidence("bp4" ,esDTO.getBp4()));  }
        if(esDTO.getBp5() != null){  this.evidenceMap.put("bp5", new Evidence("bp5" ,esDTO.getBp5()));  }
        if(esDTO.getBp6() != null){  this.evidenceMap.put("bp6", new Evidence("bp6" ,esDTO.getBp6()));  }
        if(esDTO.getBp7() != null){  this.evidenceMap.put("bp7", new Evidence("bp7" ,esDTO.getBp7()));  }
        if(esDTO.getBs1() != null){  this.evidenceMap.put("bs1", new Evidence("bs1" ,esDTO.getBs1()));  }
        if(esDTO.getBs2() != null){  this.evidenceMap.put("bs2", new Evidence("bs2" ,esDTO.getBs2()));  }
        if(esDTO.getBs3() != null){  this.evidenceMap.put("bs3", new Evidence("bs3" ,esDTO.getBs3()));  }
        if(esDTO.getBs4() != null){  this.evidenceMap.put("bs4", new Evidence("bs4" ,esDTO.getBs4()));  }
        if(esDTO.getBa1() != null){  this.evidenceMap.put("ba1", new Evidence("ba1" ,esDTO.getBa1()));  }
        if(esDTO.getPp1() != null){  this.evidenceMap.put("pp1", new Evidence("pp1" ,esDTO.getPp1()));  }
        if(esDTO.getPp2() != null){  this.evidenceMap.put("pp2", new Evidence("pp2" ,esDTO.getPp2()));  }
        if(esDTO.getPp3() != null){  this.evidenceMap.put("pp3", new Evidence("pp3" ,esDTO.getPp3()));  }
        if(esDTO.getPp4() != null){  this.evidenceMap.put("pp4", new Evidence("pp4" ,esDTO.getPp4()));  }
        if(esDTO.getPp5() != null){  this.evidenceMap.put("pp5", new Evidence("pp5" ,esDTO.getPp5()));  }
        if(esDTO.getPm1() != null){  this.evidenceMap.put("bp1", new Evidence("pm1" ,esDTO.getPm1()));  }
        if(esDTO.getPm2() != null){  this.evidenceMap.put("pm2", new Evidence("pm2" ,esDTO.getPm2()));  }
        if(esDTO.getPm3() != null){  this.evidenceMap.put("pm3", new Evidence("pm3" ,esDTO.getPm3()));  }
        if(esDTO.getPm4() != null){  this.evidenceMap.put("pm4", new Evidence("pm4" ,esDTO.getPm4()));  }
        if(esDTO.getPm5() != null){  this.evidenceMap.put("pm5", new Evidence("pm5" ,esDTO.getPm5()));  }
        if(esDTO.getPm6() != null){  this.evidenceMap.put("pm6", new Evidence("pm6" ,esDTO.getPm6()));  }
        if(esDTO.getPs1() != null){  this.evidenceMap.put("ps1", new Evidence("ps1" ,esDTO.getPs1()));  }
        if(esDTO.getPs2() != null){  this.evidenceMap.put("ps2", new Evidence("ps2" ,esDTO.getPs2()));  }
        if(esDTO.getPs3() != null){  this.evidenceMap.put("ps3", new Evidence("ps3" ,esDTO.getPs3()));  }
        if(esDTO.getPs4() != null){  this.evidenceMap.put("ps4", new Evidence("ps4" ,esDTO.getPs4()));  }
        if(esDTO.getPvs1() != null){  this.evidenceMap.put("pvs1", new Evidence("pvs1" ,esDTO.getPvs1()));  }
        return this.evidenceMap;
    }
}
