package com.persida.pathogenicity_calculator.repository.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "variant")
@Getter
@Setter
public class Variant extends AbstractEntity{
    @Id
    @Column(name = "variant_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true)
    private String caid;

    @OneToMany(mappedBy = "variant")
    protected Set<VariantInterpretation> variantinterpretation;

    @ManyToOne
    @JoinColumn(name = "gene_id")
    protected Gene gene;

    public Variant() {
        super();
    }

    public Variant(String caid, Gene gene) {
        super();
        this.caid = caid;
        if(gene != null){
            this.gene = gene;
        }
    }
}