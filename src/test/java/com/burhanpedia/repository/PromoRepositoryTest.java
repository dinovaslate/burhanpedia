package com.burhanpedia.repository;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.burhanpedia.model.discount.Promo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PromoRepositoryTest {
    
    private PromoRepository promoRepo;
    
    @Before
    public void setUp() {
        promoRepo = new PromoRepository();
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(promoRepo);
        assertEquals(0, promoRepo.getAll().length);
    }
    
    @Test
    public void testGenerate() {
        // Generate promo dengan tanggal valid
        promoRepo.generate("2025-12-31");
        
        // Verifikasi promo dibuat
        Promo[] promos = promoRepo.getAll();
        assertEquals(1, promos.length);
        
        // Verifikasi tanggal berlaku
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date expectedDate = formatter.parse("2025-12-31");
            assertEquals(expectedDate, promos[0].getBerlakuHingga());
        } catch (ParseException e) {
            fail("Tanggal tidak valid");
        }
    }
    
    @Test
    public void testGetById_ExistingPromo() {
        // Generate promo
        promoRepo.generate("2025-12-31");
        
        // Dapatkan ID promo
        String promoId = promoRepo.getAll()[0].getId();
        
        // Test getById
        Promo promo = promoRepo.getById(promoId);
        assertNotNull(promo);
        assertEquals(promoId, promo.getId());
    }
    
    @Test
    public void testGetById_NonExistingPromo() {
        Promo promo = promoRepo.getById("nonexistent");
        assertNull(promo);
    }
    
    @Test
    public void testGetAll_EmptyRepository() {
        Promo[] promos = promoRepo.getAll();
        assertNotNull(promos);
        assertEquals(0, promos.length);
    }
    
    @Test
    public void testGetAll_WithPromos() {
        // Generate beberapa promo
        promoRepo.generate("2025-12-31");
        promoRepo.generate("2026-01-31");
        
        // Test getAll
        Promo[] promos = promoRepo.getAll();
        assertNotNull(promos);
        assertEquals(2, promos.length);
    }
    
    @Test
    public void testGeneratePromoCode() {
        // Generate dua promo
        promoRepo.generate("2025-12-31");
        promoRepo.generate("2025-12-31");
        
        // Verifikasi kode promo unik
        Promo[] promos = promoRepo.getAll();
        assertNotEquals(promos[0].getId(), promos[1].getId());
        
        // Verifikasi format kode promo (16 karakter)
        assertEquals(16, promos[0].getId().length());
        assertEquals(16, promos[1].getId().length());
    }
}