package com.PolicyManagement.controller;

import com.PolicyManagement.model.*;
import com.PolicyManagement.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customer")
public class CustomerController {
@Autowired
private FeedBackService feedBackService;
    @Autowired
    private CustomerService customerService;
@Autowired
private PaymentService paymentService;
    @Autowired
    private PolicyService policyService;

    @Autowired
    private ClaimService claimService;

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    // Show registration form

    @GetMapping("/update")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String showUpdateForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("error", "User not authenticated!");
            return "redirect:/customer/home";
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            Optional<Customers> customer = customerService.findByEmail(username);
            if (customer.isPresent()) {
                model.addAttribute("customer", customer.get());
                return "updateCustomer"; // Template for updating customer details
            } else {
                model.addAttribute("error", "Customer not found!");
            }
        } else {
            model.addAttribute("error", "Invalid user details!");
        }
        return "redirect:/customer/home";
    }

    // Update customer details
    @PostMapping("/update")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String updateCustomer(@ModelAttribute Customers registrationDto, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("error", "User not authenticated!");
            return "redirect:/customer/home";
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            Optional<Customers> customerOptional = customerService.findByEmail(username);
            if (customerOptional.isPresent()) {
                Customers existingCustomer = customerOptional.get();
                long customerId = existingCustomer.getCustomerId();
                customerService.update(customerId, registrationDto); // Perform update
                model.addAttribute("message", "Customer details successfully updated!");
                return "redirect:/customer/home?updateSuccess";
            } else {
                model.addAttribute("error", "Customer not found!");
                return "redirect:/customer/home";
            }
        } else {
            model.addAttribute("error", "Invalid user details!");
            return "redirect:/customer/home";
        }
    }



    // Find customer details
    @GetMapping("/find")
    public String getCustomerDetails(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("error", "User not authenticated!");
            return "customerDetails"; // Ensure this template exists
        }

        Object principal = authentication.getPrincipal();
        logger.info("Authenticated user principal: {}", principal);

        // Assuming you have a method to get customer details by username
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            Optional<Customers> customer = customerService.findByEmail(username);
            if (customer.isPresent()) {
                model.addAttribute("customer", customer.get());
            } else {
                model.addAttribute("error", "Customer not found!");
            }
        } else {
            model.addAttribute("error", "Invalid user details!");
        }
        return "customerDetails";
    }

    // Delete customer by ID
    // Show confirmation form for deletion
    @GetMapping("/delete-confirmation")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String showDeleteConfirmation(Model model) {
        // Get the authenticated user's details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("error", "User not authenticated!");
            return "redirect:/customer/home?error";
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();

            // Fetch the customerId from the database
            Long customerId = customerService.getCustomerIdByUsername(username);
            if (customerId == null) {
                model.addAttribute("error", "Customer not found!");
                return "redirect:/customer/home?error";
            }

            model.addAttribute("customerId", customerId);
            return "delete-confirmation"; // Ensure this template exists
        } else {
            model.addAttribute("error", "Invalid user details!");
            return "redirect:/customer/home?error";
        }
    }

    // Delete customer with password confirmation
    @PostMapping("/delete")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String deleteCustomer(@RequestParam("password") String password, Model model) {
        // Get the authenticated user's details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("error", "User not authenticated!");
            return "redirect:/customer/home?error";
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();

            // Fetch the customerId from the database
            Long customerId = customerService.getCustomerIdByUsername(username);
            if (customerId == null) {
                model.addAttribute("error", "Customer not found!");
                return "redirect:/customer/home?error";
            }

            // Verify the password
            boolean isPasswordValid = customerService.verifyPassword(username, password);
            if (isPasswordValid) {
                // Perform the deletion
                String message = customerService.delete(customerId);
                model.addAttribute("message", message);
                return "redirect:/login?deleteSuccess"; // Redirect after successful deletion
            } else {
                model.addAttribute("error", "Incorrect password!");
                return "redirect:/customer/home?error";
            }
        } else {
            model.addAttribute("error", "Invalid user details!");
            return "redirect:/customer/home?error";
        }
    }

//  //   Show all policies
//    @GetMapping("/view-policy")
//    public String getAllPolicies(Model model) {
//        List<Policy> policies = policyService.getAllPolicies();
//        model.addAttribute("policies", policies);
//        return "view-policy"; // Ensure this template exists
//    }

    // Home page
    @GetMapping("/home")
    public String home() {
        return "home"; // Ensure this template exists
    }
    // Controller: Fetching all policies and displaying them
    @GetMapping("/view-policy")
    public String getAllPolicies(Model model) {
        List<Policy> policies = policyService.getAllPolicies();
        model.addAttribute("policies", policies);
        return "view-policy"; // The template that displays all policies
    }
    @PostMapping("/apply-policy/{policyId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String applyForPolicy(@PathVariable("policyId") Long policyId,
                                 @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
                                 Model model, Authentication authentication) {

        // Retrieve the policy by ID
        Optional<Policy> policyOptional = Optional.ofNullable(policyService.getPolicyById(policyId));
        Customers loggedInCustomer = getAuthenticatedCustomer(authentication);

        // Check if the user is authenticated
        if (loggedInCustomer == null) {
            model.addAttribute("error", "User not authenticated!");
            return "redirect:/customer/home";
        }

        // Check if the policy exists
        if (policyOptional.isEmpty()) {
            model.addAttribute("error", "Policy not found!");
            return "redirect:/customer/home";
        }

        Policy policy = policyOptional.get();

        // Check if the customer has already applied for the policy
        if (loggedInCustomer.getPolicies().stream()
                .anyMatch(existingPolicy -> existingPolicy.getPolicyId().equals(policy.getPolicyId()))) {
            model.addAttribute("error", "You have already applied for this policy.");
            model.addAttribute("policy", policy);
            return "policy-details";
        }

        // Process payment and apply the policy
        paymentMethod = (paymentMethod == null || paymentMethod.isEmpty()) ? "CARD" : paymentMethod;

        if ("CARD".equalsIgnoreCase(paymentMethod)) {
            // Redirect to a payment gateway or processing page
            model.addAttribute("policyId", policyId);
            model.addAttribute("paymentAmount", policy.getPremiumAmount());
            return "redirect:/customer/payment/card?policyId=" + policyId;// Adjust the URL as per your payment processing logic
        }

        // Proceed with applying the policy for other payment methods
        Double paymentAmount = policy.getPremiumAmount();

        Payment payment = new Payment();
        payment.setAmount(paymentAmount);
        payment.setMethod(paymentMethod);
        payment.setDate(new Date());
        payment.setPolicy(policy);

        // Save the payment
        paymentService.savePayment(payment);

        // Add the policy to the customer's list of policies
        loggedInCustomer.getPolicies().add(policy);
        policy.getCustomers().add(loggedInCustomer); // Adding customer to the policy's list of customers
        policy.setPolicyStatus(PolicyStatus.APPLIED);
        policy.setPaymentStatus(PaymentStatus.PAID);

        // Save the policy (the customer-policy relationship is saved automatically through cascading)
        policyService.savePolicy(policy);

        model.addAttribute("message", "Policy applied and payment successfully made!");
        return "redirect:/customer/home";
    }

    private Customers getAuthenticatedCustomer(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
            return customerService.findByEmail(email).orElse(null); // Fetch customer by email
        }
        return null;
    }

    @GetMapping("/my-policies")
    public String viewAppliedPolicies(Authentication authentication, Model model) {
        // Get the logged-in customer
        Customers loggedInCustomer = getAuthenticatedCustomer(authentication);

        if (loggedInCustomer == null) {
            model.addAttribute("error", "User not authenticated!");
            return "redirect:/login";
        }

        // Fetch the policies applied by the customer
        List<Policy> appliedPolicies = policyService.getPoliciesByCustomer(loggedInCustomer);

        model.addAttribute("policies", appliedPolicies);
        return "my-policies"; // Return to the "my-policies" view
    }
    @GetMapping("/apply-policy/{policyId}")
    public String getPolicyDetails(@PathVariable("policyId") Long policyId, Model model) {
        Policy policy = policyService.getPolicyById(policyId);
        List<Feedback> feedbackList = feedBackService.findAllByPolicyId(policyId);

        // Add attributes to the model
        //model.addAttribute("policy", policy);
        model.addAttribute("policy", policy);
        model.addAttribute("feedback", feedbackList);

        return "policy-details"; // This view will display the details of the selected policy
    }
    @GetMapping("/payment/card")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String showCardPaymentForm(@RequestParam("policyId") Long policyId, Model model) {
        Policy policy = policyService.getPolicyById(policyId);
        if (policy == null) {
            model.addAttribute("error", "Policy not found!");
            return "redirect:/customer/home";
        }

        model.addAttribute("policy", policy);
        return "card-payment"; // Render the card-payment.html page
    }

    @PostMapping("/payment/card")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String processCardPayment(@RequestParam("policyId") Long policyId,
                                     @RequestParam("cardNumber") String cardNumber,
                                     @RequestParam("expiryDate") String expiryDate,
                                     @RequestParam("cvv") String cvv,
                                     Model model, Authentication authentication) {

        Policy policy = policyService.getPolicyById(policyId);
        Customers loggedInCustomer = getAuthenticatedCustomer(authentication);

        if (policy == null || loggedInCustomer == null) {
            model.addAttribute("error", "Invalid request!");
            return "redirect:/home";
        }

        // Validate card details (you can expand this with real validation logic)
        if (cardNumber.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty()) {
            model.addAttribute("error", "Invalid card details!");
            model.addAttribute("policy", policy);
            return "card-payment";
        }

        // Process the payment (simulated)
        Double paymentAmount = policy.getPremiumAmount();

        Payment payment = new Payment();
        payment.setAmount(paymentAmount);
        payment.setMethod("CARD");
        payment.setDate(new Date());
        payment.setPolicy(policy);
        payment.setCustomer(loggedInCustomer);

        // Save the payment
        paymentService.savePayment(payment);

        // Link the policy to the customer
        loggedInCustomer.getPolicies().add(policy);
        policy.getCustomers().add(loggedInCustomer);
        policy.setPaymentStatus(PaymentStatus.PAID);

        // Save the policy
        policyService.savePolicy(policy);

        model.addAttribute("message", "Payment successfully processed with card!");
        return "redirect:/home";
    }



    @GetMapping("/claimed-customers/{policyId}")
    public String viewClaimStatusForCustomer(@PathVariable Long policyId, Principal principal, Model model) {
        // Initialize the logger
        Logger logger = LoggerFactory.getLogger(getClass());

        // Fetch the policy by ID
        Policy policy = policyService.getPolicyById(policyId);

        if (policy == null) {
            logger.error("Policy with ID {} not found.", policyId); // Log if policy not found
            model.addAttribute("error", "Policy not found!");
            return "redirect:/customer/dashboard"; // Redirect to a customer dashboard or profile page
        }

        // Fetch the claim associated with the policy and the logged-in customer
        Claim claim = claimService.getClaimByPolicyAndCustomer(policyId, principal.getName()).orElse(null);

        if (claim == null) {
            logger.error("Claim for policy ID {} not found for customer with email {}.", policyId, principal.getName());
            model.addAttribute("error", "Claim not found for this policy.");
            return "redirect:/customer/dashboard"; // Redirect if claim not found for the customer
        }

        // Log when the claim is successfully found and viewed by the authorized customer
        logger.info("Customer {} is viewing claim status for policy {}", principal.getName(), policy.getName());

        model.addAttribute("policyName", policy.getName());
        model.addAttribute("claimStatus", claim.getClaimStatus());
        model.addAttribute("adminActionMessage", claim.getAdminActionMessage());
        model.addAttribute("claimReason", claim.getClaimReason());
        model.addAttribute("claimDate", claim.getClaimDate());

        return "view-claim-status"; // Thymeleaf template for customer to view their claim status
    }


}

