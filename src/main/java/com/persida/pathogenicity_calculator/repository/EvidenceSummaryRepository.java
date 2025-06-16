package com.persida.pathogenicity_calculator.repository;

import com.persida.pathogenicity_calculator.repository.entity.Evidence;
import com.persida.pathogenicity_calculator.repository.entity.EvidenceSummary;
import com.persida.pathogenicity_calculator.repository.jpa.EvidenceSummaryJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceSummaryRepository  extends JpaRepository<EvidenceSummary, Long> {

    @Query(value="    SELECT E.evidence_id AS evidenceId, E.type AS evidenceType, E.modifier AS evidenceModifier, ES.evd_summary_id AS evdSummaryId, ES.summary FROM `variant_interpretation` AS VI\n" +
            "    LEFT JOIN `evidence` AS E\n" +
            "    ON VI.interpretation_id = E.interpretation_id\n" +
            "    LEFT JOIN `evidence_summary` AS ES\n" +
            "    ON E.evd_summary_id = ES.evd_summary_id\n" +
            "    WHERE VI.interpretation_id = :interpretationId AND E.full_evidence_label IN :evdLabels ;", nativeQuery = true)
    public List<EvidenceSummaryJPA> getEvdSummariesForVIIdAndEvdTags(@Param("interpretationId") int interpretationId, @Param("evdLabels") String[] evdLabels);
}


