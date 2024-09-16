package com.persida.pathogenicity_calculator.scheduled_tasks;

import com.persida.pathogenicity_calculator.dto.CSpecEngineDTO;
import com.persida.pathogenicity_calculator.dto.ConditionsTermAndIdDTO;
import com.persida.pathogenicity_calculator.dto.EngineRelatedGeneDTO;
import com.persida.pathogenicity_calculator.repository.CSpecRuleSetRepository;
import com.persida.pathogenicity_calculator.repository.ConditionRepository;
import com.persida.pathogenicity_calculator.repository.GeneRepository;
import com.persida.pathogenicity_calculator.repository.entity.CSpecRuleSet;
import com.persida.pathogenicity_calculator.repository.entity.Condition;
import com.persida.pathogenicity_calculator.repository.entity.Gene;
import com.persida.pathogenicity_calculator.services.CSpecEngineService;
import com.persida.pathogenicity_calculator.services.ConditionsService;
import com.persida.pathogenicity_calculator.services.GenesService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataLoadingTasksImpl implements DataLoadingTasks {
    private static Logger logger = Logger.getLogger(DataLoadingTasksImpl.class);

    @Autowired
    private CSpecEngineService cSpecEngineService;
    @Autowired
    private ConditionsService conditionsService;
    @Autowired
    private CSpecRuleSetRepository cspecRuleSetRepository;
    @Autowired
    private GenesService genesService;
    @Autowired
    private GeneRepository geneRepository;
    @Autowired
    private ConditionRepository conditionRepository;

    @Override
    public void loadAndCompareDiseaseInfo(){
        ArrayList<ConditionsTermAndIdDTO> response = conditionsService.getConditionsInfoByCall();
        if(response == null || response.size() == 0){
            logger.warn("Received no data from API for Conditions (Diseases)!");
            return;
        }

        Map<String, Condition> tempCondMap = null;
        List<Condition> allConditions = conditionRepository.findAll();
        if(allConditions != null && allConditions.size() > 0){
            tempCondMap = new HashMap<String, Condition>();
            for(Condition c : allConditions){
                tempCondMap.put(c.getCondition_id(), c);
            }
        }else{
            logger.info("No conditions in the DB at the moment!");
        }

        int newAddedC = 0;
        Condition tempCond = null;
        for(ConditionsTermAndIdDTO condDTO : response){
            if(tempCondMap != null && tempCondMap.size() > 0){
                tempCond = tempCondMap.get(condDTO.getConditionId());
                if(tempCond != null){
                    tempCondMap.remove(condDTO.getConditionId());
                    continue;
                }
            }
            conditionRepository.save(new Condition(condDTO.getConditionId(), condDTO.getTerm()));
            newAddedC++;
        }
        logger.info("Added total of "+newAddedC+" new conditions!");
        tempCondMap = null;
    }

    @Override
    public void loadAndCompareVCPEsInfo(){
        ArrayList<CSpecEngineDTO> response = cSpecEngineService.getVCEPsDataByCall();
        if(response == null || response.size() == 0){
            logger.warn("Received no data from API for CSpecEgine's Info!");
            return;
        }

        CSpecRuleSet cspecRuleSet = null;
        int addedNew = 0;
        int updatedNum = 0;
        int enabledNum = 0;
        for(CSpecEngineDTO engineDTO : response){
            Optional<CSpecRuleSet> optCSpecRuleSet = cspecRuleSetRepository.findById(engineDTO.getEngineId());

            if(engineDTO.getEnabled()){
                enabledNum++;
            }

            if(optCSpecRuleSet != null && optCSpecRuleSet.isPresent()){
                //engine with this ID already exists in the DB
                cspecRuleSet = optCSpecRuleSet.get();

                boolean updated = false;
                if(!engineDTO.getEngineSummary().equals(cspecRuleSet.getEngineSummary())){
                    cspecRuleSet.setEngineSummary(engineDTO.getEngineSummary());
                    updated = true;
                }
                if(!engineDTO.getOrganizationName().equals(cspecRuleSet.getOrganizationName())){
                    cspecRuleSet.setOrganizationName(engineDTO.getOrganizationName());
                    updated = true;
                }
                if(!engineDTO.getOrganizationLink().equals(cspecRuleSet.getOrganizationLink())){
                    cspecRuleSet.setOrganizationLink(engineDTO.getOrganizationLink());
                    updated = true;
                }
                if(engineDTO.getRuleSetId() != cspecRuleSet.getRuleSetId()){
                    cspecRuleSet.setRuleSetId(engineDTO.getRuleSetId());
                    updated = true;
                }
                if(!engineDTO.getRuleSetURL().equals(cspecRuleSet.getRuleSetURL())){
                    cspecRuleSet.setRuleSetURL(engineDTO.getRuleSetURL());
                    updated = true;
                }
                if(engineDTO.getGenes() != null && engineDTO.getGenes().size() > 0){
                    Set<Gene> genesSet = new HashSet<Gene>();
                    Gene g = null;
                    Set<EngineRelatedGeneDTO> genesList = engineDTO.getGenes();
                    for(EngineRelatedGeneDTO erGene : genesList){
                        Set<Condition> condSet = new HashSet<Condition>();
                        ArrayList<ConditionsTermAndIdDTO> condDTOList = erGene.getConditions();
                        if(condDTOList != null && condDTOList.size() > 0){
                            for(ConditionsTermAndIdDTO condDTO : condDTOList){
                                condSet.add(new Condition(condDTO.getConditionId(), condDTO.getTerm()));
                            }
                        }
                        String[] hgncAndNcbiIds = genesService.getGeneHGNCandNCBIids(erGene.getGeneName());
                        g = new Gene(erGene.getGeneName(), hgncAndNcbiIds[0], hgncAndNcbiIds[1], condSet);
                        genesService.compareAndUpdateGene(g);
                        genesSet.add(g);
                    }
                    if(genesSet.size() > 0){
                        cspecRuleSet.setGenes(genesSet);
                        updated = true;
                    }
                }

                if(engineDTO.getRuleSetJSONStr() != null && !engineDTO.getRuleSetJSONStr().equals(cspecRuleSet.getRuleSetJSONStr())){
                    cspecRuleSet.setRuleSetJSONStr(engineDTO.getRuleSetJSONStr());
                    updated = true;
                }
                if(engineDTO.getCriteriaCodesJSONStr() != null && !engineDTO.getCriteriaCodesJSONStr().equals(cspecRuleSet.getCriteriaCodesJSONStr())){
                    cspecRuleSet.setCriteriaCodesJSONStr(engineDTO.getCriteriaCodesJSONStr());
                    updated = true;
                }
                if(engineDTO.getEnabled() != cspecRuleSet.getEnabled()){
                    cspecRuleSet.setEnabled(engineDTO.getEnabled());
                    updated = true;
                }

                if(updated){
                    cspecRuleSetRepository.save(cspecRuleSet);
                    updatedNum++;
                }
            }else{
                //this is a new engine, ID does not exist in the DB
                Set<Gene> genesSet = null;
                if(engineDTO.getGenes() != null && engineDTO.getGenes().size() > 0){
                    //save the related genes first, if any exist
                    genesSet = new HashSet<Gene>();
                    Gene g = null;
                    Set<EngineRelatedGeneDTO> genesTDO = engineDTO.getGenes();
                    for(EngineRelatedGeneDTO erGene : genesTDO){
                        Optional<Gene> geneOpt = geneRepository.findById(erGene.getGeneName());
                        if(geneOpt != null && geneOpt.isPresent()){
                            g = geneOpt.get();
                        }else{
                            Set<Condition> condSet = new HashSet<Condition>();
                            ArrayList<ConditionsTermAndIdDTO> condDTOList = erGene.getConditions();
                            if(condDTOList != null && condDTOList.size() > 0){
                                for(ConditionsTermAndIdDTO condDTO : condDTOList){
                                    condSet.add(new Condition(condDTO.getConditionId(), condDTO.getTerm()));
                                }
                            }
                            String[] hgncAndNcbiId = genesService.getGeneHGNCandNCBIids(erGene.getGeneName());
                            g = new Gene(erGene.getGeneName(), hgncAndNcbiId[0], hgncAndNcbiId[1], condSet);
                            geneRepository.save(g);
                        }
                        genesSet.add(g);
                    }
                }

                cspecRuleSet = new CSpecRuleSet(engineDTO.getEngineId(), engineDTO.getEngineSummary(),
                        engineDTO.getOrganizationName(), engineDTO.getOrganizationLink(), engineDTO.getRuleSetId(), engineDTO.getRuleSetURL(),
                        genesSet, engineDTO.getRuleSetJSONStr(), engineDTO.getCriteriaCodesJSONStr(),
                        engineDTO.getEnabled());

                cspecRuleSetRepository.save(cspecRuleSet);
                addedNew++;
            }
        }
        logger.info("Added total of "+addedNew+" new CSpecEngine RuleSets, enabled: "+enabledNum+", updated: "+updatedNum);
    }
}
