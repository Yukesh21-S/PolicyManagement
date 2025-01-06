package com.PolicyManagement.controller;


import com.PolicyManagement.model.Admin;
import com.PolicyManagement.model.Policy;
import com.PolicyManagement.repository.AdminRepo;
import com.PolicyManagement.service.AdminService;
import com.PolicyManagement.service.PolicyService;
import com.PolicyManagement.service.SchemeService;
import com.PolicyManagement.service.SchemeServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/policies")
public class PolicyController {

    @Autowired
    private PolicyService policyService;
    @Autowired
    private SchemeServiceImplementation schemeService;
@Autowired
private AdminRepo adminRepo;
    // Show list of policies (accessible to all authenticated users)
    @GetMapping
    public String getAllPolicies(Model model) {
        List<Policy> policies = policyService.getAllPolicies();
        model.addAttribute("policies", policies);
        return "policy-list";
    }

    // Show form to create a new policy (admin-only access)
    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("policy", new Policy());
        model.addAttribute("schemes", schemeService.getAllSchemes());  // Fetch all schemes
        return "policy-form";
    }


    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String savePolicy(@ModelAttribute("policy") Policy policy) {
        // If you're updating the policy, make sure you correctly set the Scheme
        if (policy.getPolicyId() == null) {
            // Create new policy
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String adminEmail = user.getUsername();

            // Find the admin by email
            Admin admin = adminRepo.findByEmail(adminEmail);
            policy.setAdmin(admin);
            policyService.savePolicy(policy);
        } else {
            // Update existing policy
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String adminEmail = user.getUsername();

            // Find the admin by email
            Admin admin = adminRepo.findByEmail(adminEmail);
            policy.setAdmin(admin);
            policyService.updatePolicy(policy);
        }
        return "redirect:/policies";
    }


    // Show form to update a policy (admin-only access)
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Policy policy = policyService.getPolicyById(id);
        model.addAttribute("policy", policy);
        model.addAttribute("schemes", schemeService.getAllSchemes());  // Fetch all schemes
        return "policy-form";
    }

    // Delete a policy (admin-only access)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping ("/delete/{id}")
    public String deletePolicy(@PathVariable("id") Long id) {
        policyService.deletePolicy(id);
        return "redirect:/policies";
    }
}