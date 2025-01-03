package com.PolicyManagement.controller;

import com.PolicyManagement.model.Scheme;
import com.PolicyManagement.service.SchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/schemes")
public class SchemeController {

    @Autowired
    private SchemeService schemeService;

    // Display the form for creating a new scheme
    @GetMapping("/new")
    public String showCreateSchemeForm(Model model) {
        model.addAttribute("scheme", new Scheme());
        return "create-scheme";
    }

    // Handle form submission to create a new scheme
    @PostMapping
    public String createScheme(@ModelAttribute Scheme scheme) {
        schemeService.createScheme(scheme);
        return "redirect:/admin/schemes"; // Redirect to the schemes list page
    }

    // List all schemes
//    @GetMapping
//    public String listSchemes(Model model) {
//        List<Scheme> schemes = schemeService.getAllSchemes(); // Fetch all schemes
//        model.addAttribute("schemes", schemes);
//        return "list-schemes"; // Thymeleaf template that lists schemes
//    }
}
