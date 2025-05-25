package com.burhanpedia.repository;

import static org.junit.Assert.*;
import org.junit.Test;

import com.burhanpedia.model.discount.Promo;
import com.burhanpedia.model.discount.Voucher;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DiskonRepositoryTest {
    
    @Test
    public void testVoucherRepositoryImplementsDiskonRepository() {
        // Test bahwa VoucherRepository mengimplementasikan DiskonRepository
        VoucherRepository voucherRepo = new VoucherRepository();
        assertTrue(voucherRepo instanceof DiskonRepository);
    }
    
    @Test
    public void testPromoRepositoryImplementsDiskonRepository() {
        // Test bahwa PromoRepository mengimplementasikan DiskonRepository
        PromoRepository promoRepo = new PromoRepository();
        assertTrue(promoRepo instanceof DiskonRepository);
    }
    
    @Test
    public void testDiskonRepositoryMethods() {
        // Test bahwa method-method DiskonRepository diimplementasikan dengan benar
        VoucherRepository voucherRepo = new VoucherRepository();
        PromoRepository promoRepo = new PromoRepository();
        
        // Generate diskon
        voucherRepo.generate("2025-12-31");
        promoRepo.generate("2025-12-31");
        
        // Test getAll
        Voucher[] vouchers = voucherRepo.getAll();
        Promo[] promos = promoRepo.getAll();
        
        assertNotNull(vouchers);
        assertNotNull(promos);
        assertEquals(1, vouchers.length);
        assertEquals(1, promos.length);
        
        // Test getById
        String voucherId = vouchers[0].getId();
        String promoId = promos[0].getId();
        
        Voucher voucher = voucherRepo.getById(voucherId);
        Promo promo = promoRepo.getById(promoId);
        
        assertNotNull(voucher);
        assertNotNull(promo);
        assertEquals(voucherId, voucher.getId());
        assertEquals(promoId, promo.getId());
    }
}