package com.PolicyManagement.controller;

import com.PolicyManagement.model.Customers;
import com.PolicyManagement.model.Feedback;
import com.PolicyManagement.model.Policy;
import com.PolicyManagement.service.CustomerService;
import com.PolicyManagement.service.FeedBackService;

import com.PolicyManagement.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customer/feedback")
public class FeedbackController {
    @Autowired
    private FeedBackService feedbackService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private CustomerService customerService;

    // Display form to submit feedback for a specific policy
//    @GetMapping("/submit/{policyId}")
//    public String showFeedbackForm(@PathVariable Long policyId, Model model, Principal principal) {
//        String customerEmail = principal.getName(); // Assuming email as username
//        Customers customer = customerService.findByEmail(customerEmail).orElseThrow(
//                () -> new IllegalArgumentException("Customer not found"));
//
//        Policy policy = policyService.getPolicyById(policyId);
//
//        Feedback feedback = new Feedback();
//        feedback.setPolicy(policy);
//        feedback.setCustomer(customer);
//
//        model.addAttribute("feedback", feedback);
//        model.addAttribute("policy", policy);
//        return "submit-feedback";
//    }

    // Handle feedback submission
    @PostMapping("/submit/{policyId}")
    public String submitFeedback(@PathVariable Long policyId,
                                 @RequestParam String comment,
                                 @RequestParam int rating,
                                 Principal principal) {
        // Get the customer based on the logged-in user's email
        String customerEmail = principal.getName();
        Customers customer = customerService.findByEmail(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        // Retrieve the policy by policyId
        Policy policy = policyService.getPolicyById(policyId);

        // Create and save feedback
        Feedback feedback = new Feedback();
        feedback.setCustomer(customer);
        feedback.setPolicy(policy);
        feedback.setComment(comment);
        feedback.setRating(rating);
        feedback.setSubmittedAt(LocalDateTime.now());

        feedbackService.saveFeedback(feedback);

        // Redirect to success page or feedback list
        return "redirect:/customer/feedback/success";
    }


    // Display success page after submitting feedback
    @GetMapping("/success")
    public String feedbackSuccess() {
        return "feedback-success";
    }

    // View feedbacks for a specific policy
//    @GetMapping("/policy/{policyId}")
//    public String viewFeedbacksForPolicy(@PathVariable Long policyId, Model model) {
//        List<Feedback> feedbacks = feedbackService.getFeedbackByPolicyId(policyId);
//        model.addAttribute("feedbacks", feedbacks);
//        model.addAttribute("policy", policyService.getPolicyById(policyId));
//        return "feedback-list";
//    }
//    @GetMapping("/policy/{policyId}")
//    public String viewFeedback(@PathVariable Long policyId, Model model) {
//        // Fetch policy details
//       // Policy policy = policyService.getPolicyById(policyId);
//        // Fetch feedback for the policy
//        List<Feedback> feedbackList = feedbackService.findAllByPolicyId(policyId);
//
//        // Add attributes to the model
//        //model.addAttribute("policy", policy);
//        model.addAttribute("feedbackList", feedbackList);
//
//        return "policy-details"; // Name of the Thymeleaf template
//    }

}
