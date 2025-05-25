package com.burhanpedia.model.discount;

import java.util.Date;

public class Promo {
    private String id;
    private Date berlakuHingga;
    
    public Promo(String id, Date berlakuHingga) {
        this.id = id;
        this.berlakuHingga = berlakuHingga;
    }
    
    public String getId() {
        return id;
    }
    
    public Date getBerlakuHingga() {
        return berlakuHingga;
    }
    
    public long calculateDisc(long harga) {
        int discount = 0;
    
        for (char c : id.toCharArray()) {
            if (Character.isDigit(c)) {
                discount += Character.getNumericValue(c);
            }
        }
    
        if (discount == 0) {
            discount = 5;
        }
    
        // Perbaiki bagian ini:
        // Jika discount (hasil jumlah digit) >= 100, sederhanakan
        if (discount >= 100) {
            int newDiscount = 0;
            while (discount > 0) {
                newDiscount += discount % 10;
                discount /= 10;
            }
            discount = newDiscount;
        }
    
        return (harga * discount) / 100;
    }
    
}