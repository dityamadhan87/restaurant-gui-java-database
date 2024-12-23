/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.restaurant.app.gui.java.CustomerPackage;

/**
 *
 * @author TUF GAMING
 */
public class Guest extends Customer{
    public Guest(String guestId){
        super(guestId);
    }
    public Guest(String guestId, String firstName, String lastName, float openingBalance){
        super(guestId, firstName, lastName, openingBalance);
        this.customerType = "GUEST";
        this.memberDate = "-";
    }

    public Guest(){
        super();
    }
    
    @Override
    public long longTimeMember() {
        throw new UnsupportedOperationException("Guest not becomes member");
    }
}