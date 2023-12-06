package com.persida.pathogenicity_calculator.repository;

import com.persida.pathogenicity_calculator.repository.entity.CSpecRuleSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CSpecRuleSetRepository extends JpaRepository<CSpecRuleSet, String> {
    @Query(value="SELECT * FROM `scpec_ruleset` AS CS WHERE CS.engine_id = :engineId ;", nativeQuery = true)
    public CSpecRuleSet getCSpecRuleSetById(@Param("engineId") String engineId);
}
