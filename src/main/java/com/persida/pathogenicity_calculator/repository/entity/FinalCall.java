package com.persida.pathogenicity_calculator.repository.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "final_call")
@Getter
@Setter
public class FinalCall{

    @Id
    @Column(name = "finalcall_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "term", unique=true)
    private String term;

    @OneToMany(mappedBy = "finalcall")
    protected Set<VariantInterpretation> variantInterpretations;

    @OneToMany(mappedBy = "determinedFinalCall")
    protected Set<VariantInterpretation> variantInterpretations2;
}
