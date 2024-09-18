package com.persida.pathogenicity_calculator.repository.jpa;

import java.sql.Blob;

public interface SummaryOfClassifiedVariantsJPA {
    public String getFinalcallIds();
    public String getFinalcallTerms();
    public Integer getVariantId();
    public String getCaid();
    public String getGeneId();
}
