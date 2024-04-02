package com.persida.pathogenicity_calculator.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "scpec_ruleset")
@Getter
@Setter
public class CSpecRuleSet {
    @Id
    @Column(name = "engine_id", nullable = false, unique=true)
    private String engineId;

    @Column(name = "engine_summary")
    private String engineSummary;

    @Column(name = "organization")
    private String organizationName;

    @Column(name = "ruleset_id")
    private Integer ruleSetId;

    @Column(name = "ruleset_url")
    private String ruleSetURL;

    @Column(name = "ruleset_jsonstr", columnDefinition = "TEXT")
    private String ruleSetJSONStr;

    @Column(name = "criteriacodes_jsonstr", columnDefinition = "TEXT")
    private String criteriaCodesJSONStr;

    @Column(name = "enabled")
    private Boolean enabled;

    @OneToMany(mappedBy = "cspecRuleSet")
    protected Set<VariantInterpretation> variantInterpretations;

    @ManyToMany
    @JoinTable(
            name = "engine_gene",
            joinColumns = @JoinColumn(name = "engine_id"),
            inverseJoinColumns = @JoinColumn(name = "gene_id"))
    Set<Gene> genes;

    public CSpecRuleSet(){
        super();
    }

    public CSpecRuleSet(String engineId, String engineSummary, String organizationName, Integer ruleSetId,
                        String ruleSetURL, Set<Gene> genes, String ruleSetJSONStr, String criteriaCodesJSONStr,
                        boolean enabled){
        super();
        this.engineId = engineId;
        this.engineSummary = engineSummary;
        this.organizationName = organizationName;
        this.ruleSetId = ruleSetId;
        this.ruleSetURL = ruleSetURL;
        if(genes != null && genes.size() > 0){
            this.genes = genes;
        }
        this.ruleSetJSONStr = ruleSetJSONStr;
        this.criteriaCodesJSONStr = criteriaCodesJSONStr;
        this.enabled = enabled;
    }
}