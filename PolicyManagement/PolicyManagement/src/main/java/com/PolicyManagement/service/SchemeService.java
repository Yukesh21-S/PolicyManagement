package com.PolicyManagement.service;

import com.PolicyManagement.model.Scheme;
import com.PolicyManagement.repository.SchemeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SchemeService {

    @Autowired
    private SchemeRepo schemeRepo;

    public Scheme createScheme(Scheme scheme) {
        // Save the new scheme
        return schemeRepo.save(scheme);
    }
    public Scheme deactivateScheme(Long schemeId) {
        Optional<Scheme> existingScheme = schemeRepo.findById(schemeId);

        if (existingScheme.isPresent()) {
            Scheme scheme = existingScheme.get();
            scheme.setStatus("ACTIVE");  // Mark scheme as inactive
            return schemeRepo.save(scheme);
        }

        //throw new SchemeNotFoundException("Scheme with ID " + schemeId + " not found.");
    }
}
