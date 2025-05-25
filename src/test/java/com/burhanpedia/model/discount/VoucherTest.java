package com.burhanpedia.model.discount;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class VoucherTest {
    private Voucher voucher;
    private Date berlakuHingga;
    
    @Before
    public void setUp() {
        berlakuHingga = new Date();
        voucher = new Voucher("VOUCHER123", berlakuHingga);
    }
    
    @Test
    public void testVoucherConstructor() {
        assertEquals("VOUCHER123", voucher.getId());
        assertEquals(berlakuHingga, voucher.getBerlakuHingga());
        assertTrue(voucher.getSisaPemakaian() >= 1);
    }
    
    @Test
    public void testGetId() {
        assertEquals("VOUCHER123", voucher.getId());
    }
    
    @Test
    public void testGetSisaPemakaian() {
        int sisaPemakaian = voucher.getSisaPemakaian();
        assertTrue(sisaPemakaian >= 1);
    }
    
    @Test
    public void testDecrementPemakaian() {
        int initialSisaPemakaian = voucher.getSisaPemakaian();
        
        voucher.decrementPemakaian();
        
        assertEquals(initialSisaPemakaian - 1, voucher.getSisaPemakaian());
    }
    
    @Test
    public void testDecrementPemakaianToZero() {
        // Set sisa pemakaian ke 1 dengan cara memanggil decrementPemakaian
        // sampai tersisa 1
        while (voucher.getSisaPemakaian() > 1) {
            voucher.decrementPemakaian();
        }
        
        assertEquals(1, voucher.getSisaPemakaian());
        
        // Decrement lagi
        voucher.decrementPemakaian();
        
        // Seharusnya menjadi 0
        assertEquals(0, voucher.getSisaPemakaian());
        
        // Decrement lagi, seharusnya tetap 0
        voucher.decrementPemakaian();
        assertEquals(0, voucher.getSisaPemakaian());
    }
    
    @Test
    public void testGetBerlakuHingga() {
        assertEquals(berlakuHingga, voucher.getBerlakuHingga());
    }
    
    @Test
    public void testCalculateDisc() {
        // Voucher dengan ID "VOUCHER123" memiliki 3 digit (1, 2, 3)
        // Total digit = 6, discount = 6 * 2 = 12%
        long harga = 100000;
        long expectedDiscount = (harga * 12) / 100; // 12000
        
        assertEquals(expectedDiscount, voucher.calculateDisc(harga));
    }
    
    @Test
    public void testCalculateDiscWithNoDigits() {
        Voucher noDigitVoucher = new Voucher("ABCDEFGHIJ", berlakuHingga);
        
        // Tidak ada digit, seharusnya minimal 1%
        long harga = 100000;
        long expectedDiscount = (harga * 1) / 100; // 1000
        
        assertEquals(expectedDiscount, noDigitVoucher.calculateDisc(harga));
    }
}