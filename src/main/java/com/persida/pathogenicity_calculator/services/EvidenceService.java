package com.persida.pathogenicity_calculator.services;

import com.persida.pathogenicity_calculator.repository.entity.Evidence;
import java.util.Set;

public interface EvidenceService {
    public void saveEvidenceSet(Set<Evidence> evidenceSet);
}
