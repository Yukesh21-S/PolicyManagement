package com.PolicyManagement.service;

import com.PolicyManagement.dto.CustomerRegistrationDto;
import com.PolicyManagement.model.Customers;
import com.PolicyManagement.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomerServiceImplementation implements CustomerService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImplementation(BCryptPasswordEncoder passwordEncoder, CustomerRepository customerRepository) {
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
    }

    @Override
    public Customers save(CustomerRegistrationDto registrationDto) {
        Customers customer = new Customers(
                registrationDto.getName(),
                registrationDto.getEmail(),
                passwordEncoder.encode(registrationDto.getPassword()),
                registrationDto.getPhoneNumber(),
                registrationDto.getStatus(),
                registrationDto.getAddress()
        );
        return customerRepository.save(customer);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customers user = customerRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                true,  // account is enabled
                true,  // account is not expired
                true,  // credentials are not expired
                true,  // account is not locked
                new ArrayList<>() // no authorities
        );
    }
}
