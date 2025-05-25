package com.burhanpedia.model.user;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.burhanpedia.repository.ProductRepository;

public class PenjualTest {
    private Penjual penjual;
    
    @Before
    public void setUp() {
        penjual = new Penjual("penjual", "password123", "Toko Test");
    }
    
    @Test
    public void testPenjualConstructor() {
        assertEquals("penjual", penjual.getUsername());
        assertEquals("password123", penjual.getPassword());
        assertEquals("Penjual", penjual.getRole());
        assertNotNull(penjual.getRepo());
        assertEquals("Toko Test", penjual.getNamaToko());
        assertEquals(0, penjual.getBalance());
    }
    
    @Test
    public void testGetRepo() {
        ProductRepository repo = penjual.getRepo();
        assertNotNull(repo);
        assertEquals("Toko Test", repo.getNamaToko());
    }
    
    @Test
    public void testSetRepo() {
        ProductRepository newRepo = new ProductRepository("Toko Baru");
        penjual.setRepo(newRepo);
        assertEquals(newRepo, penjual.getRepo());
        assertEquals("Toko Baru", penjual.getNamaToko());
    }
    
    @Test
    public void testAddRevenue() {
        penjual.addRevenue(1000);
        assertEquals(1000, penjual.getBalance());
        
        // Test dengan nilai negatif (tidak boleh menambah)
        penjual.addRevenue(-500);
        assertEquals(1000, penjual.getBalance());
    }
    
    @Test
    public void testGetFormattedBalance() {
        penjual.setBalance(12345);
        assertEquals("123.45", penjual.getFormattedBalance());
    }
    
    @Test
    public void testHasSufficientBalance() {
        penjual.setBalance(1000);
        assertTrue(penjual.hasSufficientBalance(1000));
        assertTrue(penjual.hasSufficientBalance(500));
        assertFalse(penjual.hasSufficientBalance(1001));
    }
    
    @Test
    public void testWithdraw() {
        penjual.setBalance(1000);
        
        // Withdraw yang berhasil
        assertTrue(penjual.withdraw(500));
        assertEquals(500, penjual.getBalance());
        
        // Withdraw yang gagal (saldo tidak cukup)
        assertFalse(penjual.withdraw(1000));
        assertEquals(500, penjual.getBalance());
    }
}