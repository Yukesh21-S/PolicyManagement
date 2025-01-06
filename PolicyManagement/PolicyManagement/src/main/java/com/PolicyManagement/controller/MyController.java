package com.PolicyManagement.controller;

import com.PolicyManagement.model.Scheme;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MyController {


    @GetMapping  ("/login")
    public String login() {
        return "customer-login";
    }
    @PostMapping("/login")
    public String home() {
        return "home";
    }
    @GetMapping("/admin/login")
    public String adminLogin()
    {
        return "admin-login";

    }
    @GetMapping("/admin/admin-home")
    public String adminHome()
    {
        return "admin-home";
    }


}




