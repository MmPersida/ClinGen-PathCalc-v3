var evidenceTagDataObj = {
        "BP1":{"tagType":"Benign", "column":"COMPUTATIONAL AND PREDICTIVE DATA", "tagDescriptor":"Supporting", "tagValue":1, "summary":"Missense in gene where only truncating cause disease"},
        "BP2":{"tagType":"Benign", "column":"ALLELIC DATA", "tagDescriptor":"Supporting", "tagValue":2, "summary":"Observed in trans with a pathogenic variant for a fully penetrant dominant gene/disorder or observed in cis with a pathogenic variant in any inheritance pattern"},
        "BP3":{"tagType":"Benign", "column":"COMPUTATIONAL AND PREDICTIVE DATA", "tagDescriptor":"Supporting", "tagValue":3, "summary":"In-frame deletions/insertions in a repetitive region without a known function"},
        "BP4":{"tagType":"Benign", "column":"COMPUTATIONAL AND PREDICTIVE DATA", "tagDescriptor":"Supporting", "tagValue":4, "summary":"Multiple lines of computational evidence suggest no impact on gene or gene product (conservation, evolutionary, splicing impact, etc.)"},
        "BP5":{"tagType":"Benign", "column":"OTHER DATA", "tagDescriptor":"Supporting", "tagValue":5, "summary":"Variant found in a case with an alternate molecular basis for disease"},
        "BP6":{"tagType":"Benign", "column":"OTHER DATA", "tagDescriptor":"Supporting", "tagValue":6, "summary":"Reputable source recently reports variant as benign, but the evidence is not available to the laboratory to perform an independent evaluation. Caution: This evidence code is marked as a controversial to use based on a recent publication (https://www.nature.com/articles/gim201842). We will take a decision of removal of this tag once it is approved by ACMG (https://www.nature.com/articles/gim201843)"},
        "BP7":{"tagType":"Benign", "column":"COMPUTATIONAL AND PREDICTIVE DATA", "tagDescriptor":"Supporting", "tagValue":7, "summary":"A synonymous (silent) variant for which splicing prediction algorithms predict no impact to the splice consensus sequence nor the creation of a new splice site AND the nucleotide is not highly conserved"},
        "BS1":{"tagType":"Benign", "column":"POPULATION DATA", "tagDescriptor":"Strong", "tagValue":1, "summary":"Allele frequency is greater than expected for disorder"},
        "BS2":{"tagType":"Benign", "column":"POPULATION DATA", "tagDescriptor":"Strong", "tagValue":2, "summary":"Observed in a healthy adult individual for a recessive (homozygous), dominant (heterozygous), or X-linked (hemizygous) disorder, with full penetrance expected at an early age"},
        "BS3":{"tagType":"Benign", "column":"FUNCTIONAL DATA", "tagDescriptor":"Strong", "tagValue":3, "summary":"Well-established in vitro or in vivo functional studies show no damaging effect on protein function or splicing"},
        "BS4":{"tagType":"Benign", "column":"SEGREGATION DATA", "tagDescriptor":"Strong", "tagValue":4, "summary":"Lack of segregation in affected members of a family"},
        "BA1":{"tagType":"Benign", "column":"POPULATION DATA", "tagDescriptor":"Stand Alone", "tagValue":1, "summary":"Allele frequency is >5% in Exome Sequencing Project, 1000 Genomes Project, or Exome Aggregation Consortium"},
        "PP1":{"tagType":"Pathogenic", "column":"SEGREGATION DATA", "tagDescriptor":"Supporting", "tagValue":1, "summary":"Cosegregation with disease in multiple affected family members in a gene definitively known to cause the disease"},
        "PP2":{"tagType":"Pathogenic", "column":"FUNCTIONAL DATA", "tagDescriptor":"Supporting", "tagValue":2, "summary":"Missense variant in a gene that has a low rate of benign missense variation and in which missense variants are a common mechanism of disease"},
        "PP3":{"tagType":"Pathogenic", "column":"COMPUTATIONAL AND PREDICTIVE DATA", "tagDescriptor":"Supporting", "tagValue":3, "summary":"Multiple lines of computational evidence support a deleterious effect on the gene or gene product"},
        "PP4":{"tagType":"Pathogenic", "column":"OTHER DATA", "tagDescriptor":"Supporting", "tagValue":4, "summary":"Patients phenotype or family history is highly specific for a disease with a single genetic etiology"},
        "PP5":{"tagType":"Pathogenic", "column":"OTHER DATABASE", "tagDescriptor":"Supporting", "tagValue":5, "summary":"Reputable source recently reports variant as pathogenic, but the evidence is not available to the laboratory to perform an independent evaluation. Caution: This evidence code is marked as a controversial to use based on a recent publication (https://www.nature.com/articles/gim201842). We will take a decision of removal of this tag once it is approved by ACMG (https://www.nature.com/articles/gim201843)"},
        "PM1":{"tagType":"Pathogenic", "column":"FUNCTIONAL DATA", "tagDescriptor":"Moderate", "tagValue":1, "summary":"Located in a mutational hot spot and/or critical and well-established functional domain (e.g., active site of an enzyme) without benign variation"},
        "PM2":{"tagType":"Pathogenic", "column":"POPULATION DATA", "tagDescriptor":"Moderate", "tagValue":2, "summary":"Absent from controls (or at extremely low frequency if recessive) in Exome Sequencing Project, 1000 Genomes Project, or Exome Aggregation Consortium"},
        "PM3":{"tagType":"Pathogenic", "column":"ALLELIC DATA", "tagDescriptor":"Moderate", "tagValue":3, "summary":"For recessive disorders, detected in trans with a pathogenic variant"},
        "PM4":{"tagType":"Pathogenic", "column":"COMPUTATIONAL AND PREDICTIVE DATA", "tagDescriptor":"Moderate", "tagValue":4, "summary":"Protein length changes as a result of in-frame deletions/insertions in a nonrepeat region or stop-loss variants"},
        "PM5":{"tagType":"Pathogenic", "column":"COMPUTATIONAL AND PREDICTIVE DATA", "tagDescriptor":"Moderate", "tagValue":5, "summary":"Novel missense change at an amino acid residue where a different missense change determined to be pathogenic has been seen before"},
        "PM6":{"tagType":"Pathogenic", "column":"DE NOVO DATA", "tagDescriptor":"Moderate", "tagValue":6, "summary":"Assumed de novo, but without confirmation of paternity and maternity"},
        "PS1":{"tagType":"Pathogenic", "column":"COMPUTATIONAL AND PREDICTIVE DATA", "tagDescriptor":"Strong", "tagValue":1, "summary":"Same amino acid change as an established pathogenic variant"},
        "PS2":{"tagType":"Pathogenic", "column":"DE NOVO DATA", "tagDescriptor":"Strong", "tagValue":2, "summary":"De novo (paternity and maternity confirmed)"},
        "PS3":{"tagType":"Pathogenic", "column":"FUNCTIONAL DATA", "tagDescriptor":"Strong", "tagValue":3, "summary":"Well-established functional studies show a deleterious effect"},
        "PS4":{"tagType":"Pathogenic", "column":"POPULATION DATA", "tagDescriptor":"Strong", "tagValue":4, "summary":"Prevalence in affecteds statistically increased over controls"},
        "PVS1":{"tagType":"Pathogenic", "column":"COMPUTATIONAL AND PREDICTIVE DATA", "tagDescriptor":"Very Strong", "tagValue":1, "summary":"Predicted nullvariant in a gene where LOF is a known mechanism of disease"}
};

var basicEvidenceData_row = {
    "POPULATION DATA": {"indx":"0","evidenceValues":["BA1","BS1","BS2","PM2","PS4"]},
    "COMPUTATIONAL AND PREDICTIVE DATA": {"indx":"1","evidenceValues":["BP1", "BP3","BP4", "BP7","PM4","PM5","PP3","PS1","PVS1"]},
    "FUNCTIONAL DATA": {"indx":"2","evidenceValues":["BS3","PM1","PP2", "PS3"]},
    "SEGREGATION DATA": {"indx":"3","evidenceValues":["BS4","PP1"]},
    "DE NOVO DATA": {"indx":"4","evidenceValues":["PM6","PS2"]},
    "ALLELIC DATA": {"indx":"5","evidenceValues":["BP2","PM3"]},
    "OTHER DATABASE": {"indx":"6","evidenceValues":["BP6","PP5"]},
    "OTHER DATA": {"indx":"7","evidenceValues":["BP5","PP4"]}
}

var basicEvidenceTagTypes_columns = {
    "Benign":{"indx":"0", "cssColorClass":"benignGreen", "cssClass":"greenTD", "tagValues": ["Supporting","Strong","Stand Alone"]},
    "Pathogenic":{"indx":"1", "cssColorClass":"pathogenicityPurple", "cssClass":"pinkTD", "tagValues": ["Supporting","Moderate","Strong","Very Strong"]}
}

function getColumnGroupNameFromIndx(indx){
    let keys = Object.keys(basicEvidenceTagTypes_columns);
    for(let k in keys){
        if(Number(indx) == Number(k)){
            return keys[k];
        }
    }
}

function getRowObjectForIndx(rowIndx){
    return getObjectPropertyBasedOnIndex(rowIndx, basicEvidenceData_row);
}

function getColumnGroupObjectForIndx(columnGroupIndex){
    return getObjectPropertyBasedOnIndex(columnGroupIndex, basicEvidenceTagTypes_columns);
}

function getObjectPropertyBasedOnIndex(indx, object){
    let keys = Object.keys(object);
    for(let k in keys){
        if(Number(indx) == Number(k)){
            return object[keys[k]];
        }
    }
}
