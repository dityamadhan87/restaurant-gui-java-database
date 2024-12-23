/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OrderPackage;

import Menu.Menu;
import PromotionPackage.PercentOffPromo;
import PromotionPackage.Promotion;
import com.mycompany.restaurant.app.gui.java.Admin.Admin;
import com.mycompany.restaurant.app.gui.java.CustomerPackage.Customer;
import com.mycompany.restaurant.app.gui.java.CustomerPackage.Guest;
import com.mycompany.restaurant.app.gui.java.InterfaceRestaurant.ReadData;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TUF GAMING
 */
public abstract class Order implements ReadData{
    private Set<Order> orderList = new LinkedHashSet<>();
    private Set<Order> historyList = new LinkedHashSet<>();
    private Admin admin = new Admin();
    
    private Customer customer;
    private Set<OrderItem> item;
    private Promotion appliedPromo;
    
    int orderNumber;
    String orderDate;
    private float subTotalFoodCost;
    private float shippingCost = 15000;
    private double totalDiscount;
    private double totalPrice;
    
    final String DATABASE_URL = "database";
    
    public Order(Customer customer, Set<OrderItem> item, Promotion appliedPromo) {
        this.orderNumber = 0;
        this.orderDate = "-";
        this.customer = customer;
        this.item = item;
        this.appliedPromo = appliedPromo;
    }
    
    public Order(Customer customer){
        this.customer = customer;
    }
    
    public Order(){
        
    }

    public Set<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(Set<Order> orderList) {
        this.orderList = orderList;
    }

    public Set<Order> getHistoryList() {
        return historyList;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Set<OrderItem> getItem() {
        return item;
    }

    public Promotion getAppliedPromo() {
        return appliedPromo;
    }

    public void setAppliedPromo(Promotion appliedPromo) {
        this.appliedPromo = appliedPromo;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public float getSubTotalFoodCost() {
        subTotalFoodCost = 0;
        for (OrderItem orderItem : item) {
            subTotalFoodCost += orderItem.getMenu().getMenuPrice() * orderItem.getQuantity();
        }
        return subTotalFoodCost;
    }

    public float getShippingCost() {
        return shippingCost;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public double getTotalPrice() {
        totalPrice = subTotalFoodCost + shippingCost - totalDiscount;
        return totalPrice;
    }
    
    @Override
    public void loadMenu() throws Exception {
        admin.loadMenu();
    }

    @Override
    public void loadCustomer() throws Exception {
        admin.loadCustomer();
    }

    @Override
    public void loadCart() throws Exception {
        final String SELECT_QUERY = "select * from Cart";
        try (
                Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SELECT_QUERY)) {
            
            while(resultSet.next()){
                String customerId = resultSet.getString("customerID");
                String menuId = resultSet.getString("menuID");
                int quantity = resultSet.getInt("quantity");
                
                Customer customer = admin.getListCustomer().stream().filter(p -> p.getCustomerId().equals(customerId))
                        .findFirst().orElse(new Guest(customerId));
                Menu menu = admin.getListMenu().stream().filter(m -> m.getMenuId().equals(menuId)).findFirst()
                        .orElse(new Menu(menuId));
                OrderItem item = new OrderItem(menu, quantity);
                
                Order existingOrder = null;
                for (Order cart : orderList) {
                    if (cart.getCustomer().equals(customer)) {
                        existingOrder = cart;
                        break;
                    }
                }

                if (existingOrder == null) {
                    existingOrder = new OrderNonCheckedOut(customer, new LinkedHashSet<>(),null);
                }
                
                existingOrder.getItem().add(item);
                orderList.add(existingOrder);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void loadPromo() throws Exception {
        admin.loadPromo();
    }

    @Override
    public void loadHistory() throws Exception {
        final String SELECT_QUERY = "SELECT "
                + "co.orderNumber, "
                + "do.orderDate, "
                + "co.customerID, "
                + "co.menuID, "
                + "co.quantity, "
                + "co.promoCode "
                + "FROM CustomerOrder co JOIN DataOrder do ON co.orderNumber = do.orderNumber";
        try (
                Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SELECT_QUERY)) {
            
            while(resultSet.next()){
                int orderNumber = resultSet.getInt("orderNumber");
                String orderDate = resultSet.getString("orderDate");
                String customerID = resultSet.getString("CustomerID");
                String menuID = resultSet.getString("menuID");
                int quantity = resultSet.getInt("quantity");
                String promoCode = resultSet.getString("promoCode");
                
                Customer customer = admin.getListCustomer().stream().filter(p -> p.getCustomerId().equals(customerID))
                        .findFirst().orElse(new Guest(customerID));
                Menu menu = admin.getListMenu().stream().filter(m -> m.getMenuId().equals(menuID)).findFirst()
                        .orElse(new Menu(menuID));
                OrderItem item = new OrderItem(menu, quantity);
                Promotion promo = new PercentOffPromo(promoCode);
                
                for (Promotion promotion : getAdmin().getListPromo()) {
                    if (promotion.equals(promo)) {
                        promo = promotion;
                        break;
                    }
                }
                
                Order existingOrderCheckedOut = null;
                for (Order orderCheckedOut : historyList) {
                    if (orderCheckedOut.getOrderNumber() == orderNumber) {
                        existingOrderCheckedOut = orderCheckedOut;
                        break;
                    }
                }

                if (existingOrderCheckedOut == null) {
                    existingOrderCheckedOut = new OrderCheckedOut(orderNumber,orderDate,customer, new LinkedHashSet<>(),promo);
                }
                
                existingOrderCheckedOut.getItem().add(item);
                historyList.add(existingOrderCheckedOut);
            }
        }
    }
    
    public abstract boolean equalsSpecific(Order other);
    public abstract int hashCodeSpecific();
    public abstract String printDetails(String input);

    @Override
    public int hashCode() {
        return hashCodeSpecific();
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
        final Order other = (Order) obj;
        return equalsSpecific(other);
    }
}