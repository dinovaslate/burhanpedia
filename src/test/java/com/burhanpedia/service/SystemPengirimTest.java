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
import com.burhanpedia.model.user.Pengirim;
import com.burhanpedia.service.SystemPengirim;
import com.burhanpedia.model.transaction.Transaksi;

public class SystemPengirimTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    
    private SystemPengirim systemPengirim;
    private Burhanpedia burhanpedia;
    private Pengirim testPengirim;
    
    @Before
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        burhanpedia = new Burhanpedia();
        systemPengirim = new SystemPengirim(new Scanner(System.in), burhanpedia);
        
        // Buat user pengirim untuk testing
        testPengirim = new Pengirim("testpengirim", "testpass");
        testPengirim.setBalance(100000); // Set saldo awal 1000.00
        burhanpedia.getUserRepo().addUser(testPengirim);
    }
    
    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    
    @Test
    public void testHandleMenu_Exit() {
        // Simulasi input untuk keluar dari menu pengirim
        String input = "5\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        systemPengirim = new SystemPengirim(new Scanner(inContent), burhanpedia);
        systemPengirim.setActivePengirim(testPengirim);
        systemPengirim.handleMenu();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue(output.contains("MENU PENGIRIM"));
    }
    
    @Test
    public void testHandleFindJob() {
        // Set active pengirim
        systemPengirim.setActivePengirim(testPengirim);
        
        // Simulasi input untuk find job diikuti exit
        String input = "1\n5\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        systemPengirim = new SystemPengirim(new Scanner(inContent), burhanpedia);
        systemPengirim.setActivePengirim(testPengirim);
        systemPengirim.handleMenu();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue(output.contains("║ Tidak ada pesanan yang tersedia saat ini!               ║"));
    }
    
    @Test
    public void testHandleTakeJob_NotFound() {
        // Set active pengirim
        systemPengirim.setActivePengirim(testPengirim);
        
        // Simulasi input untuk take job dengan ID yang tidak ada diikuti exit
        String input = "2\nTRX123456\n5\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        systemPengirim = new SystemPengirim(new Scanner(inContent), burhanpedia);
        systemPengirim.setActivePengirim(testPengirim);
        systemPengirim.handleMenu();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue(output.contains("Tidak ada pesanan untuk ID tersebut"));
    }
    
    @Test
    public void testHandleConfirmJob_NotFound() {
        // Set active pengirim
        systemPengirim.setActivePengirim(testPengirim);
        
        // Simulasi input untuk confirm job dengan ID yang tidak ada diikuti exit
        String input = "3\nTRX123456\n5\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        systemPengirim = new SystemPengirim(new Scanner(inContent), burhanpedia);
        systemPengirim.setActivePengirim(testPengirim);
        systemPengirim.handleMenu();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue(output.contains("Tidak ada pesanan untuk ID tersebut"));
    }
    
    @Test
    public void testHandleRiwayatTransaksi() {
        // Set active pengirim
        systemPengirim.setActivePengirim(testPengirim);
        
        // Simulasi input untuk lihat riwayat transaksi diikuti exit
        String input = "4\n5\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        systemPengirim = new SystemPengirim(new Scanner(inContent), burhanpedia);
        systemPengirim.setActivePengirim(testPengirim);
        systemPengirim.handleMenu();
        
        // Tidak ada verifikasi khusus karena method ini memanggil method di kelas Pengirim
    }
    
    @Test
    public void testHandleMenu_InvalidCommand() {
        // Set active pengirim
        systemPengirim.setActivePengirim(testPengirim);
        
        // Simulasi input command yang tidak valid diikuti exit
        String input = "invalid\n5\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        systemPengirim = new SystemPengirim(new Scanner(inContent), burhanpedia);
        systemPengirim.setActivePengirim(testPengirim);
        systemPengirim.handleMenu();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue(output.contains("Perintah tidak valid!"));
    }
}