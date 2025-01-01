package com.PolicyManagement.model;



import jakarta.persistence.*;
import java.util.List;

@Entity
public class Scheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long schemeId;

    private String name;
    private String description;
    private String eligibility;
    private String benefits;
    private String status;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @OneToMany(mappedBy = "scheme", cascade = CascadeType.ALL)
    private List<Policy> policies;

    // Getters and Setters
}
