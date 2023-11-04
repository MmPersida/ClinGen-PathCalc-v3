package com.persida.pathogenicity_calculator.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "evidence_link")
@Getter
@Setter
public class EvidenceLink extends AbstractEntity {

    @Id
    @Column(name = "evd_link_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "link")
    private String evdLink;

    @Column(name = "comment")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "evidence_id", nullable = false)
    protected Evidence evidence;

    public EvidenceLink(){
        super();
    }

    public EvidenceLink(String evdLink){
        super();
        this.evdLink = evdLink;
    }
}
