package com.burhanpedia.model.discount;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class PromoTest {
    private Promo promo;
    private Date berlakuHingga;
    
    @Before
    public void setUp() {
        berlakuHingga = new Date();
        promo = new Promo("PROMO123", berlakuHingga);
    }
    
    @Test
    public void testPromoConstructor() {
        assertEquals("PROMO123", promo.getId());
        assertEquals(berlakuHingga, promo.getBerlakuHingga());
    }
    
    @Test
    public void testGetId() {
        assertEquals("PROMO123", promo.getId());
    }
    
    @Test
    public void testGetBerlakuHingga() {
        assertEquals(berlakuHingga, promo.getBerlakuHingga());
    }
    
    @Test
    public void testCalculateDisc() {
        // Promo dengan ID "PROMO123" memiliki 3 digit (1, 2, 3)
        // Total digit = 6, discount = 6%
        long harga = 100000;
        long expectedDiscount = (harga * 6) / 100; // 6000
        
        assertEquals(expectedDiscount, promo.calculateDisc(harga));
    }
    
    @Test
    public void testCalculateDiscWithNoDigits() {
        Promo noDigitPromo = new Promo("ABCDEFGHIJ", berlakuHingga);
        
        // Tidak ada digit, seharusnya default 5%
        long harga = 100000;
        long expectedDiscount = (harga * 5) / 100; // 5000
        
        assertEquals(expectedDiscount, noDigitPromo.calculateDisc(harga));
    }
    
    @Test
public void testCalculateDiscWithLargeDiscount() {
    Promo largeDiscountPromo = new Promo("PROMO999999", berlakuHingga);

    // Total digit = 54 → tidak lebih dari 100, jadi tetap 54%
    long harga = 100000;
    long expectedDiscount = (harga * 54) / 100; // 54000

    assertEquals(expectedDiscount, largeDiscountPromo.calculateDisc(harga));
}

}