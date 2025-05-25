package com.burhanpedia.model.discount;

import java.util.Date;
import java.util.Random;

public class Voucher {
    private String id;
    private int sisaPemakaian;
    private Date berlakuHingga;
    
    public Voucher(String id, Date berlakuHingga) {
        this.id = id;
        this.berlakuHingga = berlakuHingga;
        
        // Determine sisaPemakaian based on id character at random index
        Random random = new Random();
        int index = random.nextInt(Math.max(1, id.length())); // Ensure at least 1 character
        char digit = id.charAt(index);
        
        // Convert char to int and ensure minimum 1
        this.sisaPemakaian = Character.isDigit(digit) ? 
                            Character.getNumericValue(digit) : 1;
        
        if (this.sisaPemakaian == 0) {
            this.sisaPemakaian = 1;
        }
    }
    
    public String getId() {
        return id;
    }
    
    public int getSisaPemakaian() {
        return sisaPemakaian;
    }
    
    public void decrementPemakaian() {
        if (sisaPemakaian > 0) {
            sisaPemakaian--;
        }
    }
    
    public Date getBerlakuHingga() {
        return berlakuHingga;
    }
    
    public long calculateDisc(long harga) {
        int discount = 0;
        char[] idChars = id.toCharArray();
        
        for (char c : idChars) {
            if (Character.isDigit(c)) {
                discount += Character.getNumericValue(c);
            }
        }
        
        // Calculate discount, minimum 1%
        int discountPercentage = Math.max(discount * 2, 1);
        return (harga * discountPercentage) / 100;
    }
}