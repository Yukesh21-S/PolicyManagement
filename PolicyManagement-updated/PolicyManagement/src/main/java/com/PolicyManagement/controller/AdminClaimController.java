package com.PolicyManagement.controller;

import com.PolicyManagement.model.Claim;
import com.PolicyManagement.service.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/claims")
@PreAuthorize("hasRole('ADMIN')") // Ensures all methods require the ADMIN role
public class AdminClaimController {

    @Autowired
    private ClaimService claimService;

    // View all pending claims (renders the HTML page)
    @GetMapping("/pending")
    public String getPendingClaimsPage(Model model) {
        List<Claim> pendingClaims = claimService.getClaimsByStatus("PROCESSING");
        model.addAttribute("pendingClaims", pendingClaims);
        return "pending-claims"; // Corresponds to pending-claims.html
    }

    // Update claim status (handles form submission)
    @PostMapping("/updateStatus/{claimId}")
    public String updateClaimStatus(@PathVariable Long claimId, @RequestParam String status, Model model) {
        Claim claim = claimService.getClaimById(claimId).orElse(null);

        if (claim == null) {
            model.addAttribute("error", "Claim not found!");
            return "redirect:/admin/claims/pending";
        }

        // Validate the status
        if (!status.equalsIgnoreCase("APPROVED") &&
                !status.equalsIgnoreCase("REJECTED") &&
                !status.equalsIgnoreCase("UNDER_PROCESSING")) {
            model.addAttribute("error", "Invalid status! Valid statuses are: APPROVED, REJECTED, UNDER_PROCESSING.");
            return "redirect:/admin/claims/pending";
        }

        // Set the claim status based on the action
        claim.setClaimStatus(status.toUpperCase());

        // Set the corresponding admin action message
        if (status.equalsIgnoreCase("APPROVED")) {
            claim.setAdminActionMessage("Your claim has been approved.");
        } else if (status.equalsIgnoreCase("REJECTED")) {
            claim.setAdminActionMessage("Your claim has been rejected. Please contact support for further assistance.");
        } else if (status.equalsIgnoreCase("UNDER_PROCESSING")) {
            claim.setAdminActionMessage("Your claim is under processing. Please wait for further updates.");
        }

        // Save the updated claim
        claimService.saveClaim(claim);

        // Add success message and redirect to the pending claims page
        model.addAttribute("success", "Claim status updated to: " + status.toUpperCase());
        return "redirect:/admin/claims/pending"; // Redirect to refresh the page and show updated status
    }


}
