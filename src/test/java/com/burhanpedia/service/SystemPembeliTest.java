package com.burhanpedia.service;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import com.burhanpedia.Burhanpedia;
import com.burhanpedia.model.user.Pembeli;
import com.burhanpedia.model.user.Penjual;
import com.burhanpedia.service.SystemPembeli;
import com.burhanpedia.exception.TransactionProcessException;

public class SystemPembeliTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    
    private SystemPembeli systemPembeli;
    private Burhanpedia burhanpedia;
    private Pembeli testPembeli;
    private Scanner scanner;
    
    @Before
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        burhanpedia = new Burhanpedia();
        
        // Buat user pembeli untuk testing dengan saldo awal 1000.00
        testPembeli = new Pembeli("testpembeli", "testpass");
        testPembeli.setBalance(100000); // 1000.00 in cents
        burhanpedia.getUserRepo().addUser(testPembeli);
        
        // Buat user penjual untuk testing
        Penjual testPenjual = new Penjual("testpenjual", "testpass", "TestToko");
        burhanpedia.getUserRepo().addUser(testPenjual);
    }
    
    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
        if (scanner != null) {
            scanner.close();
        }
    }

    private void setupSystemWithInput(String input) {
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        scanner = new Scanner(inContent);
        systemPembeli = new SystemPembeli(scanner, burhanpedia);
        systemPembeli.setActivePembeli(testPembeli);
        outContent.reset(); // Clear previous output
    }
    
    @Test
    public void testShowMenu() {
        setupSystemWithInput("");  // No input needed for this test
        String expectedMenu = "\n╔════════════════════════════════════╗\n" +
                            "║           MENU PEMBELI             ║\n" +
                            "╠════════════════════════════════════╣\n" +
                            "║ 1. Cek Saldo                       ║\n" +
                            "║ 2. Top Up Saldo                    ║\n" +
                            "║ 3. Lihat Daftar Produk             ║\n" +
                            "║ 4. Tambah ke Keranjang             ║\n" +
                            "║ 5. Checkout                        ║\n" +
                            "║ 6. Lacak Pesanan                   ║\n" +
                            "║ 7. Lihat Laporan Pengeluaran       ║\n" +
                            "║ 8. Lihat Riwayat Transaksi         ║\n" +
                            "║ 9. Kembali ke Menu Utama           ║\n" +
                            "╚════════════════════════════════════╝";
        
        assertEquals("Menu should match exactly", expectedMenu, systemPembeli.showMenu());
    }

    
    @Test
    public void testHandleTopupSaldo() throws TransactionProcessException {
        setupSystemWithInput("2\n50000\n9\n");
        systemPembeli.handleMenu();
        
        String output = outContent.toString();
        assertTrue("Should contain top up menu", output.contains("║            TOP UP SALDO            ║"));
        assertTrue("Should contain input prompt", output.contains("Masukkan jumlah saldo yang ingin ditambah:"));
        assertTrue("Should contain success message", output.contains("✓ Saldo berhasil ditambah!"));
    }
    
    @Test
    public void testHandleTopupSaldo_InvalidAmount() throws TransactionProcessException {
        setupSystemWithInput("2\n-1000\n9\n");
        systemPembeli.handleMenu();
        
        String output = outContent.toString();
        assertTrue("Should show error for negative amount", output.contains("✗ Jumlah saldo harus positif!"));
    }
    
    @Test
    public void testHandleTopupSaldo_InvalidInput() throws TransactionProcessException {
        setupSystemWithInput("2\nabc\n9\n");
        systemPembeli.handleMenu();
        
        String output = outContent.toString();
        assertTrue("Should show error for invalid input", output.contains("✗ Masukkan angka yang valid!"));
    }
    
    @Test
    public void testHandleMenu_Exit() {
        setupSystemWithInput("9\n");
        systemPembeli.handleMenu();
        
        String output = outContent.toString();
        assertTrue("Should show menu before exit", output.contains("MENU PEMBELI"));
    }
    
    @Test
    public void testHandleMenu_InvalidCommand() {
        setupSystemWithInput("invalid\n9\n");
        systemPembeli.handleMenu();
        
        String output = outContent.toString();
        assertTrue("Should show invalid command message", output.contains("Perintah tidak valid!"));
    }
    
    @Test
    public void testHandleCekSaldo_UserNotFound() throws TransactionProcessException {
        // Create a pembeli that's not in the repository
        Pembeli nonExistentPembeli = new Pembeli("nonexistent", "pass");
        setupSystemWithInput("1\n9\n");
        systemPembeli.setActivePembeli(nonExistentPembeli);
        
        systemPembeli.handleMenu();
        String output = outContent.toString();
        assertTrue("Should show user not found error", output.contains("User tidak ditemukan"));
    }
    
    @Test
    public void testHandleCekSaldo_NullUser() throws TransactionProcessException {
        setupSystemWithInput("1\n9\n");
        systemPembeli.setActivePembeli(null);
        
        systemPembeli.handleMenu();
        String output = outContent.toString();
        assertTrue("Should show inactive user error", output.contains("Pembeli tidak aktif"));
    }
} 