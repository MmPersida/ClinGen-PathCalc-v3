package com.persida.pathogenicity_calculator.repository.jpa;

public interface EvidenceSummaryJPA {
    public Integer getEvidenceId();
    public String getEvidenceType();
    public String getEvidenceModifier();
    public Integer getEvdSummaryId();
    public String getSummary();
}
