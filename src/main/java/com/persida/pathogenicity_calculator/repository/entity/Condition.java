package com.persida.pathogenicity_calculator.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "\"condition\"")
@Getter
@Setter
public class Condition{

    @Id
    @Column(name = "\"condition_id\"")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "hpo_id", unique=true)
    private String hpoId;

    @Column(name = "term", unique=true)
    private String term;

    @OneToMany(mappedBy = "condition")
    protected Set<VariantInterpretation> variantInterpretations;
}
