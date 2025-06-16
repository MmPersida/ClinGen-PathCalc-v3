package com.persida.pathogenicity_calculator.repository;

import com.persida.pathogenicity_calculator.repository.entity.Inheritance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InheritanceRepository extends JpaRepository<Inheritance, Long> {

    @Query(value="SELECT * FROM `inheritance` AS I ORDER BY I.inheritance_id ;", nativeQuery = true)
    public List<Inheritance> getInheritanceModes();

    @Query(value="SELECT * FROM `inheritance` AS I WHERE I.inheritance_id= :inheritanceId ;", nativeQuery = true)
    public Inheritance getInheritanceById(@Param("inheritanceId") int inheritanceId);

    @Query(value="SELECT * FROM `inheritance` AS I WHERE I.term= :term ;", nativeQuery = true)
    public Inheritance getInheritanceByName(@Param("term") String term);
}
