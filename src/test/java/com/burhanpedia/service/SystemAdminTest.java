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
import java.util.Date;

import com.burhanpedia.Burhanpedia;
import com.burhanpedia.model.user.Admin;
import com.burhanpedia.model.discount.Voucher;
import com.burhanpedia.service.SystemAdmin;

public class SystemAdminTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    
    private SystemAdmin systemAdmin;
    private Burhanpedia burhanpedia;
    private Admin testAdmin;
    private Scanner scanner;
    
    @Before
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        burhanpedia = new Burhanpedia();
        testAdmin = new Admin("testadmin", "testpass");
        burhanpedia.getAdminRepo().addAdmin(testAdmin);
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
        systemAdmin = new SystemAdmin(scanner, burhanpedia);
        systemAdmin.setActiveAdmin(testAdmin);
    }
    
    @Test
    public void testShowMenu() {
        setupSystemWithInput("");  // No input needed for this test
        String expectedMenu = "\n" +
                            "╔════════════════════════════════════╗\n" +
                            "║           MENU ADMIN SISTEM         ║\n" +
                            "╠════════════════════════════════════╣\n" +
                            "║ 1. Buat Voucher Baru               ║\n" +
                            "║ 2. Buat Promo Baru                 ║\n" +
                            "║ 3. Kelola Voucher                  ║\n" +
                            "║ 4. Kelola Promo                    ║\n" +
                            "║ 5. Kembali ke Menu Utama           ║\n" +
                            "╚════════════════════════════════════╝";
        
        assertEquals(expectedMenu, systemAdmin.showMenu());
    }
    
    @Test
    public void testHandleMenu_Exit() {
        setupSystemWithInput("5\n");
        systemAdmin.handleMenu();
        String output = outContent.toString();
        assertTrue(output.contains("MENU ADMIN"));
    }
    
    @Test
    public void testHandleGenerateVoucher() {
        setupSystemWithInput("1\n2023-12-31\n5\n");
        systemAdmin.handleMenu();
        String output = outContent.toString();
        assertTrue(output.contains("BUAT VOUCHER BARU"));
        assertTrue(output.contains("Voucher berhasil dibuat"));
    }
    
    @Test
    public void testHandleGeneratePromo() {
        setupSystemWithInput("2\n2023-12-31\n5\n");
        systemAdmin.handleMenu();
        String output = outContent.toString();
        assertTrue(output.contains("BUAT PROMO BARU"));
        assertTrue(output.contains("Promo berhasil dibuat"));
    }
    
    @Test
    public void testHandleLihatVoucher_LihatSemua() {
        setupSystemWithInput("3\n1\n5\n");
        systemAdmin.handleMenu();
        String output = outContent.toString();
        assertTrue(output.contains("║ Belum ada voucher yang tersedia"));
    }
    
    @Test
    public void testHandleLihatVoucher_LihatById() {
        // Setup a test voucher first
        setupSystemWithInput("1\n2023-12-31\n3\n2\nVCR001\n5\n");
        systemAdmin.handleMenu();
        String output = outContent.toString();
        assertTrue(output.contains("DETAIL VOUCHER"));
        assertTrue(output.contains("VCR001"));
    }
    
    @Test
    public void testHandleLihatVoucher_LihatById_NotFound() {
        setupSystemWithInput("3\n2\nVCR999\n5\n");
        systemAdmin.handleMenu();
        String output = outContent.toString();
        assertTrue(output.contains("Voucher dengan ID VCR999 tidak ditemukan"));
    }

    @Test
    public void testHandleLihatPromo_LihatSemua() {
        setupSystemWithInput("4\n1\n5\n");
        systemAdmin.handleMenu();
        String output = outContent.toString();
        assertTrue(output.contains("║ Belum ada promo yang tersedia"));
    }
    
    @Test
    public void testHandleLihatPromo_LihatById() {
        setupSystemWithInput("4\n2\nPRM123\n5\n");
        systemAdmin.handleMenu();
        String output = outContent.toString();
        assertTrue(output.contains("Promo dengan ID PRM123 tidak ditemukan"));
    }
    
    @Test
    public void testHandleMenu_InvalidCommand() {
        setupSystemWithInput("invalid\n5\n");
        systemAdmin.handleMenu();
        String output = outContent.toString();
        assertTrue(output.contains("Perintah tidak valid!"));
    }
}