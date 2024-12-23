/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PromotionPackage;

import OrderPackage.Order;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author TUF GAMING
 */
public class PercentOffPromo extends Promotion{
    
    public PercentOffPromo(String promoCode, String startDate, String endDate, String percentDiscount, float maxDiscount, float minimumPurchase) {
        super(promoCode, startDate, endDate, percentDiscount, maxDiscount, minimumPurchase);
        this.promoType = "DISCOUNT";
    }

    public PercentOffPromo(String promoCode) {
        super(promoCode);
    }

    @Override
    public double totalDiscount(Order x) {
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(getPercentDiscount());
        matcher.find();
        String percentDiscount = matcher.group();
        double discount = (Double.parseDouble(percentDiscount) / 100) * x.getSubTotalFoodCost();
        if (discount > getMaxDiscount()) 
            return getMaxDiscount();
        return discount;
    }
}