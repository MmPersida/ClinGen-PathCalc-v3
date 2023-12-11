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
    @Column(name = "\"condition_id\"", nullable = false, unique=true)
    private String condition_id;

    @Column(name = "term",nullable = false)
    private String term;

    @OneToMany(mappedBy = "condition")
    protected Set<VariantInterpretation> variantInterpretations;

    @ManyToMany(mappedBy = "conditions")
    Set<Gene> genes;

    public Condition(){
        super();
    }

    public Condition(String condition_id, String term){
        super();
        this.condition_id = condition_id;
        this.term = term;
    }
}
