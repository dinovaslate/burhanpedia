package com.burhanpedia.repository;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import com.burhanpedia.model.transaction.Transaksi;
import com.burhanpedia.model.product.Product;
import com.burhanpedia.model.discount.Voucher;
import com.burhanpedia.model.discount.Promo;

import java.io.File;
import java.util.UUID;

public class TransaksiRepositoryTest {
    
    private TransaksiRepository transaksiRepo;
    private VoucherRepository voucherRepo;
    private PromoRepository promoRepo;
    private ProductRepository productRepo;
    private Transaksi testTransaksi;
    private Product testProduct;
    
    @Before
    public void setUp() {
        voucherRepo = new VoucherRepository();
        promoRepo = new PromoRepository();
        productRepo = new ProductRepository("Test Toko");
        transaksiRepo = new TransaksiRepository(voucherRepo, promoRepo, productRepo);
        
        testProduct = new Product("TestProduct", 10, 50000);
        productRepo.addProduct(testProduct);
        
        testTransaksi = new Transaksi("testPembeli", "testPenjual", "Regular");
        testTransaksi.addProduct(testProduct.getProductId(), 2);
    }
    
    @After
    public void tearDown() {
        // Hapus file CSV test jika ada
        File file = new File("data/transactions.csv");
        if (file.exists()) {
            file.delete();
        }
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(transaksiRepo);
        assertEquals(0, transaksiRepo.getList().length);
    }
    
    @Test
    public void testAddTransaksi() {
        transaksiRepo.addTransaksi(testTransaksi);
        
        // Verifikasi transaksi ditambahkan
        Transaksi[] transaksis = transaksiRepo.getList();
        assertEquals(1, transaksis.length);
        assertEquals(testTransaksi.getId(), transaksis[0].getId());
    }
    
    @Test
    public void testGetList() {
        // Tambahkan beberapa transaksi
        transaksiRepo.addTransaksi(testTransaksi);
        
        Transaksi anotherTransaksi = new Transaksi("anotherPembeli", "anotherPenjual", "Instant");
        anotherTransaksi.addProduct(testProduct.getProductId(), 1);
        transaksiRepo.addTransaksi(anotherTransaksi);
        
        // Test getList
        Transaksi[] transaksis = transaksiRepo.getList();
        assertNotNull(transaksis);
        assertEquals(2, transaksis.length);
    }
    
    @Test
    public void testGetTransaksiById_ExistingTransaksi() {
        transaksiRepo.addTransaksi(testTransaksi);
        
        // Test getTransaksiById
        Transaksi transaksi = transaksiRepo.getTransaksiById(testTransaksi.getId());
        assertNotNull(transaksi);
        assertEquals(testTransaksi.getId(), transaksi.getId());
    }
    
    @Test
    public void testGetTransaksiById_NonExistingTransaksi() {
        Transaksi transaksi = transaksiRepo.getTransaksiById("nonexistent");
        assertNull(transaksi);
    }
    
    @Test
    public void testGetAvailableJobs_NoJobs() {
        Transaksi[] jobs = transaksiRepo.getAvailableJobs();
        assertNotNull(jobs);
        assertEquals(0, jobs.length);
    }
    
    @Test
    public void testGetAvailableJobs_WithJobs() {
        // Tambahkan transaksi dan ubah statusnya
        transaksiRepo.addTransaksi(testTransaksi);
        transaksiRepo.updateTransactionStatus(testTransaksi.getId(), "Menunggu Pengirim");
        
        // Test getAvailableJobs
        Transaksi[] jobs = transaksiRepo.getAvailableJobs();
        assertNotNull(jobs);
        assertEquals(1, jobs.length);
        assertEquals(testTransaksi.getId(), jobs[0].getId());
    }
    
    @Test
    public void testCalculateTotalTransaksi_WithoutDiscount() {
        transaksiRepo.addTransaksi(testTransaksi);
        
        long total = transaksiRepo.calculateTotalTransaksi(testTransaksi.getId());
        // Expected: (50000 * 2) + (3% tax) + 10000 shipping
        long expected = (100000 + (100000 * 3 / 100) + 10000);
        assertEquals(expected, total);
    }
    
    @Test
    public void testCalculateTotalTransaksi_WithVoucher() {
        // Create and add voucher
        Voucher voucher = new Voucher("12200", new java.util.Date());
        voucherRepo.addVoucher(voucher);
        
        // Add voucher to transaction
        testTransaksi.setIdDiskon(voucher.getId());
        transaksiRepo.addTransaksi(testTransaksi);
        
        long total = transaksiRepo.calculateTotalTransaksi(testTransaksi.getId());
        // Expected: (50000 * 2) - discount + (3% tax) + 10000 shipping
        long subtotal = 100000;
        long discount = voucher.calculateDisc(subtotal);
        long afterDiscount = subtotal - discount;
        long tax = (afterDiscount * 3) / 100;
        long expected = afterDiscount + tax + 10000;
        
        assertEquals(expected, total);
    }
    
    @Test
    public void testCalculateTotalTransaksi_WithPromo() {
        // Create and add promo
        Promo promo = new Promo("PROMO123", new java.util.Date());
        promoRepo.addPromo(promo);
        
        // Add promo to transaction
        testTransaksi.setIdDiskon(promo.getId());
        transaksiRepo.addTransaksi(testTransaksi);
        
        long total = transaksiRepo.calculateTotalTransaksi(testTransaksi.getId());
        // Expected: (50000 * 2) - discount + (3% tax) + 10000 shipping
        long subtotal = 100000;
        long discount = promo.calculateDisc(subtotal);
        long afterDiscount = subtotal - discount;
        long tax = (afterDiscount * 3) / 100;
        long expected = afterDiscount + tax + 10000;
        
        assertEquals(expected, total);
    }
    
    @Test
    public void testSaveAndLoadTransactionsToCsv() {
        // Tambahkan transaksi
        transaksiRepo.addTransaksi(testTransaksi);
        
        // Buat repository baru untuk memuat dari CSV
        TransaksiRepository newRepo = new TransaksiRepository(voucherRepo, promoRepo, productRepo);
        
        // Verifikasi transaksi dimuat dengan benar
        assertEquals(1, newRepo.getList().length);
        assertEquals(testTransaksi.getId(), newRepo.getList()[0].getId());
        assertEquals("testPembeli", newRepo.getList()[0].getNamePembeli());
        assertEquals("testPenjual", newRepo.getList()[0].getNamePenjual());
        assertEquals("Regular", newRepo.getList()[0].getJenisTransaksi());
    }
}