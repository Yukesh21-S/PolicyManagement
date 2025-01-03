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
import java.util.Optional;

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
    public Customers save(Customers registrationDto) {
        Customers customer = new Customers(
                registrationDto.getName(),
                registrationDto.getEmail(),
                passwordEncoder.encode(registrationDto.getPassword()),
                registrationDto.getPhoneNumber(),
                "ACTIVE",
                registrationDto.getDateOfRegistration(),
                registrationDto.getAddress()
        );
        return customerRepository.save(customer);
    }
    public Customers update(long customerId, Customers registrationDto) {
        // Find the customer by ID
        Optional<Customers> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isEmpty()) {
            throw new RuntimeException("Customer with ID " + customerId + " not found.");
        }

        Customers customer = optionalCustomer.get();

        // Update basic fields
        customer.setName(registrationDto.getName());
        customer.setEmail(registrationDto.getEmail());
        customer.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        customer.setDateOfRegistration(registrationDto.getDateOfRegistration());
        customer.setPhoneNumber(registrationDto.getPhoneNumber());
        customer.setStatus(registrationDto.getStatus());

        // Update address if provided
        if (registrationDto.getAddress() != null) {
            if (customer.getAddress() == null) {
                // If no existing address, set the new address
                customer.setAddress(registrationDto.getAddress());
            } else {
                // Update existing address fields
                customer.getAddress().setStreet(registrationDto.getAddress().getStreet());
                customer.getAddress().setCity(registrationDto.getAddress().getCity());
                customer.getAddress().setState(registrationDto.getAddress().getState());
                customer.getAddress().setCountry(registrationDto.getAddress().getCountry());

            }
        }

        // Save the updated customer
        return customerRepository.save(customer);
    }

    public Customers findByCustomerId(long customerId) {
        Optional<Customers> customer = customerRepository.findById(customerId);
        if (customer.isEmpty()) {
            return null;
        } else {
            return customer.get();
        }
    }
    public String delete(long customerId) {
        Optional<Customers> customer = customerRepository.findById(customerId);
        if(customer.isPresent()) {
           Customers customernew= customer.get();
           customernew.setStatus("DEACTIVATED");

        customerRepository.save(customernew);
            return "Successfully DEACTIVATED";}
        return "Customer not found";
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
