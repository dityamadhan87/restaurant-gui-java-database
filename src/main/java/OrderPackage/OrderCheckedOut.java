/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OrderPackage;

import PromotionPackage.CashbackPromo;
import PromotionPackage.FreeShippingPromo;
import PromotionPackage.PercentOffPromo;
import PromotionPackage.Promotion;
import com.mycompany.restaurant.app.gui.java.CustomerPackage.Customer;
import com.mycompany.restaurant.app.gui.java.CustomerPackage.Guest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TUF GAMING
 */
public class OrderCheckedOut extends Order{
    
    public OrderCheckedOut(int orderNumber, String orderDate, Customer customer, Set<OrderItem> item, Promotion appliedPromo){
        super(customer,item,appliedPromo);
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
    }
    
    public OrderCheckedOut(){
        super();
    }
    
    public void checkOut(String input) throws Exception{
        String customerId = input;
        
        Customer customer = new Guest(customerId);

        for (Customer getCustomer : getAdmin().getListCustomer()) {
            if (getCustomer.equals(customer)) {
                customer = getCustomer;
                break;
            }
        }
        
        Order existingOrder = null;
        for (Order order : getOrderList()) {
            if (order.getCustomer().equals(customer)) {
                existingOrder = order;
                break;
            }
        }

        if (existingOrder == null) {
            throw new Exception("CHECK_OUT FAILED: CUSTOMER HAVE NOT ORDERED");
        }
        
        if (customer.getOpeningBalance() < existingOrder.getTotalPrice()) {
            throw new Exception("CHECK_OUT FAILED: " + customerId + " " + customer.getFullName() + " INSUFFICIENT BALANCE");
        }
        
        final String INSERT_DATAORDER_QUERY = "INSERT INTO DataOrder (orderNumber, orderDate) VALUES (?, ?)";
        final String INSERT_CUSTOMERORDER_QUERY = "INSERT INTO CustomerOrder (orderNumber, customerID, menuID, quantity, promoCode) VALUES (?, ?, ?, ?, ?)";

        int orderNumber = 0;
        final String SELECT_QUERY = "SELECT * FROM CustomerOrderDetails";

        try (
                Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SELECT_QUERY)) {

            while (resultSet.next()) {
                orderNumber = resultSet.getInt("orderNumber");
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderCheckedOut.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        orderNumber += 1;
        
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                PreparedStatement insertDataOrder = connection.prepareStatement(INSERT_DATAORDER_QUERY);
                PreparedStatement insertCustomerOrder = connection.prepareStatement(INSERT_CUSTOMERORDER_QUERY)) {
 
            String orderDate = LocalDate.now().toString(); 

            insertDataOrder.setInt(1, orderNumber);
            insertDataOrder.setString(2, orderDate);
            insertDataOrder.executeUpdate();

            for (OrderItem item : existingOrder.getItem()) {
                String menuID = item.getMenu().getMenuId(); 
                int quantity = item.getQuantity(); 
                String promoCode = existingOrder.getAppliedPromo() != null ? existingOrder.getAppliedPromo().getPromoCode() : null;

                insertCustomerOrder.setInt(1, orderNumber);
                insertCustomerOrder.setString(2, customerId);
                insertCustomerOrder.setString(3, menuID);
                insertCustomerOrder.setInt(4, quantity);
                insertCustomerOrder.setString(5, promoCode);
                insertCustomerOrder.executeUpdate();
            }

        } catch (SQLException ex) { 
            Logger.getLogger(OrderCheckedOut.class.getName()).log(Level.SEVERE, null, ex);
        }

        double remainingBalance = customer.getOpeningBalance() - existingOrder.getTotalPrice();
        customer.setOpeningBalance(remainingBalance);
        
        Order orderCheckedOut = new OrderCheckedOut(orderNumber, LocalDate.now().toString(), customer, existingOrder.getItem(), existingOrder.getAppliedPromo());
        getHistoryList().add(orderCheckedOut);
        getOrderList().remove(new OrderNonCheckedOut(customer));
    }
    
    @Override
    public String printDetails(String input) {
        StringBuilder orderDetails = new StringBuilder();
        String customerId = input;
        
        Customer customer = getAdmin().getListCustomer().stream().filter(p -> p.getCustomerId().equals(customerId))
                    .findFirst().orElse(new Guest(customerId));
        
        for(Order order : getHistoryList()){
            if(order.getCustomer().getCustomerId().equals(customerId)){
                order.setTotalDiscount(0);
                orderDetails.append("Customer ID: ").append(customer.getCustomerId());
                orderDetails.append("\nName: ").append(customer.getFullName());
                orderDetails.append("\nNomor Pesanan: ").append(order.orderNumber);
                orderDetails.append("\nTanggal Pesanan: ").append(order.getOrderDate());
                orderDetails.append(String.format("\n%3s | %-20s | %3s | %8s \n", "No", "Menu", "Qty", "Subtotal"));
                orderDetails.append("=".repeat(50)).append("\n");
                
                int i = 1;
                for (OrderItem item : order.getItem()) {
                    orderDetails.append(String.format("%c %-3d %-23s %-5d %.0f\n", ' ',
                            i++, item.getMenu().getMenuName(), item.getQuantity(),
                            item.getSubPrice()));
                }
                orderDetails.append("=".repeat(50)).append("\n");
                orderDetails.append(String.format("%-30s %-8c %.0f\n", "Total", ':', order.getSubTotalFoodCost()));
                
                if (order.getAppliedPromo() != null && order.getAppliedPromo() instanceof PercentOffPromo) {
                    Promotion customerPromo = order.getAppliedPromo();
                    double totalDiscount = customerPromo.totalDiscount(order);
                    order.setTotalDiscount(totalDiscount);
                    orderDetails.append(String.format("%-6s %-19s %-8c %.0f\n", "Promo:", customerPromo.getPromoCode(), ':',
                            totalDiscount));
                }
                
                orderDetails.append(String.format("%-22s %-8c %.0f\n", "Shipping cost", ':', order.getShippingCost()));
                
                if (order.getAppliedPromo() != null && order.getAppliedPromo() instanceof FreeShippingPromo) {
                    Promotion customerPromo = order.getAppliedPromo();
                    double totalDiscount = customerPromo.totalDiscount(order);
                    order.setTotalDiscount(totalDiscount);
                    orderDetails.append(String.format("%-6s %-19s %-8c %.0f\n", "Promo:", customerPromo.getPromoCode(), ':',
                            totalDiscount));
                }
                
                orderDetails.append("=".repeat(50)).append("\n");
                orderDetails.append(String.format("%-30s %-8c %.0f\n", "Total", ':', order.getTotalPrice()));
                
                if (order.getAppliedPromo() != null && order.getAppliedPromo() instanceof CashbackPromo) {
                    Promotion customerPromo = order.getAppliedPromo();
                    double totalDiscount = customerPromo.totalDiscount(order);
                    order.setTotalDiscount(totalDiscount);
                    orderDetails.append(String.format("%-6s %-19s %-8c %.0f\n", "Promo:", customerPromo.getPromoCode(), ':',
                            totalDiscount));
                }
                
                orderDetails.append(String.format("%-26s %-8c %s\n", "Balance", ':', customer.getOpeningBalance()));
                break;
            }
        }
        return orderDetails.toString();
    }

    @Override
    public boolean equalsSpecific(Order other) {
        if (!(other instanceof OrderCheckedOut)) return false;
        OrderCheckedOut o = (OrderCheckedOut) other;
        return this.orderNumber == o.orderNumber;
    }

    @Override
    public int hashCodeSpecific() {
        return Integer.hashCode(orderNumber);
    }
}