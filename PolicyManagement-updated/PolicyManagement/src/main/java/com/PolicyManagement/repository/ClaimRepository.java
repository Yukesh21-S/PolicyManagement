package com.PolicyManagement.repository;

import com.PolicyManagement.model.Claim;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends CrudRepository<Claim, Long> {
    List<Claim> findByClaimStatus(String claimStatus);

    @Query("SELECT c FROM Claim c WHERE c.policy.policyId = :policyId AND c.customer.email = :customerEmail")
    Optional<Claim> findByPolicyIdAndCustomerEmail(@Param("policyId") Long policyId, @Param("customerEmail") String customerEmail);
}