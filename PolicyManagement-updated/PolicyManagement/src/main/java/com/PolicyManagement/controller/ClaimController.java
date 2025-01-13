package com.PolicyManagement.controller;



import com.PolicyManagement.model.Claim;
import com.PolicyManagement.model.Customers;
import com.PolicyManagement.model.Policy;
import com.PolicyManagement.service.ClaimService;
import com.PolicyManagement.service.CustomerService;
import com.PolicyManagement.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/claim")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private CustomerService customerService;

    /**
     * Customer claims a policy with a reason.
     */
    @GetMapping("/claimPolicy/{policyId}")
    public String showClaimPolicyPage(@PathVariable("policyId") Long policyId, Model model) {
        Policy policy = policyService.getPolicyById(policyId);
        if (policy == null) {
            model.addAttribute("error", "Policy not found!");
            return "redirect:/customer/my-policies"; // Redirect to policies list if not found
        }
        model.addAttribute("policy", policy);
        return "claim-policy"; // Display the claim-policy.html page
    }

    // Submit the claim for the selected policy
    @PostMapping("/claimPolicy/{policyId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String claimPolicy(@PathVariable Long policyId,
                              @RequestParam String claimReason,
                              Authentication authentication,
                              Model model) {
        // Fetch the authenticated customer
        Customers loggedInCustomer = getAuthenticatedCustomer(authentication);
        if (loggedInCustomer == null) {
            model.addAttribute("error", "User not authenticated!");
            return "redirect:/login"; // Redirect to login if user is not authenticated
        }

        // Retrieve the policy and check if it exists
        Policy policy = policyService.getPolicyById(policyId);
        if (policy == null) {
            model.addAttribute("error", "Policy not found!");
            return "redirect:/customer/my-policies"; // Redirect to policies list if policy doesn't exist
        }

        // Check if the customer has already claimed the policy
        boolean alreadyClaimed = loggedInCustomer.getClaims().stream()
                .anyMatch(claim -> claim.getPolicy().getPolicyId().equals(policyId));
        if (alreadyClaimed) {
            model.addAttribute("error", "You have already claimed this policy.");
            return "claim-policy"; // Show the claim form again if the user already claimed
        }

        // Create and save the new claim
        Claim claim = new Claim();
        claim.setCustomer(loggedInCustomer);
        claim.setPolicy(policy);
        claim.setClaimReason(claimReason);
        claim.setClaimStatus("PROCESSING"); // Default status
        claim.setClaimDate(new Date());
        claimService.saveClaim(claim);

        model.addAttribute("message", "Your claim has been successfully submitted and is under review.");
        return "redirect:/customer/home"; // Redirect to the customer's home page
    }

    // Helper method to get authenticated customer


    /**
     * Admin view: List all claims for review.
     */
    @GetMapping("/admin/claims")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewAllClaims(Model model) {
        List<Claim> claims = claimService.getAllClaims();
        model.addAttribute("claims", claims);
        return "view-claims"; // Return admin claims view
    }

    /**
     * Admin updates the claim status.
     */
    @PostMapping("/admin/claims/{claimId}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateClaimStatus(@PathVariable Long claimId,
                                    @RequestParam String newStatus,
                                    Model model) {
        // Retrieve the claim
        Optional<Claim> claimOptional = claimService.getClaimById(claimId);
        if (claimOptional.isEmpty()) {
            model.addAttribute("error", "Claim not found!");
            return "redirect:/admin/claims"; // Redirect to admin claims page
        }

        Claim claim = claimOptional.get();

        // Update the claim status
        claim.setClaimStatus(newStatus);
        claimService.saveClaim(claim);

        // Provide success message
        model.addAttribute("message", "Claim status updated successfully.");
        return "redirect:/admin/claims";
    }

    /**
     * Helper method to fetch the authenticated customer.
     */
    private Customers getAuthenticatedCustomer(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
            return customerService.findByEmail(email).orElse(null);
        }
        return null;
    }
}
