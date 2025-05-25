package com.burhanpedia.repository;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.burhanpedia.model.discount.Voucher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VoucherRepositoryTest {
    
    private VoucherRepository voucherRepo;
    
    @Before
    public void setUp() {
        voucherRepo = new VoucherRepository();
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(voucherRepo);
        assertEquals(0, voucherRepo.getAll().length);
    }
    
    @Test
    public void testGenerate() {
        // Generate voucher dengan tanggal valid
        voucherRepo.generate("2025-12-31");
        
        // Verifikasi voucher dibuat
        Voucher[] vouchers = voucherRepo.getAll();
        assertEquals(1, vouchers.length);
        
        // Verifikasi tanggal berlaku
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date expectedDate = formatter.parse("2025-12-31");
            assertEquals(expectedDate, vouchers[0].getBerlakuHingga());
        } catch (ParseException e) {
            fail("Tanggal tidak valid");
        }
    }
    
    @Test
    public void testGetById_ExistingVoucher() {
        // Generate voucher
        voucherRepo.generate("2025-12-31");
        
        // Dapatkan ID voucher
        String voucherId = voucherRepo.getAll()[0].getId();
        
        // Test getById
        Voucher voucher = voucherRepo.getById(voucherId);
        assertNotNull(voucher);
        assertEquals(voucherId, voucher.getId());
    }
    
    @Test
    public void testGetById_NonExistingVoucher() {
        Voucher voucher = voucherRepo.getById("nonexistent");
        assertNull(voucher);
    }
    
    @Test
    public void testGetAll_EmptyRepository() {
        Voucher[] vouchers = voucherRepo.getAll();
        assertNotNull(vouchers);
        assertEquals(0, vouchers.length);
    }
    
    @Test
    public void testGetAll_WithVouchers() {
        // Generate beberapa voucher
        voucherRepo.generate("2025-12-31");
        voucherRepo.generate("2026-01-31");
        
        // Test getAll
        Voucher[] vouchers = voucherRepo.getAll();
        assertNotNull(vouchers);
        assertEquals(2, vouchers.length);
    }
    
    @Test
    public void testGenerateVoucherCode() {
        // Generate dua voucher
        voucherRepo.generate("2025-12-31");
        voucherRepo.generate("2025-12-31");
        
        // Verifikasi kode voucher unik
        Voucher[] vouchers = voucherRepo.getAll();
        assertNotEquals(vouchers[0].getId(), vouchers[1].getId());
        
        // Verifikasi format kode voucher (10 digit)
        assertEquals(10, vouchers[0].getId().length());
        assertEquals(10, vouchers[1].getId().length());
    }
}