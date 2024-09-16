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

    @Column(name = "hgnc_id")
    private String hgncId;

    @Column(name = "ncbi_id")
    private String ncbiId;

    @OneToMany(mappedBy = "gene")
    protected Set<Variant> variants;

    @ManyToMany(mappedBy = "genes")
    Set<CSpecRuleSet> cspecRuleSets;

    @ManyToMany
    @JoinTable(
            name = "gene_condition",
            joinColumns = @JoinColumn(name = "gene_id"),
            inverseJoinColumns = @JoinColumn(name = "\"condition_id\""))
    Set<Condition> conditions;

    public Gene(){
        super();
    }

    public Gene(String geneId){
        super();
        this.geneId = geneId;
    }

    public Gene(String geneId, String hgncId, String ncbiId){
        super();
        this.geneId = geneId;
        this.hgncId = hgncId;
        this.ncbiId = ncbiId;
    }

    public Gene(String geneId, String hgncId, String ncbiId, Set<Condition> conditions){
        super();
        this.geneId = geneId;
        this.hgncId = hgncId;
        this.ncbiId = ncbiId;
        if(conditions != null && conditions.size() > 0){
            this.conditions = conditions;
        }
    }
}
