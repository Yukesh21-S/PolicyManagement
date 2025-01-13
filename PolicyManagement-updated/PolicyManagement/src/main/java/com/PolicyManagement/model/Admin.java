package com.PolicyManagement.model;



import jakarta.persistence.*;
import java.util.List;

@Entity
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;

    private String name;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role=Role.ADMIN;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Scheme> schemes;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Policy> policies;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Claim> claims;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Customers> customers;

    public Admin() {

    }

    public Admin(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Scheme> getSchemes() {
        return schemes;
    }

    public void setSchemes(List<Scheme> schemes) {
        this.schemes = schemes;
    }

    public List<Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(List<Policy> policies) {
        this.policies = policies;
    }

    public List<Claim> getClaims() {
        return claims;
    }

    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }

    public List<Customers> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customers> customers) {
        this.customers = customers;
    }

    public Admin(Long adminId, String name, String email,Role role, String password, List<Scheme> schemes, List<Policy> policies, List<Claim> claims, List<Customers> customers) {
        this.adminId = adminId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
        this.schemes = schemes;
        this.policies = policies;
        this.claims = claims;
        this.customers = customers;
    }
}

