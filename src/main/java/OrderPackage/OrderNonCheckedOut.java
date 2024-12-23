/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OrderPackage;

import Menu.Menu;
import PromotionPackage.CashbackPromo;
import PromotionPackage.FreeShippingPromo;
import PromotionPackage.PercentOffPromo;
import PromotionPackage.Promotion;
import com.mycompany.restaurant.app.gui.java.Admin.Admin;
import com.mycompany.restaurant.app.gui.java.CustomerPackage.Customer;
import com.mycompany.restaurant.app.gui.java.CustomerPackage.Guest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author TUF GAMING
 */
public class OrderNonCheckedOut extends Order{
    
    public OrderNonCheckedOut(Customer customer, Set<OrderItem> item, Promotion appliedPromo) {
        super(customer,item,appliedPromo);
    }
    
    public OrderNonCheckedOut(Customer customer){
        super(customer);
    }
    
    public OrderNonCheckedOut(){
        super();
    }
    
    public void makeOrder(String input) throws Exception {
        final String INSERT_QUERY = "insert into Cart(customerID,menuID,quantity) values (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                PreparedStatement insertData = connection.prepareStatement(INSERT_QUERY);) {
            String[] orderDataUnit = input.split("\\|");
            String customerId = orderDataUnit[0];
            String menuId = orderDataUnit[1];
            String quantity = orderDataUnit[2];

            Customer customer = getAdmin().getListCustomer().stream().filter(p -> p.getCustomerId().equals(customerId))
                    .findFirst().orElse(new Guest(customerId));
            Menu menu = getAdmin().getListMenu().stream().filter(m -> m.getMenuId().equals(menuId)).findFirst()
                    .orElse(new Menu(menuId));
            OrderItem item = new OrderItem(menu, Integer.parseInt(quantity));

            if (!getAdmin().getListCustomer().contains(customer)
                    || !getAdmin().getListMenu().contains(menu)) {
                throw new Exception("ADD_TO_CART FAILED: NON EXISTENT CUSTOMER OR MENU");
            }
            
            Order existingOrder = null;
            for (Order order : getOrderList()) {
                if (order.getCustomer().equals(customer)) {
                    existingOrder = order;
                    break;
                }
            }

            if (existingOrder == null) {
                existingOrder = new OrderNonCheckedOut(customer, new LinkedHashSet<>(), null);
            }
            
            existingOrder.getItem().add(item);
            getOrderList().add(existingOrder);

            insertData.setString(1, customerId);
            insertData.setString(2, menuId);
            insertData.setString(3, quantity);
            insertData.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(OrderNonCheckedOut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void removeItem(String input){
        final String REMOVE_QUERY = "DELETE FROM Cart WHERE customerID = ? and menuID = ?";
        String[] orderDataUnit = input.split("\\|");
        String customerId = orderDataUnit[0];
        String menuId = orderDataUnit[1];
        Customer customer = new Guest(customerId);
        Menu menu = new Menu(menuId);
        OrderItem item = new OrderItem(menu);
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                PreparedStatement removeData = connection.prepareStatement(REMOVE_QUERY);) {
            
            Order existingOrder = null;
            for (Order order : getOrderList()) {
                if (order.getCustomer().equals(customer)) {
                    existingOrder = order;
                    break;
                }
            }
            
            existingOrder.getItem().remove(item);
            
            removeData.setString(1, customerId);  
            removeData.setString(2, menuId);
            removeData.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void updateItem(String input){
        final String UPDATE_QUERY = "UPDATE Cart "
                + "Set quantity = ?"
                + "WHERE customerID = ? and menuID = ?";
        
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                PreparedStatement updateData = connection.prepareStatement(UPDATE_QUERY);) {
            
            String[] orderDataUnit = input.split("\\|");
            String customerId = orderDataUnit[0];
            String menuId = orderDataUnit[1];
            String quantity = orderDataUnit[2];
            
            Customer customer = getAdmin().getListCustomer().stream().filter(p -> p.getCustomerId().equals(customerId))
                    .findFirst().orElse(new Guest(customerId));
            Menu menu = getAdmin().getListMenu().stream().filter(m -> m.getMenuId().equals(menuId)).findFirst()
                    .orElse(new Menu(menuId));
            OrderItem item = new OrderItem(menu, Integer.parseInt(quantity));

            Order existingOrder = null;
            for (Order order : getOrderList()) {
                if (order.getCustomer().equals(customer)) {
                    existingOrder = order;
                    break;
                }
            }

            if (existingOrder == null) {
                existingOrder = new OrderNonCheckedOut(customer, new LinkedHashSet<>(),null);
            }
            
            updateData.setInt(1, Integer.parseInt(quantity));
            updateData.setString(2, customerId);
            updateData.setString(3, menuId);
            updateData.executeUpdate();
            
            existingOrder.getItem().remove(item);
            existingOrder.getItem().add(item);
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void applyPromo(String input) throws Exception{
        String[] promoCustomerUnit = input.split("\\|");
        String customerId = promoCustomerUnit[0];
        String promoCode = promoCustomerUnit[1];
        
        Customer customer = new Guest(customerId);
        Promotion promo = new PercentOffPromo(promoCode);

        for (Customer getCustomer : getAdmin().getListCustomer()) {
            if (getCustomer.getCustomerId().equals(customerId)) {
                customer = getCustomer;
                break;
            }
        }

        for (Promotion promotion : getAdmin().getListPromo()) {
            if (promotion.getPromoCode().equals(promoCode)) {
                promo = promotion;
                break;
            }
        }
        
        if (!promo.isCustomerEligible(customer)) {
            throw new Exception("APPLY_PROMO FAILED: CUSTOMER IS NOT ELIGIBLE");
        }
        
        Order existingOrder = null;
        for (Order order : getOrderList()) {
            if (order.getCustomer().equals(customer)) {
                existingOrder = order;
                break;
            }
        }

        if (promo instanceof PercentOffPromo || promo instanceof CashbackPromo) {
            if (!promo.isMinimumPriceEligible(existingOrder)) {
                throw new Exception("APPLY_PROMO FAILED: MINIMUM PRICE NOT MET");
            }
        }
        
        if (promo instanceof FreeShippingPromo) {
            if (!promo.isShippingFeeEligible(existingOrder)) {
                throw new Exception("APPLY_PROMO FAILED: SHIPPING FEE NOT MET");
            }
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate expiredDate = LocalDate.parse(promo.getEndDate(), formatter);
        LocalDate startDate = LocalDate.parse(promo.getStartDate(), formatter);

        if (startDate.isAfter(LocalDate.now())) {
            throw new Exception("APPLY_PROMO FAILED: PROMO " + promo.getPromoCode() + " NOT YET STARTED");
        }

        if (LocalDate.now().isAfter(expiredDate)) {
            throw new Exception("APPLY_PROMO FAILED: PROMO " + promo.getPromoCode() + " HAS EXPIRED");
        }
        
        existingOrder.setAppliedPromo(promo);
    }
    
    @Override
    public String printDetails(String input){
        StringBuilder orderDetails = new StringBuilder();
        String customerId = input;
        
        Customer customer = getAdmin().getListCustomer().stream().filter(p -> p.getCustomerId().equals(customerId))
                    .findFirst().orElse(new Guest(customerId));
        
        for(Order order : getOrderList()){
            if(order.getCustomer().equals(customer)){
                order.setTotalDiscount(0);
                orderDetails.append("Customer ID: ").append(customer.getCustomerId());
                orderDetails.append("\nName: ").append(customer.getFullName());
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
        if(!(other instanceof OrderNonCheckedOut)) return false;
        OrderNonCheckedOut o = (OrderNonCheckedOut) other;
        return getCustomer().equals(o.getCustomer());
    }

    @Override
    public int hashCodeSpecific() {
        return getCustomer().hashCode();
    }
}