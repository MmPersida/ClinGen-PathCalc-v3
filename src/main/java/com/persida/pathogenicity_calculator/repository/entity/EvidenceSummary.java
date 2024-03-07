package com.persida.pathogenicity_calculator.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "evidence_summary")
@Getter
@Setter
public class EvidenceSummary extends AbstractEntity {

    @Id
    @Column(name = "evd_summary_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @OneToOne(mappedBy = "evidenceSummary")
    private Evidence evidence;

    public EvidenceSummary(){
        super();
    }

    public EvidenceSummary(String summary){
        super();
        this.summary = summary;
    }
}
