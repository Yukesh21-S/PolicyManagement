package com.PolicyManagement.controller;

import com.PolicyManagement.dto.CustomerRegistrationDto;
import com.PolicyManagement.model.Customers;
import com.PolicyManagement.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable("id") long customerId,
                                 @ModelAttribute CustomerRegistrationDto registrationDto,
                                 Model model) {
        Customers updatedCustomer = customerService.update(customerId, registrationDto);
        model.addAttribute("message", "Customer details successfully updated!");
        return "redirect:/customer/update?success"; // Redirect after successful update
    }

    // Find customer by ID
    @GetMapping("/find/{id}")
    public String findCustomerById(@PathVariable("id") long customerId, Model model) {
        Customers customer = customerService.findByCustomerId(customerId);
        if (customer == null) {
            model.addAttribute("error", "Customer not found!");
            return "error"; // Thymeleaf error template
        }
        model.addAttribute("customer", customer);
        return "customerDetails"; // Thymeleaf template to display customer details
    }

    // Delete customer by ID
    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") long customerId, Model model) {
        String message = customerService.delete(customerId);
        model.addAttribute("message", message);
        return "redirect:/customer/delete?success"; // Redirect after successful deletion
    }
}

