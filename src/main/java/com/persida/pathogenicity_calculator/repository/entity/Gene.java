package com.persida.pathogenicity_calculator.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "gene")
@Getter
@Setter
public class Gene {
    @Id
    @Column(name = "gene_id", nullable = false, unique = true)
    private String geneId;

    @Column(name = "conditionNames")
    private String conditionNames;

    @OneToMany(mappedBy = "gene")
    protected Set<Variant> variants;

    @ManyToMany(mappedBy = "genes")
    Set<CSpecRuleSet> cspecRuleSets;

    public Gene(){
        super();
    }

    public Gene(String geneId){
        super();
        this.geneId = geneId;
    }

    public Gene(String geneId, String conditionNames){
        super();
        this.geneId = geneId;
        if(conditionNames != null && !conditionNames.equals("")){
            this.conditionNames = conditionNames;
        }
    }
}
