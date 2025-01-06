package com.PolicyManagement.model;



import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private Double amount;
    private String method; // Card, UPI, Bank Transfer
    private Date date;

    @ManyToOne
    @JoinColumn(name = "policy_id")
    private Policy policy;

    // Getters and Setters
}
