package com.PolicyManagement.controller;

import com.PolicyManagement.dto.CustomerRegistrationDto;
import com.PolicyManagement.model.Customers;
import com.PolicyManagement.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registration")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Create new customer
    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("customer", new CustomerRegistrationDto());
        return "registration";  // Thymeleaf template name
    }
    @PostMapping
    public String createCustomer(@ModelAttribute CustomerRegistrationDto registrationDto, Model model) {
        Customers customer = customerService.save(registrationDto);
        model.addAttribute("message", "Customer successfully registered!");
        return "redirect:/registration?success"; // Redirect after successful registration
    }
}

