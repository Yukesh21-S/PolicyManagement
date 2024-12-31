package com.PolicyManagement.model;

import jakarta.persistence.*;


@Entity
public class Admin {

    @Id
    private Long id;  // The primary key field

    // other fields like name, email, etc.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // other getter and setter methods
}
