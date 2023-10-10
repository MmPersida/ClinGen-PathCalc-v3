package com.persida.pathogenicity_calculator.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "evidenceset_id", nullable = false)
    private EvidenceSet evidenceset;

    @ManyToOne
    @JoinColumn(name = "\"condition_id\"", nullable = false)
    protected Condition condition;

    @ManyToOne
    @JoinColumn(name = "finalcall_id", nullable = false)
    protected FinalCall finalcall;

    @ManyToOne
    @JoinColumn(name = "inheritance_id", nullable = false)
    protected Inheritance inheritance;

    public VariantInterpretation(){
        super();
    }

    public VariantInterpretation(User user, Variant variant, EvidenceSet evidenceset, Condition condition,
                                 FinalCall finalcall, Inheritance inheritance){
        super();
        this.user = user;
        this.variant = variant;
        this.evidenceset = evidenceset;
        this.condition = condition;
        this.finalcall = finalcall;
        this.inheritance = inheritance;
    }

    public User getUser() { return user; }
    public Variant getVariant() { return variant; }
    public EvidenceSet getEvidenceset() { return evidenceset; }
    public Condition getCondition() { return condition; }
    public FinalCall getFinalCall() { return finalcall; }
    public Inheritance getInheritance() { return inheritance; }
}