package com.PolicyManagement.model;



import jakarta.persistence.*;

@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    private int rating;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customers customer;

    // Getters and Setters
}
