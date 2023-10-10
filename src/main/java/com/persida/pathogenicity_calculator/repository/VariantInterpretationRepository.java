package com.persida.pathogenicity_calculator.repository;

import com.persida.pathogenicity_calculator.repository.entity.VariantInterpretation;
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
            "AND VI.user_id = :userid ;", nativeQuery = true)
    VariantInterpretation getVariantInterpretationByCAID(@Param("userid") int userid, @Param("caid") String variantCAID);

    @Query(value = "SELECT V.caid, VI.modified_on AS modifiedOn\n" +
            "FROM `variant_interpretation` AS VI\n" +
            "LEFT JOIN `variant` AS V\n" +
            "ON VI.variant_id = V.variant_id\n" +
            "WHERE V.caid LIKE CONCAT(:partialName, '%') \n" +
            "AND VI.user_id = :userid LIMIT 100;", nativeQuery = true)
    List<VarinatCAIdJPA> getInterpretedVariantCAIDsLike(@Param("userid") int userid, @Param("partialName") String partialName);

    @Query(value = "SELECT * FROM `variant_interpretation` AS VI\n" +
            "LEFT JOIN `variant` AS V\n" +
            "ON VI.variant_id = V.variant_id\n" +
            "WHERE VI.user_id = :userid ORDER BY VI.modified_on DESC LIMIT 10;", nativeQuery = true)
    List<VariantInterpretation> getRecentlyInterpretedVariants(@Param("userid") int userid);

    /*
    @Query(value = "SELECT FC.term FROM " +
            "`variant_interpretation` AS VI\n" +
            "LEFT JOIN `variant` AS V\n" +
            "ON VI.variant_id = V.variant_id\n" +
            "LEFT JOIN `final_call` AS FC\n" +
            "ON VI.finalcall_id = FC.finalcall_id\n" +
            "WHERE V.caid = :caid\n" +
            "AND VI.user_id = :userid ;", nativeQuery = true)
    String getFinalCallForCaID(@Param("userid") int userid, @Param("caid") String variantCAID);
    */
}

