package com.persida.pathogenicity_calculator.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "evidence_set")
@Getter
@Setter
public class EvidenceSet extends AbstractEntity {

    @Id
    @Column(name = "evidenceset_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(mappedBy = "evidenceset")
    private VariantInterpretation variantInterpretation;

    @Column(name = "bp1")
    private Character bp1;
    @Column(name = "bp2")
    private Character bp2;
    @Column(name = "bp3")
    private Character bp3;
    @Column(name = "bp4")
    private Character bp4;
    @Column(name = "bp5")
    private Character bp5;
    @Column(name = "bp6")
    private Character bp6;
    @Column(name = "bp7")
    private Character bp7;
    @Column(name = "bs1")
    private Character bs1;
    @Column(name = "bs2")
    private Character bs2;
    @Column(name = "bs3")
    private Character bs3;
    @Column(name = "bs4")
    private Character bs4;
    @Column(name = "ba1")
    private Boolean ba1;
    @Column(name = "pp1")
    private Character pp1;
    @Column(name = "pp2")
    private Character pp2;
    @Column(name = "pp3")
    private Character pp3;
    @Column(name = "pp4")
    private Character pp4;
    @Column(name = "pp5")
    private Character pp5;
    @Column(name = "pm1")
    private Character pm1;
    @Column(name = "pm2")
    private Character pm2;
    @Column(name = "pm3")
    private Character pm3;
    @Column(name = "pm4")
    private Character pm4;
    @Column(name = "pm5")
    private Character pm5;
    @Column(name = "pm6")
    private Character pm6;
    @Column(name = "ps1")
    private Character ps1;
    @Column(name = "ps2")
    private Character ps2;
    @Column(name = "ps3")
    private Character ps3;
    @Column(name = "ps4")
    private Character ps4;
    @Column(name = "pvs1")
    private Character pvs1;

}

