/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package PromotionPackage;

import OrderPackage.Order;
import com.mycompany.restaurant.app.gui.java.CustomerPackage.Customer;

/**
 *
 * @author TUF GAMING
 */
public interface Applicable {
    boolean isCustomerEligible(Customer x);
    boolean isMinimumPriceEligible(Order x);
    boolean isShippingFeeEligible(Order x);
    double totalDiscount(Order x);
}