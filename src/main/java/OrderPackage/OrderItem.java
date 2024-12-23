/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OrderPackage;

import Menu.Menu;
import java.util.Objects;

/**
 *
 * @author TUF GAMING
 */
public class OrderItem {
    private Menu menu;
    private int quantity;
    private float subPrice;

    public OrderItem(Menu menu, int quantity) {
        this.menu = menu;
        this.quantity = quantity;
    }
    
    public OrderItem(Menu menu){
        this.menu = menu;
    }

    public Menu getMenu() {
        return menu;
    }

    public float getSubPrice(){
        subPrice = menu.getMenuPrice() * quantity;
        return subPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.menu);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OrderItem other = (OrderItem) obj;
        return Objects.equals(this.menu, other.menu);
    }
}