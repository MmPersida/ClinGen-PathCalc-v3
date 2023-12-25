package com.persida.pathogenicity_calculator.repository.jpa;

public interface EvidenceSummaryJPA {
    public Integer getEvidenceId();
    public String getFullEvidenceLabel();
    public Integer getEvdSummaryId();
    public String getSummary();
}
