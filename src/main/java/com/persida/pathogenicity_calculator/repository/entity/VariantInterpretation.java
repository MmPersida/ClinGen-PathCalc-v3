package com.persida.pathogenicity_calculator.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "variant_interpretation")
@Getter
@Setter
public class VariantInterpretation extends AbstractEntity{

    @Id
    @Column(name = "interpretation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    protected User user;

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    @OneToMany(mappedBy = "variantInterpretation", cascade = CascadeType.ALL, orphanRemoval = true)
    protected Set<Evidence> evidences;

    @ManyToOne
    @JoinColumn(name = "\"condition_id\"", nullable = false)
    protected Condition condition;

    @ManyToOne
    @JoinColumn(name = "finalcall_id", nullable = false)
    protected FinalCall finalcall;

    @ManyToOne
    @JoinColumn(name = "determined_finalcall_id")
    protected FinalCall determinedFinalCall;

    @ManyToOne
    @JoinColumn(name = "inheritance_id", nullable = false)
    protected Inheritance inheritance;

    @Column(name = "vi_description", columnDefinition = "TEXT")
    protected String viDescription;

    @ManyToOne
    @JoinColumn(name = "cspecengine_id", nullable = false)
    protected CSpecRuleSet cspecRuleSet;

    public VariantInterpretation(){
        super();
    }

    public VariantInterpretation(User user, Variant variant, Set<Evidence> evidences, Condition condition,
                                 FinalCall finalcall, Inheritance inheritance, CSpecRuleSet cspecRuleSet){
        super();
        this.user = user;
        this.variant = variant;
        this.evidences = evidences;
        this.condition = condition;
        this.finalcall = finalcall;
        this.inheritance = inheritance;
        this.cspecRuleSet = cspecRuleSet;
    }

    public User getUser() { return this.user; }
    public Variant getVariant() { return this.variant; }
    public Set<Evidence> getEvidences() { return this.evidences; }
    public Condition getCondition() { return this.condition; }
    public FinalCall getFinalCall() { return this.finalcall; }
    public FinalCall getDeterminedFinalCall() { return this.determinedFinalCall; }
    public Inheritance getInheritance() { return this.inheritance; }
}