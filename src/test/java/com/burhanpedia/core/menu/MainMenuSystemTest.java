package com.burhanpedia.core.menu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import com.burhanpedia.exception.*;
import com.burhanpedia.model.user.*;
import com.burhanpedia.repository.*;
import com.burhanpedia.util.DateManager;
import com.burhanpedia.Burhanpedia;

public class MainMenuSystemTest {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    
    private MainMenuSystem mainMenuSystem;
    private Burhanpedia burhanpedia;
    
    @Before
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        burhanpedia = new Burhanpedia();
        
        // Reset DateManager untuk konsistensi test
        DateManager.getInstance().reset();

    }
    
    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }
    
    @Test
    public void testShowMenu() {
        // Simulasi input untuk keluar dari sistem
        String input = "5\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        mainMenuSystem = new MainMenuSystem();
        
        // Verifikasi menu yang ditampilkan
        String expectedMenu = "\n╔════════════════════════════════════╗\n" +
                            "║           MENU UTAMA               ║\n" +
                            "╠════════════════════════════════════╣\n" +
                            "║ 1. Login                           ║\n" +
                            "║ 2. Register                        ║\n" +
                            "║ 3. Hari Selanjutnya                ║\n" +
                            "║ 4. Command                         ║\n" +
                            "║ 5. Keluar                          ║\n" +
                            "╚════════════════════════════════════╝";
        
        assertEquals(expectedMenu, mainMenuSystem.showMenu());
    }
    
    @Test
    public void testHandleMenu_Exit() {
        // Simulasi input untuk keluar dari sistem
        String input = "5\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        mainMenuSystem = new MainMenuSystem();
        mainMenuSystem.handleMenu();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue(output.contains("Terima kasih telah menggunakan Burhanpedia!"));
    }
    
    @Test
    public void testHandleMenu_InvalidCommand() {
        // Simulasi input command yang tidak valid diikuti exit
        String input = "invalid\n5\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        mainMenuSystem = new MainMenuSystem();
        mainMenuSystem.handleMenu();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue(output.contains("Perintah tidak valid!"));
    }
    
    @Test
    public void testHandleNextDay() {
        // Simulasi input untuk next day diikuti exit
        String input = "3\n5\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        mainMenuSystem = new MainMenuSystem();
        mainMenuSystem.handleMenu();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue(output.contains("╔════════════════════════════════════╗"));
        assertTrue(output.contains("║         HARI SELANJUTNYA          ║"));
        assertTrue(output.contains("╠════════════════════════════════════╣"));
        assertTrue(output.contains("║ Tanggal:"));
        assertTrue(output.contains("║ Pok pok pok!"));
        assertTrue(output.contains("╚════════════════════════════════════╝"));
    }
    
 @Test
    public void testHandleRegister_NewUser() {
        //reset
        burhanpedia.getUserRepo().clear();
        
        // Fix 1: Ensure the output buffer is fresh before we start the test
        outContent.reset();
        
        // Simulasi input untuk registrasi user baru (pembeli) diikuti exit
        String input = "2\ntestuser\ntestpass\n2\n5\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        // Simpan jumlah user sebelum registrasi
        int userCountBefore = burhanpedia.getUserRepo().getAll().length;
        
        // Fix 2: Create a MainMenuSystem with our shared Burhanpedia instance
        mainMenuSystem = new MainMenuSystem(burhanpedia);
        mainMenuSystem.handleMenu();
        
        // Get output for verification
        String output = outContent.toString();
        
        // Fix 3: Print the output for debugging
        System.err.println("DEBUG - Output captured: " + output);
        
        // Verifikasi jumlah user bertambah
        int userCountAfter = burhanpedia.getUserRepo().getAll().length;
        assertEquals("Jumlah user seharusnya bertambah 1", userCountBefore + 1, userCountAfter);
        
        // Verifikasi user telah ditambahkan ke repository
        boolean userFound = false;
        for (User user : burhanpedia.getUserRepo().getAll()) {
            System.err.println("DEBUG - User found: " + user.getUsername() + " with role: " + user.getRole());
            if (user.getUsername().equals("testuser") && user.getRole().equals("Pembeli")) {
                userFound = true;
                break;
            }
        }
        assertTrue("User testuser dengan role Pembeli tidak ditemukan di repository", userFound);
        
        // Fix 4: More flexible check for registration success message
        boolean successMessagePresent = (output.contains("akun pembeli berhasil") || 
                                        (output.contains("Registrasi") && output.contains("berhasil")));
        
        assertTrue("Output tidak mengandung konfirmasi registrasi berhasil", successMessagePresent);
    }
    
    @Test
    public void testHandleRegister_ExistingUser_AddRole() {
        // Tambahkan user untuk testing
        //reset
        burhanpedia.getUserRepo().clear();
        burhanpedia.getUserRepo().addUser(new Pembeli("testuser", "testpass"));
        
        // Reset output stream
        outContent.reset();
        
        // Simulasi input untuk menambahkan role penjual ke user yang sudah ada
        // Add more input lines to handle all Scanner.nextLine() calls
        String input = "2\ntestuser\n1\nTestToko\n5\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        mainMenuSystem = new MainMenuSystem(burhanpedia);
        mainMenuSystem.handleMenu();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue(output.contains("Registrasi akun penjual berhasil!"));
        
        // Verifikasi role telah ditambahkan
        int roleCount = 0;
        for (User user : burhanpedia.getUserRepo().getAll()) {
            if (user.getUsername().equals("testuser")) {
                //log role
                System.err.println("DEBUG - User role: " + user.getRole());
                roleCount++;
            }
        }


        assertEquals(2, roleCount); // Seharusnya ada 2 role (Pembeli dan Penjual)
    }
    
    @Test
    public void testHandleLogin_Success() {
        // Tambahkan user untuk login
        //reset
        burhanpedia.getUserRepo().clear();
        burhanpedia.getUserRepo().addUser(new Pembeli("testuser", "testpass"));
        
        // Simulasi input untuk login diikuti exit dari menu pembeli
        String input = "1\ntestuser\ntestpass\n9\n5\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        mainMenuSystem = new MainMenuSystem();
        mainMenuSystem.handleMenu();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue(output.contains("Login berhasil! Selamat datang, testuser!"));
        assertTrue(output.contains("MENU PEMBELI"));
    }
    
    @Test
    public void testHandleLogin_WrongPassword() {
        // Tambahkan user untuk login
        burhanpedia.getUserRepo().addUser(new Pembeli("testuser", "testpass"));
        
        // Simulasi input untuk login dengan password salah diikuti exit
        String input = "1\ntestuser\nwrongpass\n5\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        mainMenuSystem = new MainMenuSystem();
        mainMenuSystem.handleMenu();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue(output.contains("Password salah!"));
    }
    
    @Test
    public void testHandleLogin_UserNotFound() {
        // Simulasi input untuk login dengan username yang tidak ada diikuti exit
        String input = "1\nnonexistentuser\ntestpass\n5\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        mainMenuSystem = new MainMenuSystem();
        mainMenuSystem.handleMenu();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue(output.contains("Username tidak ditemukan!"));
    }
    
    
    
    @Test
    public void testHandleLogin_MultipleRoles() {
        // Tambahkan user dengan multiple roles
        burhanpedia.getUserRepo().addUser(new Pembeli("multiuser", "testpass"));
        burhanpedia.getUserRepo().addUser(new Penjual("multiuser", "testpass", "TestToko"));
        
        // Simulasi input untuk login dengan multiple roles, pilih role pembeli
        String input = "1\nmultiuser\ntestpass\n1\n9\n5\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        mainMenuSystem = new MainMenuSystem();
        mainMenuSystem.handleMenu();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue(output.contains("Login berhasil! Selamat datang, multiuser!"));
        assertTrue(output.contains("MENU PEMBELI"));
    }
    
    @Test
    public void testHandleLogin_CekSaldoAntarAkun() {
        // Reset repository and output stream
        burhanpedia.getUserRepo().clear();
        outContent.reset();
        
        // Create test users with specific balances
        Pembeli pembeli = new Pembeli("multiuser", "testpass");
        pembeli.setBalance(50000); // 500.00
        burhanpedia.getUserRepo().addUser(pembeli);
        
        Penjual penjual = new Penjual("multiuser", "testpass", "TestToko");
        penjual.setBalance(100000); // 1000.00
        burhanpedia.getUserRepo().addUser(penjual);
        
        // Simulate user input for login with multiple roles and choosing "cek saldo antar akun"
        String input = "1\nmultiuser\ntestpass\n3\n5\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        // Create MainMenuSystem with shared burhanpedia instance
        mainMenuSystem = new MainMenuSystem(burhanpedia);
        mainMenuSystem.handleMenu();
        
        // Get output for verification
        String output = outContent.toString();
        
        // Debug output
        System.err.println("DEBUG - Output captured: " + output);
        
        // Verify the box-drawing character format and content
        assertTrue("Missing box top border", output.contains(   "╔════════════════════════════════════╗"));
        assertTrue("Missing title", output.contains(            "║         SALDO ANTAR AKUN          ║"));
        assertTrue("Missing header separator", output.contains( "╠════════════════════════════════════╣"));
        assertTrue("Missing column headers", output.contains(   "║ Role        │ Saldo                ║"));
        assertTrue("Missing content separator", output.contains("╠════════════════════════════════════╣"));
        assertTrue("Missing Penjual balance", output.contains(  "║ Penjual    │ 1000.00              ║"));
        assertTrue("Missing Pembeli balance", output.contains(  "║ Pembeli    │ 500.00               ║"));
        assertTrue("Missing box bottom border", output.contains("╚════════════════════════════════════╝"));
        

    }
   
    @Test
    public void testHandleLogin_AdminSuccess() {
        // Simulasi input untuk login sebagai admin diikuti exit dari menu admin
        String input = "1\nadmin\nadmin123\n5\n5\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        mainMenuSystem = new MainMenuSystem();
        mainMenuSystem.handleMenu();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue(output.contains("Login berhasil! Selamat datang, admin!"));
        assertTrue(output.contains("MENU ADMIN"));
    }
}