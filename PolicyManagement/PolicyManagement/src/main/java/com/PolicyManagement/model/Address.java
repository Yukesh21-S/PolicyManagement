package com.PolicyManagement.model;

import jakarta.persistence.*;


@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    private String street;
    private String city;
    private String state;
    private String country;

    @OneToOne
    @JoinColumn(name = "customer_id") // Foreign key column to link with Customer
    private Customers customers;

    public Address() {
    }

    public Address(Long addressId, String street, String city, String state, String country, Customers customer) {
        this.addressId = addressId;
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        this.customers = customer;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Customers getCustomer() {
        return customers;
    }

    public void setCustomer(Customers customer) {
        this.customers = customers;
    }
}


