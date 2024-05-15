package com.persida.pathogenicity_calculator.repository;

import com.persida.pathogenicity_calculator.repository.entity.CSpecRuleSet;
import com.persida.pathogenicity_calculator.repository.jpa.CSpecRuleSetJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CSpecRuleSetRepository extends JpaRepository<CSpecRuleSet, String> {

    @Query(value="SELECT * FROM `scpec_ruleset` AS CS WHERE CS.engine_id = :engineId ;", nativeQuery = true)
    public CSpecRuleSet getCSpecRuleSetById(@Param("engineId") String engineId);

    @Query(value="SELECT CSR.engine_id AS engineId, CSR.engine_summary AS engineSummary, CSR.organization,\n" +
            "EG.gene_id AS geneId, GC.condition_id AS conditionId FROM `scpec_ruleset` AS CSR\n" +
            "LEFT JOIN `engine_gene` AS EG\n" +
            "ON CSR.engine_id = EG.engine_id\n" +
            "LEFT JOIN `gene_condition` AS GC\n" +
            "ON EG.gene_id = GC.gene_id\n" +
            "WHERE CSR.enabled = true AND (EG.gene_id = :geneNameId OR GC.condition_id = :conditionId) ;", nativeQuery = true)
    public List<CSpecRuleSetJPA> getSortedAndEnabledCSpecEngines(@Param("geneNameId") String geneNameId, @Param("conditionId") String conditionId);

    @Query(value="SELECT CSR.engine_id AS engineId, CSR.engine_summary AS engineSummary, CSR.organization\n" +
            "FROM `scpec_ruleset` AS CSR WHERE CSR.enabled = true ;", nativeQuery = true)
    public List<CSpecRuleSetJPA> getAllEnabledCSpecEnginesBasicInfo();

    @Query(value="SELECT * FROM `scpec_ruleset` AS CSR WHERE CSR.enabled = true ;", nativeQuery = true)
    public List<CSpecRuleSet> getAllEnabledCSpecEnginesInfo();

    @Query(value="SELECT * FROM `scpec_ruleset` AS CSR \n" +
            "WHERE CSR.organization LIKE CONCAT('%', :partialName, '%') \n" +
            "AND CSR.enabled = true ;", nativeQuery = true)
    public List<CSpecRuleSet> getAllEnabledCSpecEnginesInfoByNameLike(@Param("partialName") String partialName);
}
