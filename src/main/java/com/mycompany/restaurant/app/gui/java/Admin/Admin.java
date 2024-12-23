/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.restaurant.app.gui.java.Admin;

/**
 *
 * @author TUF GAMING
 */
import PromotionPackage.FreeShippingPromo;
import PromotionPackage.PercentOffPromo;
import PromotionPackage.CashbackPromo;
import PromotionPackage.Promotion;
import Menu.*;
import com.mycompany.restaurant.app.gui.java.CustomerPackage.*;

import java.util.Set;
import java.util.LinkedHashSet;

import com.mycompany.restaurant.app.gui.java.InterfaceRestaurant.ReadData;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Admin implements ReadData{
    private final Set<Customer> listCustomer = new LinkedHashSet<>();
    private final Set<Menu> listMenu = new LinkedHashSet<>();
    private final Set<Promotion> listPromo = new LinkedHashSet<>();

    final String DATABASE_URL = "database";
    
    public void createMenu(String input) throws Exception{
        final String INSERT_QUERY = "insert into Menu(menuID,menuName,menuPrice) values (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                PreparedStatement insertData = connection.prepareStatement(INSERT_QUERY);) {
            String[] menuDataUnit = input.split("\\|");
            String menuId = menuDataUnit[0].trim();
            String menuName = menuDataUnit[1].trim();
            String menuPrice = menuDataUnit[2].trim();
            
            Menu menu = new Menu(menuId,menuName,Float.parseFloat(menuPrice));
            if(listMenu.contains(menu))
                throw new Exception("CREATE MENU FAILED: " + menuId + " IS EXISTS");
            
            insertData.setString(1, menuId);
            insertData.setString(2, menuName);
            insertData.setString(3, menuPrice);
            insertData.executeUpdate();

            listMenu.add(menu);
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void removeMenu(String input) {
        final String REMOVE_QUERY = "DELETE FROM Menu WHERE menuID = ?";
        Menu menu = new Menu(input);
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                PreparedStatement removeData = connection.prepareStatement(REMOVE_QUERY);) {
            
            removeData.setString(1, input);  
            removeData.executeUpdate();
            listMenu.remove(menu);
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void updateMenu(String input) throws Exception{
        final String UPDATE_QUERY = "UPDATE Menu "
                + "Set menuName = ?, "
                + "menuPrice = ? "
                + "where menuID = ?";
        
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                PreparedStatement updateData = connection.prepareStatement(UPDATE_QUERY);) {
            
            String[] menuDataUnit = input.split("\\|");
            String menuId = menuDataUnit[0].trim();
            String menuName = menuDataUnit[1].trim();
            String menuPrice = menuDataUnit[2].trim();     
            
            Menu menu = new Menu(menuId,menuName,Float.parseFloat(menuPrice));
            
            if(!listMenu.contains(menu))
                throw new Exception("UPDATE MENU FAILED: " + menuName + " NOT EXISTS");
            
            updateData.setString(1, menuName);
            updateData.setFloat(2, Float.parseFloat(menuPrice));
            updateData.setString(3, menuId);
            updateData.executeUpdate();
            
            listMenu.remove(menu);
            listMenu.add(menu);
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void createGuest(String input) throws Exception{
        final String INSERT_QUERY = "insert into Customer(customerType,customerID,customerName,openingBalance) values (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                PreparedStatement insertData = connection.prepareStatement(INSERT_QUERY);) {
            String[] customerDataUnit = input.split("\\|");
            String customerId = customerDataUnit[1].trim();
            String customerName = customerDataUnit[2].trim();
            String openingBalance = customerDataUnit[4].trim();

            String firstName;
            String lastName;

            if (customerName.contains(" ")) {
                firstName = customerName.substring(0, customerName.indexOf(' '));
                lastName = customerName.substring(customerName.indexOf(' ') + 1);
            } else {
                firstName = customerName;
                lastName = "";
            }

            Customer customer = new Guest(customerId, firstName, lastName, Float.parseFloat(openingBalance));
            if(listCustomer.contains(customer))
                throw new Exception("CREATE GUEST FAILED: " + customerId + " IS EXISTS");
            insertData.setString(1, customer.getCustomerType());
            insertData.setString(2, customerId);
            insertData.setString(3, customerName);
            insertData.setFloat(4, Float.parseFloat(openingBalance));
            insertData.executeUpdate();

            listCustomer.add(customer);
            
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void removeCustomer(String input) {
        final String REMOVE_QUERY = "DELETE FROM Customer WHERE customerID = ?";
        Customer customer = new Guest(input);
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                PreparedStatement removeData = connection.prepareStatement(REMOVE_QUERY);) {
            
            removeData.setString(1, input);  
            removeData.executeUpdate();
            listCustomer.remove(customer);
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void updateCustomer(String input) throws Exception{
        final String UPDATE_QUERY = "UPDATE Customer "
                + "Set customerType = ?, "
                + "customerName = ?, "
                + "memberDate = ?,"
                + "openingBalance = openingBalance + ?"
                + "where customerID = ?";
        
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                PreparedStatement updateData = connection.prepareStatement(UPDATE_QUERY);) {
            
            String[] customerDataUnit = input.split("\\|");
            String customerType = customerDataUnit[0].trim();
            String customerId = customerDataUnit[1].trim();
            String customerName = customerDataUnit[2].trim();
            String memberDate = customerDataUnit[3].trim();
            String openingBalance = customerDataUnit[4].trim();
            String topupBalance = customerDataUnit[5].trim();
            float totalBalance = Float.parseFloat(openingBalance) + Float.parseFloat(topupBalance);
            
            String firstName;
            String lastName;

            if (customerName.contains(" ")) {
                firstName = customerName.substring(0, customerName.indexOf(' '));
                lastName = customerName.substring(customerName.indexOf(' ') + 1);
            } else {
                firstName = customerName;
                lastName = "";
            }            
            
            Customer customer;
            
            if(customerType.equals("GUEST"))
                customer = new Guest(customerId, firstName, lastName, totalBalance);
            else
                customer = new Member(customerId, firstName, lastName, memberDate, totalBalance);
            
            if(!listCustomer.contains(customer))
                throw new Exception("UPDATE CUSTOMER FAILED: " + customerId + " NOT EXISTS");
            
            updateData.setString(1, customer.getCustomerType());
            updateData.setString(2, customerName);
            if(customer instanceof Guest)
                updateData.setString(3, null);
            else
                updateData.setString(3, memberDate);
            updateData.setFloat(4, Float.parseFloat(topupBalance));
            updateData.setString(5, customerId);
            updateData.executeUpdate();
            
            listCustomer.remove(customer);
            listCustomer.add(customer);
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createMember(String input) throws Exception{
        final String INSERT_QUERY = "insert into Customer(customerType,customerID,customerName,memberDate,openingBalance) values (?,?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                PreparedStatement insertData = connection.prepareStatement(INSERT_QUERY);) {
            String[] customerDataUnit = input.split("\\|");
            String customerId = customerDataUnit[1].trim();
            String customerName = customerDataUnit[2].trim();
            String memberDate = customerDataUnit[3].trim();
            String openingBalance = customerDataUnit[4].trim();

            String firstName;
            String lastName;

            if (customerName.contains(" ")) {
                firstName = customerName.substring(0, customerName.indexOf(' '));
                lastName = customerName.substring(customerName.indexOf(' ') + 1);
            } else {
                firstName = customerName;
                lastName = "";
            }

            Customer customer = new Member(customerId, firstName, lastName, memberDate, Float.parseFloat(openingBalance));
            if(listCustomer.contains(customer))
                throw new Exception("CREATE MEMBER FAILED: " + customerId + " ALREADY EXISTS");
            
            insertData.setString(1, customer.getCustomerType());
            insertData.setString(2, customerId);
            insertData.setString(3, customerName);
            insertData.setString(4, memberDate);
            insertData.setFloat(5, Float.parseFloat(openingBalance));
            insertData.executeUpdate();

            listCustomer.add(customer);
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void createPromo(String input) throws Exception{
        final String INSERT_QUERY = "insert into Promo(promoType,promoCode,startDate,endDate,percentDiscount,maxDiscount,minPurchase) values (?,?,?,?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                PreparedStatement insertData = connection.prepareStatement(INSERT_QUERY);) {
            String[] promoDataUnit = input.split("\\|");
            String promoType = promoDataUnit[0].trim();
            String promoCode = promoDataUnit[1].trim();
            String startDate = promoDataUnit[2].trim();
            String endDate = promoDataUnit[3].trim();
            String percentDiscount = promoDataUnit[4].trim();
            String maxDiscount = promoDataUnit[5].trim();
            String minPurchase = promoDataUnit[6].trim();
            
            Promotion promo = new CashbackPromo(promoCode);
            if(listPromo.contains(promo))
                throw new Exception("CREATE PROMO " + promoType + " FAILED: " + promoCode + " IS EXISTS");

            promo = switch (promoType) {
                case "DISCOUNT" -> new PercentOffPromo(promoCode,startDate,endDate,percentDiscount,Float.parseFloat(maxDiscount),Float.parseFloat(minPurchase));
                case "DELIVERY" -> new FreeShippingPromo(promoCode,startDate,endDate,percentDiscount,Float.parseFloat(maxDiscount),Float.parseFloat(minPurchase));
                default -> new CashbackPromo(promoCode,startDate,endDate,percentDiscount,Float.parseFloat(maxDiscount),Float.parseFloat(minPurchase));
            };
            
            insertData.setString(1, promoType);
            insertData.setString(2, promoCode);
            insertData.setString(3, startDate);
            insertData.setString(4, endDate);
            insertData.setString(5, percentDiscount);
            insertData.setFloat(6, Float.parseFloat(maxDiscount));
            insertData.setFloat(7, Float.parseFloat(minPurchase));
            insertData.executeUpdate();

            listPromo.add(promo);
            
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void removePromo(String input){
        final String REMOVE_QUERY = "DELETE FROM Promo WHERE promoCode = ?";
        Promotion promo = new CashbackPromo(input);
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                PreparedStatement removeData = connection.prepareStatement(REMOVE_QUERY);) {
            
            removeData.setString(1, input);  
            removeData.executeUpdate();
            listPromo.remove(promo);
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void updatePromo(String input) throws Exception{
        final String UPDATE_QUERY = "UPDATE Promo "
                + "Set promoType = ?, "
                + "startDate = ?, "
                + "endDate = ?,"
                + "percentDiscount = ?,"
                + "maxDiscount = ?, "
                + "minPurchase = ? "
                + "where promoCode = ?";
        
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                PreparedStatement updateData = connection.prepareStatement(UPDATE_QUERY);) {
            
            String[] promoDataUnit = input.split("\\|");
            String promoType = promoDataUnit[0].trim();
            String promoCode = promoDataUnit[1].trim();
            String startDate = promoDataUnit[2].trim();
            String endDate = promoDataUnit[3].trim();
            String percentDiscount = promoDataUnit[4].trim();
            String maxDiscount = promoDataUnit[5].trim();
            String minPurchase = promoDataUnit[6].trim();         
            
            Promotion promo;
            promo = switch (promoType) {
                case "DISCOUNT" -> new PercentOffPromo(promoCode,startDate,endDate,percentDiscount,Float.parseFloat(maxDiscount),Float.parseFloat(minPurchase));
                case "DELIVERY" -> new FreeShippingPromo(promoCode,startDate,endDate,percentDiscount,Float.parseFloat(maxDiscount),Float.parseFloat(minPurchase));
                default -> new CashbackPromo(promoCode,startDate,endDate,percentDiscount,Float.parseFloat(maxDiscount),Float.parseFloat(minPurchase));
            };
            
            if(!listPromo.contains(promo))
                throw new Exception("UPDATE PROMO FAILED: " + promoCode + " NOT EXISTS");
            
            updateData.setString(1, promoType);
            updateData.setString(2, startDate);
            updateData.setString(3, endDate);
            updateData.setString(4, percentDiscount);
            updateData.setFloat(5, Float.parseFloat(maxDiscount));
            updateData.setFloat(6, Float.parseFloat(minPurchase));
            updateData.setString(7, promoCode);
            updateData.executeUpdate();
            
            listPromo.remove(promo);
            listPromo.add(promo);
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Set<Menu> getListMenu() {
        return listMenu;
    }

    public Set<Customer> getListCustomer() {
        return listCustomer;
    }

    public Set<Promotion> getListPromo() {
        return listPromo;
    }

    @Override
    public void loadMenu() throws Exception {
        final String SELECT_QUERY = "select * from Menu";
        try (
                Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SELECT_QUERY)) {
            
            while(resultSet.next()){
                String menuId = resultSet.getString("menuID");
                String menuName = resultSet.getString("menuName");
                float menuPrice = resultSet.getFloat("menuPrice");
                Menu menu = new Menu(menuId,menuName,menuPrice);

                if (!listMenu.contains(menu))
                    listMenu.add(menu);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void loadCustomer() {
        final String SELECT_QUERY = "select * from Customer";
        try (
                Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SELECT_QUERY)) {
            
            while(resultSet.next()){
                String customerType = resultSet.getString("customerType");
                String customerId = resultSet.getString("customerID");
                String customerName = resultSet.getString("customerName");
                Date memberDate = resultSet.getDate("memberDate");
                String convertedMemberDate;
                if(memberDate == null){
                    convertedMemberDate = "-";
                } else {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    convertedMemberDate = formatter.format(memberDate);
                }
                float openingBalance = resultSet.getFloat("openingBalance");
                String firstName;
                String lastName;

                if (customerName.contains(" ")) {
                    firstName = customerName.substring(0, customerName.indexOf(' '));
                    lastName = customerName.substring(customerName.indexOf(' ') + 1);
                } else {
                    firstName = customerName;
                    lastName = "";
                }
                Customer customer;
                if (customerType.equals("GUEST"))
                    customer = new Guest(customerId, firstName, lastName, openingBalance);
                else
                    customer = new Member(customerId, firstName, lastName, convertedMemberDate, openingBalance);

                if (!listCustomer.contains(customer))
                    listCustomer.add(customer);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void loadCart() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void loadPromo() {
        final String SELECT_QUERY = "select * from Promo";
        try (
                Connection connection = DriverManager.getConnection(DATABASE_URL, "database", "database");
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SELECT_QUERY)) {
            
            while(resultSet.next()){
                String promoType = resultSet.getString("promoType");
                String promoCode = resultSet.getString("promoCode");
                Date startDate = resultSet.getDate("startDate");
                Date endDate = resultSet.getDate("endDate");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String convertedStartDate = formatter.format(startDate);
                String convertedEndDate = formatter.format(endDate);
                String percentDiscount = resultSet.getString("percentDiscount");
                float maxDiscount = resultSet.getFloat("maxDiscount");
                float minPurchase = resultSet.getFloat("minPurchase");
                
                Promotion promo;
                promo = switch (promoType) {
                    case "DISCOUNT" -> new PercentOffPromo(promoCode,convertedStartDate,convertedEndDate,percentDiscount,maxDiscount,minPurchase);
                    case "DELIVERY" -> new FreeShippingPromo(promoCode,convertedStartDate,convertedEndDate,percentDiscount,maxDiscount,minPurchase);
                    default -> new CashbackPromo(promoCode,convertedStartDate,convertedEndDate,percentDiscount,maxDiscount,minPurchase);
                };
                
                if (!listPromo.contains(promo))
                    listPromo.add(promo);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void loadHistory() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}