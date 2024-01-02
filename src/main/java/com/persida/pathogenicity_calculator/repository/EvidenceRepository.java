package com.persida.pathogenicity_calculator.repository;

import com.persida.pathogenicity_calculator.repository.entity.Evidence;
import com.persida.pathogenicity_calculator.repository.entity.EvidenceSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    @Query(value="SELECT * FROM `evidence` AS E WHERE E.interpretation_id = :interpretationId AND E.type = :evdTagName ;", nativeQuery = true)
    public Evidence getEvidenceByNameAndVIId(@Param("interpretationId") int interpretationId, @Param("evdTagName") String evdTagName);
}
