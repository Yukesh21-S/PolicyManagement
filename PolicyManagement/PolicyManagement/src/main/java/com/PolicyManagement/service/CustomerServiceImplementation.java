package com.PolicyManagement.service;

import com.PolicyManagement.model.Customers;
import com.PolicyManagement.model.Role;
import com.PolicyManagement.model.Admin;
import com.PolicyManagement.password.PasswordValidator;
import com.PolicyManagement.repository.AdminRepo;
import com.PolicyManagement.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.PolicyManagement.model.Role.ADMIN;

@Service
public class CustomerServiceImplementation implements CustomerService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;
    private final AdminRepo adminRepository;

    @Autowired
    public CustomerServiceImplementation(
            BCryptPasswordEncoder passwordEncoder,
            CustomerRepository customerRepository,
            AdminRepo adminRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public Customers save(Customers registrationDto) {
        if (!PasswordValidator.validatePassword(registrationDto.getPassword())) {
            throw new IllegalArgumentException("Password does not meet the required criteria.");
        }

        Customers customer = new Customers(
                registrationDto.getName(),
                registrationDto.getEmail(),
                passwordEncoder.encode(registrationDto.getPassword()),
                registrationDto.getPhoneNumber(),
                "ACTIVE",
                registrationDto.getDateOfRegistration(),
                registrationDto.getAddress(),
                Role.CUSTOMER
        );
        return customerRepository.save(customer);
    }

    public Customers update(long customerId, Customers registrationDto) {
        Optional<Customers> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isEmpty()) {
            throw new RuntimeException("Customer with ID " + customerId + " not found.");
        }

        Customers customer = optionalCustomer.get();
        customer.setName(registrationDto.getName());
        customer.setEmail(registrationDto.getEmail());
        customer.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        customer.setDateOfRegistration(registrationDto.getDateOfRegistration());
        customer.setPhoneNumber(registrationDto.getPhoneNumber());
        customer.setStatus(registrationDto.getStatus());
        customer.setRole(Role.CUSTOMER);

        if (registrationDto.getAddress() != null) {
            if (customer.getAddress() == null) {
                customer.setAddress(registrationDto.getAddress());
            } else {
                customer.getAddress().setStreet(registrationDto.getAddress().getStreet());
                customer.getAddress().setCity(registrationDto.getAddress().getCity());
                customer.getAddress().setState(registrationDto.getAddress().getState());
                customer.getAddress().setCountry(registrationDto.getAddress().getCountry());
            }
        }

        return customerRepository.save(customer);
    }

    public Customers findByCustomerId(long customerId) {
        Optional<Customers> customer = customerRepository.findById(customerId);
        return customer.orElse(null);
    }

    public String delete(long customerId) {
        Optional<Customers> customer = customerRepository.findById(customerId);
        if (customer.isPresent()) {
            Customers customerToDelete = customer.get();
            customerToDelete.setStatus("DEACTIVATED");
            customerRepository.save(customerToDelete);
            return "Successfully DEACTIVATED";
        }
        return "Customer not found";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // First, check if the username belongs to a customer
        Customers customer = customerRepository.findByEmail(username);
        if (customer != null) {
            List<GrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + customer.getRole().name())
            );

            return new User(
                    customer.getEmail(),
                    customer.getPassword(),
                    true,  // account is enabled
                    true,  // account is not expired
                    true,  // credentials are not expired
                    true,  // account is not locked
                    authorities
            );
        }

        // If not a customer, check if the username belongs to an admin
        Admin admin = adminRepository.findByEmail(username);
        if (admin != null) {
            List<GrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + ADMIN)
            );

            return new User(
                    admin.getEmail(),
                    admin.getPassword(),
                    true,  // account is enabled
                    true,  // account is not expired
                    true,  // credentials are not expired
                    true,  // account is not locked
                    authorities
            );
        }

        // If neither, throw exception
        throw new UsernameNotFoundException("Invalid username or password.");
    }
}