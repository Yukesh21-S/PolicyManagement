package com.PolicyManagement.repository;

import com.PolicyManagement.model.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchemeRepo extends JpaRepository<Scheme, Long> {
}
