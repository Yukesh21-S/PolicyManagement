package com.PolicyManagement.service;

import com.PolicyManagement.model.Policy;
import com.PolicyManagement.repository.PolicyRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyService {
    @Autowired
    private PolicyRepo policyRepository;

    // Get all policies
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    // Save a policy
    public void savePolicy(Policy policy) {
        policyRepository.save(policy);
    }

    // Get policy by ID
    public Policy getPolicyById(Long id) {
        return policyRepository.findById(id).orElse(null);
    }

    // Delete policy by ID
    public void deletePolicy(Long id) {
        policyRepository.deleteById(id);
    }

    public void updatePolicy(Policy policy) {
        // Fetch the existing policy from the database
        Optional<Policy> existingPolicyOpt = policyRepository.findById(policy.getPolicyId());

        if (existingPolicyOpt.isPresent()) {
            Policy existingPolicy = existingPolicyOpt.get();

            // Update the fields with new values
            existingPolicy.setAdmin(policy.getAdmin());
            existingPolicy.setDescription(policy.getDescription());
            existingPolicy.setName(policy.getName());
            existingPolicy.setAnnuityTerm(policy.getAnnuityTerm());

            // Save the updated policy
            policyRepository.save(existingPolicy);
        } else {
            throw new EntityNotFoundException("Policy with ID " + policy.getPolicyId() + " not found.");
        }
    }
}
