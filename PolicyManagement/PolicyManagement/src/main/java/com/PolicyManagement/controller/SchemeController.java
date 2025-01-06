package com.PolicyManagement.controller;

import com.PolicyManagement.model.Admin;
import com.PolicyManagement.model.Scheme;
import com.PolicyManagement.repository.AdminRepo;
import com.PolicyManagement.service.SchemeServiceImplementation;
import org.hibernate.annotations.Array;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/schemes")
public class SchemeController {

    @Autowired
    private SchemeServiceImplementation schemeService;

    @Autowired
    private AdminRepo adminRepo;
    private static final Logger logger = LoggerFactory.getLogger(SchemeController.class);
    // Display the form for creating a new scheme
    @GetMapping("/new")
    public String showCreateSchemeForm(Model model) {
        model.addAttribute("scheme", new Scheme());
        return "create-scheme"; // Thymeleaf template to create a scheme
    }

    // Handle form submission to create a new scheme
    @PostMapping("/save")
    public String createScheme(@ModelAttribute Scheme scheme, RedirectAttributes redirectAttributes) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String adminEmail = user.getUsername();

        // Find the admin by email
        Admin admin = adminRepo.findByEmail(adminEmail);
        scheme.setAdmin(admin);
        schemeService.createScheme(scheme);
        logger.info("Admin with email {} created a new scheme: {}", adminEmail, scheme.getName());
        redirectAttributes.addFlashAttribute("message", "Scheme '" + scheme.getName() + "' created successfully!");
        return "redirect:/admin/schemes/new"; // Redirect to the schemes list page
    }


    @GetMapping("/list-schemes")
    public String listSchemes(Model model) {
        List<Scheme> schemes = schemeService.getAllSchemes(); // Fetch all schemes
        model.addAttribute("schemes", schemes);
        return "list-schemes"; // Thymeleaf template that lists schemes
    }

    // Handle deactivating a scheme
    @PostMapping("/{id}/deactivate")
    public String deactivateScheme(@PathVariable Long id) {
        schemeService.deactivateScheme(id);
        return "redirect:/admin/schemes/list-schemes"; // Redirect to schemes list after deactivation
    }
}
