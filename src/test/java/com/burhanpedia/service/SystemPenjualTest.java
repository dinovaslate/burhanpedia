package com.burhanpedia.service;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.UUID;

import com.burhanpedia.Burhanpedia;
import com.burhanpedia.model.product.Product;
import com.burhanpedia.model.transaction.Transaksi;
import com.burhanpedia.model.user.Penjual;
import com.burhanpedia.exception.TransactionProcessException;
import com.burhanpedia.repository.ProductRepository;
import com.burhanpedia.repository.TransaksiRepository;
import com.burhanpedia.service.SystemPenjual;

@RunWith(JUnit4.class)
public class SystemPenjualTest {
    
    private SystemPenjual systemPenjual;
    private Burhanpedia burhanpedia;
    private Penjual penjual;
    private Scanner scanner;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    
    @Before
    public void setUp() {
        // Simpan output asli
        originalOut = System.out;
        
        // Siapkan output stream untuk menangkap output
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        
        // Inisialisasi Burhanpedia
        burhanpedia = new Burhanpedia();
        
        // Buat penjual untuk testing
        penjual = new Penjual("testPenjual", "password123", "Toko Test");
        
        // Tambahkan produk ke toko penjual
        Product testProduct = new Product("TestProduct", 10, 50000);
        penjual.getRepo().addProduct(testProduct);
        
        // Buat scanner dengan input kosong (akan diisi per test)
        scanner = new Scanner("");
        
        // Inisialisasi SystemPenjual
        systemPenjual = new SystemPenjual(scanner, burhanpedia);
        systemPenjual.setActivePenjual(penjual);
    }
    
    @After
    public void tearDown() {
        // Kembalikan output asli
        System.setOut(originalOut);
        
        // Hapus file CSV test jika ada
        File productsFile = new File("data/products.csv");
        if (productsFile.exists()) {
            productsFile.delete();
        }
        
        File transactionsFile = new File("data/transactions.csv");
        if (transactionsFile.exists()) {
            transactionsFile.delete();
        }
    }

    
    @Test
    public void testHandleCekProduk() throws TransactionProcessException {
        // Reset output stream
        outputStream.reset();
        
        // Panggil method yang akan diuji
        systemPenjual.handleCekProduk();
        
        // Verifikasi output
        String output = outputStream.toString();
        assertTrue(output.contains("TestProduct"));
        assertTrue(output.contains("500.00")); // 50000/100.0
        assertTrue(output.contains("10")); // stok
    }
    
    @Test
    public void testHandleTambahProduk() throws TransactionProcessException {
        // Siapkan input untuk scanner
        String input = "NewProduct\n20\n75000\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        scanner = new Scanner(System.in);
        systemPenjual = new SystemPenjual(scanner, burhanpedia);
        systemPenjual.setActivePenjual(penjual);
        
        // Reset output stream
        outputStream.reset();
        
        // Panggil method yang akan diuji
        systemPenjual.handleTambahProduk();
        
        // Verifikasi output
        String output = outputStream.toString();
        assertTrue(output.contains("✓ Produk berhasil ditambahkan!"));
        
        // Verifikasi produk ditambahkan ke repository
        Product newProduct = penjual.getRepo().getProductByName("NewProduct");
        assertNotNull(newProduct);
        assertEquals(20, newProduct.getProductStock());
        assertEquals(75000, newProduct.getProductPrice());
    }
    
    @Test
    public void testHandleTambahStok() throws TransactionProcessException {
        // Siapkan input untuk scanner
        String input = "TestProduct\n5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        scanner = new Scanner(System.in);
        systemPenjual = new SystemPenjual(scanner, burhanpedia);
        systemPenjual.setActivePenjual(penjual);
        
        // Reset output stream
        outputStream.reset();
        
        // Panggil method yang akan diuji
        systemPenjual.handleTambahStok();
        
        // Verifikasi output - Memperbaiki ekspektasi output dengan menggunakan nama yang lowercase
        String output = outputStream.toString();
        assertTrue(output.contains("✓ Stok TestProduct berhasil ditambah"));  // Fixed: lowercase product name
        assertTrue(output.contains("Stok saat ini: 15"));
        
        // Verifikasi stok diperbarui
        Product product = penjual.getRepo().getProductByName("TestProduct");
        assertEquals(15, product.getProductStock()); // 10 (stok awal) + 5 (penambahan)
    }
    
    @Test
    public void testHandleUbahHarga() throws TransactionProcessException {
        // Siapkan input untuk scanner
        String input = "TestProduct\n60000\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        scanner = new Scanner(System.in);
        systemPenjual = new SystemPenjual(scanner, burhanpedia);
        systemPenjual.setActivePenjual(penjual);
        
        // Reset output stream
        outputStream.reset();
        
        // Panggil method yang akan diuji
        systemPenjual.handleUbahHarga();
        
        // Verifikasi output
        String output = outputStream.toString();
        assertTrue(output.contains("✓ Harga produk berhasil diperbarui"));
        
        // Verifikasi harga diperbarui
        Product product = penjual.getRepo().getProductByName("TestProduct");
        assertEquals(60000, product.getProductPrice());
    }

    
    @Test
public void testHandleKirimBarang() throws TransactionProcessException {
    // Instead of creating a transaction and then setting its ID,
    // we'll use reflection to access the private fields of the Transaksi class
    
    // Create transaction for testing
    Transaksi transaksi = new Transaksi("testPembeli", "testPenjual", "Regular");
    
    // Add it to the repository
    burhanpedia.getTransaksiRepo().addTransaksi(transaksi);
    
    // Capture the transaction ID for verification
    String transactionId = transaksi.getId();
    
    // Prepare input for scanner (simulate entering the transaction ID and then "batal")
    String input = "batal\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    scanner = new Scanner(System.in);
    systemPenjual = new SystemPenjual(scanner, burhanpedia);
    systemPenjual.setActivePenjual(penjual);
    
    // Reset output stream
    outputStream.reset();
    
    // Call the method being tested
    systemPenjual.handleKirimBarang();
    
    // Verify transaction was displayed
    String output = outputStream.toString();
    assertTrue(output.contains(transactionId));
    assertTrue(output.contains("Sedang Dikemas"));
}
    

    @Test
    public void testHandleCekSaldo() throws TransactionProcessException {
        // Set saldo penjual
        penjual.setBalance(12345);
        
        // Reset output stream
        outputStream.reset();
        
        // Panggil method yang akan diuji
        systemPenjual.handleCekSaldo();
        
        // Verifikasi output
        String output = outputStream.toString();
        assertTrue(output.contains("Saldo saat ini: 123.45"));
    }
    
    @Test
    public void testHandleMenu_Exit() {
        // Siapkan input untuk scanner (pilih menu 9 untuk keluar)
        String input = "9\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        scanner = new Scanner(System.in);
        systemPenjual = new SystemPenjual(scanner, burhanpedia);
        systemPenjual.setActivePenjual(penjual);
        
        // Reset output stream
        outputStream.reset();
        
        // Panggil method yang akan diuji
        systemPenjual.handleMenu();
        
        // Verifikasi menu ditampilkan
        String output = outputStream.toString();
        assertTrue(output.contains("MENU PENJUAL"));
    }
    
    @Test
    public void testHandleMenu_InvalidCommand() {
        // Siapkan input untuk scanner (perintah tidak valid, lalu keluar)
        String input = "99\n9\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        scanner = new Scanner(System.in);
        systemPenjual = new SystemPenjual(scanner, burhanpedia);
        systemPenjual.setActivePenjual(penjual);
        
        // Reset output stream
        outputStream.reset();
        
        // Panggil method yang akan diuji
        systemPenjual.handleMenu();
        
        // Verifikasi pesan error ditampilkan
        String output = outputStream.toString();
        assertTrue(output.contains("Perintah tidak valid"));
    }
    
    @Test(expected = TransactionProcessException.class)
    public void testHandleCekProduk_NoPenjual() throws TransactionProcessException {
        // Set penjual aktif ke null
        systemPenjual.setActivePenjual(null);
        
        // Panggil method yang akan diuji, seharusnya throw exception
        systemPenjual.handleCekProduk();
    }
}