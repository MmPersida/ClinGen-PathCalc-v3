package com.persida.pathogenicity_calculator.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "evidence")
@Getter
@Setter
public class Evidence extends AbstractEntity {

    @Id
    @Column(name = "evidence_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type")
    private String evdType;

    @Column(name = "value")
    private Character evdValue;

    @ManyToOne
    @JoinColumn(name = "interpretation_id", nullable = false)
    protected VariantInterpretation variantInterpretation;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "evd_summary_id")
    protected EvidenceSummary evidenceSummary;

    @OneToMany(mappedBy = "evidence", cascade = CascadeType.ALL, orphanRemoval = true)
    protected Set<EvidenceLink> evidenceLinks;

    public Evidence(){
        super();
    }

    public Evidence(String evdType, Character evdValue){
        super();
        this.evdType = evdType;
        this.evdValue = evdValue;
    }

    public Evidence(String evdType, Character evdValue, EvidenceSummary evidenceSummary, VariantInterpretation variantInterpretation){
        super();
        this.evdType = evdType;
        this.evdValue = evdValue;
        if(evidenceSummary != null){
            this.evidenceSummary = evidenceSummary;
        }
        this.variantInterpretation = variantInterpretation;
    }
}
