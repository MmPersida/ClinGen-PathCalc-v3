package com.persida.pathogenicity_calculator.repository;

import com.persida.pathogenicity_calculator.repository.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantRepository  extends JpaRepository<Variant, Long> {

    @Query(value="SELECT * FROM `variant` AS V WHERE V.variant_id = :variantId ;", nativeQuery = true)
    public Variant getVariantById(@Param("variantId") int variantId);

    @Query(value="SELECT * FROM `variant` AS V WHERE V.caid= :caid ;", nativeQuery = true)
    public Variant getVariantByCAID(@Param("caid") String caid);
}
