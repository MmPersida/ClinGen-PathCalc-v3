package com.persida.pathogenicity_calculator.repository;

import com.persida.pathogenicity_calculator.repository.entity.EvidenceLink;
import com.persida.pathogenicity_calculator.repository.jpa.EvidenceLinkJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceLinksRepository   extends JpaRepository<EvidenceLink, Long> {

    @Query(value="SELECT * FROM `evidence_link` WHERE evd_link_id = :evdLinkId ;", nativeQuery = true)
    public EvidenceLink getEvdLinkById(@Param("evdLinkId") int evdLinkId);

    @Query(value="  SELECT E.evidence_id AS evidenceId, E.type AS evidenceType, E.modifier AS evidenceModifier, \n" +
            "   EL.evd_link_id AS evdLinkId, EL.comment, EL.link_code AS linkCode, EL.link \n" +
            "   FROM `variant_interpretation` AS VI\n" +
            "   LEFT JOIN `evidence` AS E\n" +
            "   ON VI.interpretation_id = E.interpretation_id\n" +
            "   LEFT JOIN `evidence_link` AS EL\n" +
            "   ON E.evidence_id = EL.evidence_id\n" +
            "   WHERE VI.interpretation_id = :interpretationId AND E.type = :evdTag " +
            "   AND ((E.modifier IS NULL AND :evdModifier IS NULL) OR E.modifier = :evdModifier ) ;", nativeQuery = true)
    public List<EvidenceLinkJPA> getLinksFroVIIdAndEvdTag(@Param("interpretationId") int interpretationId,
                                                          @Param("evdTag") String evdTag,  @Param("evdModifier") String evdModifier);
}
