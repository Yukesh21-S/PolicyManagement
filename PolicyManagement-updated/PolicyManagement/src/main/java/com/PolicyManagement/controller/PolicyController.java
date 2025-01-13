package com.PolicyManagement.controller;


import com.PolicyManagement.model.Admin;
import com.PolicyManagement.model.Customers;
import com.PolicyManagement.model.Policy;
import com.PolicyManagement.repository.AdminRepo;
import com.PolicyManagement.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/policies")
public class PolicyController {

    @Autowired
    private CustomerServiceImplementation customerService;
    @Autowired
    private PolicyService policyService;
    @Autowired
    private SchemeServiceImplementation schemeService;
@Autowired
private AdminRepo adminRepo;
   // private static final Logger logger = LoggerFactory.getLogger(PolicyController.class);
    // Show list of policies (accessible to all authenticated users)
    @GetMapping
    public String getAllPolicies(Model model) {
        List<Policy> policies = policyService.getAllPolicies();
        model.addAttribute("policies", policies);
        return "policy-list";
    }

    // Show form to create a new policy (admin-only access)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("policy", new Policy());
        model.addAttribute("schemes", schemeService.getAllSchemes());  // Fetch all schemes
        return "policy-form";
    }

    @PreAuthorize("hasRole('ADMIN')")
        @PostMapping("/save")
        public String saveOrUpdatePolicy(@ModelAttribute("policy") Policy policy) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String adminEmail = user.getUsername();

            // Find the admin by email
            Optional<Admin> admin = adminRepo.findByEmail(adminEmail);

            if (admin.isEmpty()) {
                throw new IllegalStateException("Admin not found.");
            }

            policy.setAdmin(admin.get());

        //    logger.info("Policy ID: " + policy.getPolicyId());  // Log policy ID
            if (policy.getPolicyId() == null) {
                // Create new policy
                policyService.savePolicy(policy);
            } else {
                // Update existing policy
                policyService.updatePolicy(policy);
            }
            return "redirect:/admin/policies";
        }





    // Show form to update a policy (admin-only access)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Policy policy = policyService.getPolicyById(id);
        model.addAttribute("policy", policy);
        model.addAttribute("schemes", schemeService.getAllSchemes());
        return "policy-form";
    }


    // Delete a policy (admin-only access)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping ("/delete/{id}")
    public String deletePolicy(@PathVariable("id") Long id) {
        policyService.deletePolicy(id);
        return "redirect:/admin/policies";
    }
    @GetMapping("/{id}/customers")
    public String viewCustomersForPolicy(@PathVariable Long id, Model model) {
        Policy policy = policyService.getPolicyById(id);
        List<Customers> customers = customerService.getCustomersByPolicyId(id);
        model.addAttribute("policyName", policy.getName());
        model.addAttribute("customers", customers);
        return "policy-customer";
    }
    @GetMapping("/{id}/claimed-customers")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewClaimedCustomersForPolicy(@PathVariable Long id, Model model) {
        // Fetch the policy by ID
        Policy policy = policyService.getPolicyById(id);
        if (policy == null) {
            model.addAttribute("errorMessage", "Policy not found!");
            return "redirect:/admin/policies";
        }

        // Fetch customers who have claims for this policy
        List<Customers> allClaimedCustomers = customerService.getCustomersByClaimedPolicyId(id);

        // Filter customers whose claims for this policy have a status of "Approved"
        List<Customers> approvedClaimedCustomers = allClaimedCustomers.stream()
                .filter(customer -> customer.getClaims().stream()
                        .anyMatch(claim -> "Approved".equalsIgnoreCase(claim.getClaimStatus())
                                && claim.getPolicy().getPolicyId().equals(id)))
                .toList();

        if (approvedClaimedCustomers.isEmpty()) {
            model.addAttribute("message", "No customers with approved claims for this policy.");
        } else {
            model.addAttribute("customers", approvedClaimedCustomers);
        }

        model.addAttribute("policyName", policy.getName());
        return "view-claims"; // Render a view for claimed customers with approved claims
    }


}