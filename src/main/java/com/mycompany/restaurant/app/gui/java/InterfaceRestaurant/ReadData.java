/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.restaurant.app.gui.java.InterfaceRestaurant;

/**
 *
 * @author TUF GAMING
 */
public interface ReadData {
    public abstract void loadMenu() throws Exception;
    public abstract void loadCustomer() throws Exception;
    public abstract void loadCart() throws Exception;
    public abstract void loadPromo() throws Exception;
    public abstract void loadHistory() throws Exception;
}
