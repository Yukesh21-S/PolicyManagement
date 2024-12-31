package com.PolicyManagement.service;

import com.PolicyManagement.dto.CustomerRegistrationDto;
import com.PolicyManagement.model.Customers;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


public interface CustomerService extends UserDetailsService {
    Customers save(CustomerRegistrationDto registrationDto);
}