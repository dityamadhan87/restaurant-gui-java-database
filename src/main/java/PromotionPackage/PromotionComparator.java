/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PromotionPackage;

import OrderPackage.Order;
import java.util.Comparator;

/**
 *
 * @author TUF GAMING
 */
public class PromotionComparator implements Comparator<Promotion>{
    private Order order;

    public PromotionComparator(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
    
    @Override
    public int compare(Promotion o1, Promotion o2) {
        double discount1 = o1.totalDiscount(order);
        double discount2 = o2.totalDiscount(order);

        return Double.compare(discount2, discount1);
    }
}