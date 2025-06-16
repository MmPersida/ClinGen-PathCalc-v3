package com.persida.pathogenicity_calculator.repository.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "inheritance")
@Getter
@Setter
public class Inheritance{

    @Id
    @Column(name = "inheritance_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "term", unique=true)
    private String term;

    @OneToMany(mappedBy = "inheritance")
    protected Set<VariantInterpretation> variantInterpretations;
}
