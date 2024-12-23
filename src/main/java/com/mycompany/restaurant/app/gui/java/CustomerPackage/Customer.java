/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.restaurant.app.gui.java.CustomerPackage;

/**
 *
 * @author TUF GAMING
 */
public abstract class Customer {
    String customerType;
    private String customerId;
    private String firstName;
    private String lastName;
    private double openingBalance;
    String memberDate;

    public Customer(String customerId, String firstName, String lastName,
            float openingBalance) {
        this.customerType = "-";
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.openingBalance = openingBalance;
        this.memberDate = "-";
    }

    public Customer(String customerId){
        this.customerId = customerId;
    
    }
    public Customer(){
    }

    public String getCustomerType() {
        return customerType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public double getOpeningBalance() {
        return openingBalance;
    }

    public String getMemberDate() {
        return memberDate;
    }

    public void setOpeningBalance(double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public abstract long longTimeMember();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !(obj instanceof Customer))
            return false;
        Customer other = (Customer) obj;
        return customerId != null && customerId.equals(other.customerId);
    }
}