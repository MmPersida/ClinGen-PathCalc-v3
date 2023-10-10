package com.persida.pathogenicity_calculator.repository;

import com.persida.pathogenicity_calculator.repository.entity.FinalCall;
import com.persida.pathogenicity_calculator.repository.entity.Inheritance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FinalCallRepository extends JpaRepository<FinalCall, Long> {

    @Query(value="SELECT * FROM `final_call` AS FC WHERE FC.finalcall_id= :finalCallId ;", nativeQuery = true)
    public FinalCall getFinalCallById(@Param("finalCallId") int finalCallId);

    @Query(value="SELECT * FROM `final_call` AS FC WHERE FC.term= :term ;", nativeQuery = true)
    public FinalCall getFinalCallByName(@Param("term") String term);

    @Query(value="SELECT * FROM `final_call` AS FC WHERE FC.term= 'INSUFFICIENT' ;", nativeQuery = true)
    public FinalCall getFinalCallInsufficient();
}
