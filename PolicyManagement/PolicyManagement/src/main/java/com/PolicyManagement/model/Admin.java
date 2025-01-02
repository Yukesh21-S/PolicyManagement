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

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Scheme> schemes;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Policy> policies;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Claim> claims;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Customers> customers;

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
}
