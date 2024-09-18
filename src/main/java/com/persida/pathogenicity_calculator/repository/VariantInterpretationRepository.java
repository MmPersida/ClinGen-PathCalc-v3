package com.persida.pathogenicity_calculator.repository;

import com.persida.pathogenicity_calculator.repository.entity.VariantInterpretation;
import com.persida.pathogenicity_calculator.repository.jpa.SummaryOfClassifiedVariantsJPA;
import com.persida.pathogenicity_calculator.repository.jpa.VarinatCAIdJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariantInterpretationRepository extends JpaRepository<VariantInterpretation, Long> {

    @Query(value = "SELECT * FROM `variant_interpretation` AS VI WHERE VI.interpretation_id = :interpretationId ;", nativeQuery = true)
    VariantInterpretation getVariantInterpretationById(@Param("interpretationId") int interpretationId);

    @Query(value = "SELECT * FROM `variant_interpretation` AS VI\n" +
            "LEFT JOIN `variant` AS V\n" +
            "ON VI.variant_id = V.variant_id\n" +
            "WHERE V.caid = :caid\n" +
            "AND VI.user_id = :userid ORDER BY VI.created_on DESC;", nativeQuery = true)
    List<VariantInterpretation> getVariantInterpretationsByCAID(@Param("userid") int userid, @Param("caid") String variantCAID);

    @Query(value = "SELECT DISTINCT V.caid\n" +
            "FROM `variant_interpretation` AS VI\n" +
            "LEFT JOIN `variant` AS V\n" +
            "ON VI.variant_id = V.variant_id\n" +
            "WHERE V.caid LIKE CONCAT(:partialName, '%') \n" +
            "AND VI.user_id = :userid LIMIT 100;", nativeQuery = true)
    List<String> getInterpretedVariantCAIDsLike(@Param("userid") int userid, @Param("partialName") String partialName);

    @Query(value = "SELECT * FROM `variant_interpretation` AS VI\n" +
            "LEFT JOIN `variant` AS V\n" +
            "ON VI.variant_id = V.variant_id\n" +
            "WHERE VI.user_id = :userid ORDER BY VI.modified_on DESC LIMIT 15;", nativeQuery = true)
    List<VariantInterpretation> getRecentlyInterpretedVariants(@Param("userid") int userid);

    @Query(value = "SELECT * FROM `variant_interpretation` AS VI\n" +
            "LEFT JOIN `variant` AS V\n" +
            "ON VI.variant_id = V.variant_id\n" +
            "WHERE VI.user_id = :userid AND V.caid = :caid\n" +
            "AND VI.condition_id = :conId AND VI.inheritance_id = :inherId AND VI.cspecengine_id = :cspecengineId ;", nativeQuery = true)
    List<VariantInterpretation> searchInterpretationsByCaidEvdcDocEngineId(@Param("userid") int userid, @Param("caid") String caid,
                                                                           @Param("conId") String conId, @Param("inherId") int inherId,
                                                                           @Param("cspecengineId") String cspecengineId);

    @Query(value = "SELECT CONVERT(GROUP_CONCAT(VI.finalcall_id SEPARATOR ',') USING utf8) AS finalcallIds, \n" +
            "CONVERT(GROUP_CONCAT(FC.term SEPARATOR ',') USING utf8) AS finalcallTerms, \n" +
            "V.variant_id AS variantId, V.caid, V.gene_id AS geneId \n" +
            "FROM `variant_interpretation` AS VI \n" +
            "LEFT JOIN `variant` AS V \n" +
            "ON VI.variant_id = V.variant_id \n" +
            "LEFT JOIN `final_call` AS FC \n" +
            "ON VI.finalcall_id = FC.finalcall_id \n" +
            "WHERE VI.user_id = :userid \n" +
            "GROUP BY V.gene_id \n" +
            "ORDER BY VI.modified_on DESC LIMIT 15; ", nativeQuery = true)
    List<SummaryOfClassifiedVariantsJPA> getSummaryOfClassifiedVariants(@Param("userid") int userid);
}

