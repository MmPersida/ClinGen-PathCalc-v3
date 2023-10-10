package com.persida.pathogenicity_calculator.repository;

import com.persida.pathogenicity_calculator.repository.entity.Condition;
import com.persida.pathogenicity_calculator.repository.jpa.ConditionTermIdJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConditionRepository extends JpaRepository<Condition, Long> {

    @Query(value="SELECT * FROM `condition` AS C WHERE C.condition_id= :conditionId ;", nativeQuery = true)
    public Condition getConditionById(@Param("conditionId") int conditionId);

    @Query(value="SELECT * FROM `condition` AS C WHERE C.term= :term ;", nativeQuery = true)
    public Condition getConditionByName(@Param("term") String term);

    @Query(value="SELECT condition_id as conditionId, term FROM `condition` AS C WHERE C.term LIKE CONCAT(:conditionTerm, '%') LIMIT 25;", nativeQuery = true)
    public List<ConditionTermIdJPA> getConditionTermsLike(@Param("conditionTerm") String conditionTerm);
}
