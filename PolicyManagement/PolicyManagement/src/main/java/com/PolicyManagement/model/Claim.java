package com.PolicyManagement.model;


import jakarta.persistence.*;

@Entity
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long claimId;

    private String description;
    private Double amount;
    private String status;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @ManyToOne
    @JoinColumn(name = "policy_id")
    private Policy policy;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customers customer;


    // Getters and Setters
}
