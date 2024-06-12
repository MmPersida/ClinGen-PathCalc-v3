package com.persida.pathogenicity_calculator.repository;

import com.persida.pathogenicity_calculator.repository.entity.Gene;
import com.persida.pathogenicity_calculator.repository.jpa.EngineDataForGeneJPA;
import com.persida.pathogenicity_calculator.repository.jpa.EvidenceLinkJPA;
import com.persida.pathogenicity_calculator.repository.jpa.EvidenceSummaryJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneRepository extends JpaRepository<Gene, String> {

    @Query(value = "SELECT G.gene_id AS geneName, EG.engine_id AS engineId, RS.enabled, RS.organization FROM `gene` as G\n" +
            "LEFT JOIN `engine_gene` AS EG\n" +
            "ON G.gene_id = EG.gene_id\n" +
            "Left JOIN `scpec_ruleset` AS RS\n" +
            "ON EG.engine_id = RS.engine_id\n" +
            "Where g.gene_id = :geneName ;", nativeQuery = true)
    public EngineDataForGeneJPA getEngineDataForGene(@Param("geneName") String geneName);
}
