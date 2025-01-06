package com.PolicyManagement.repository;

import com.PolicyManagement.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepo extends JpaRepository<Policy, Long> {
}
