package com.burhanpedia;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.burhanpedia.model.user.Pembeli;
import com.burhanpedia.model.user.Penjual;
import com.burhanpedia.model.product.Product;
import com.burhanpedia.model.transaction.Transaksi;
import com.burhanpedia.repository.UserRepository;
import com.burhanpedia.repository.VoucherRepository;
import com.burhanpedia.repository.PromoRepository;

public class BurhanpediaTest {
    
    private Burhanpedia burhanpedia;
    
    @Before
    public void setUp() {
        burhanpedia = new Burhanpedia();
        
        // Reset repositories untuk menghindari duplikasi
        burhanpedia.getUserRepo().clear();
        burhanpedia.getVoucherRepo().clear();
        burhanpedia.getPromoRepo().clear();
        burhanpedia.getTransaksiRepo().clear();
        
        // Create test users
        Pembeli pembeli = new Pembeli("testPembeli", "password");
        pembeli.setBalance(100000);
        
        Penjual penjual = new Penjual("testPenjual", "password", "testToko");
        
        // Add product to penjual
        Product product = new Product("testProduct", 10, 50000);
        penjual.getRepo().addProduct(product);
        
        // Add users to repository
        burhanpedia.getUserRepo().addUser(pembeli);
        burhanpedia.getUserRepo().addUser(penjual);
        
        // Generate voucher and promo
        burhanpedia.getVoucherRepo().generate("2025-12-31");
        burhanpedia.getPromoRepo().generate("2025-12-31");
    }
    
    @Test
    public void testGetUserRepo() {
        UserRepository userRepo = burhanpedia.getUserRepo();
        assertNotNull(userRepo);
        
        // Hapus log yang tidak perlu
        // System.out.println(userRepo.getAll().length);
        // for (var user : userRepo.getAll()) {
        //     System.out.println(user.getUsername());
        // }
        
        assertEquals(2, userRepo.getAll().length);
    }
    
    @Test
    public void testGetVoucherRepo() {
        VoucherRepository voucherRepo = burhanpedia.getVoucherRepo();
        assertNotNull(voucherRepo);
        assertEquals(1, voucherRepo.getAll().length);
    }
    
    @Test
    public void testGetPromoRepo() {
        PromoRepository promoRepo = burhanpedia.getPromoRepo();
        assertNotNull(promoRepo);
        assertEquals(1, promoRepo.getAll().length);
    }
    
    
    
    private Penjual[] getPenjualList() {
        java.util.ArrayList<Penjual> penjuals = new java.util.ArrayList<>();
        
        for (var user : burhanpedia.getUserRepo().getAll()) {
            if (user.getRole().equals("Penjual")) {
                penjuals.add((Penjual) user);
            }
        }
        
        return penjuals.toArray(new Penjual[0]);
    }
}