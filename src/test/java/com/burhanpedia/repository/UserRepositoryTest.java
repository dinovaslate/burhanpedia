package com.burhanpedia.repository;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import com.burhanpedia.model.user.Pembeli;
import com.burhanpedia.model.user.Penjual;
import com.burhanpedia.model.user.Pengirim;
import com.burhanpedia.model.user.User;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public class UserRepositoryTest {
    
    private UserRepository userRepo;
    private Pembeli testPembeli;
    private Penjual testPenjual;
    private Pengirim testPengirim;
    
    @Before
    public void setUp() {
        userRepo = new UserRepository();
        
        // Buat user test
        testPembeli = new Pembeli("testPembeli", "password");
        testPembeli.setBalance(100000);
        
        testPenjual = new Penjual("testPenjual", "password", "testToko");
        testPenjual.setBalance(200000);
        
        testPengirim = new Pengirim("testPengirim", "password");
        testPengirim.setBalance(50000);
    }
    
    @After
    public void tearDown() {
        // Hapus file CSV test jika ada
        File file = new File("data/users.csv");
        if (file.exists()) {
            file.delete();
        }
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(userRepo);
    }
    
    @Test
    public void testAddUser() {
        userRepo.addUser(testPembeli);
        
        // Verifikasi user ditambahkan
        User[] users = userRepo.getAll();
        assertTrue(users.length > 0);
        
        boolean found = false;
        for (User user : users) {
            if (user.getUsername().equals("testPembeli")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
    
    @Test
    public void testGetUserById_ExistingUser() {
        userRepo.addUser(testPembeli);
        
        // Test getUserById
        Optional<User> user = userRepo.getUserById(testPembeli.getId());
        assertTrue(user.isPresent());
        assertEquals(testPembeli.getId(), user.get().getId());
        assertEquals("testPembeli", user.get().getUsername());
    }
    
    @Test
    public void testGetUserById_NonExistingUser() {
        Optional<User> user = userRepo.getUserById(UUID.randomUUID());
        assertFalse(user.isPresent());
    }
    
    @Test
    public void testGetUserByName_ExistingUser() {
        userRepo.addUser(testPembeli);
        
        // Test getUserByName
        Optional<User> user = userRepo.getUserByName("testPembeli");
        assertTrue(user.isPresent());
        assertEquals("testPembeli", user.get().getUsername());
        assertEquals("password", user.get().getPassword());
    }
    
    @Test
    public void testGetUserByName_NonExistingUser() {
        Optional<User> user = userRepo.getUserByName("nonexistent");
        assertFalse(user.isPresent());
    }
    
    @Test
    public void testGetAll() {
        // Tambahkan beberapa user
        userRepo.addUser(testPembeli);
        userRepo.addUser(testPenjual);
        userRepo.addUser(testPengirim);
        
        // Test getAll
        User[] users = userRepo.getAll();
        assertNotNull(users);
        assertTrue(users.length >= 3);
    }
    
    @Test
    public void testGetUserRoles_SingleRole() {
        userRepo.addUser(testPembeli);
        
        // Test getUserRoles
        String[] roles = userRepo.getUserRoles("testPembeli");
        assertNotNull(roles);
        assertEquals(1, roles.length);
        assertEquals("Pembeli", roles[0]);
    }
    
    @Test
    public void testGetUserRoles_MultipleRoles() {
        // Tambahkan user dengan username yang sama tapi role berbeda
        userRepo.addUser(testPembeli);
        
        Penjual anotherRole = new Penjual("testPembeli", "password", "anotherToko");
        userRepo.addUser(anotherRole);
        
        // Test getUserRoles
        String[] roles = userRepo.getUserRoles("testPembeli");
        assertNotNull(roles);
        assertEquals(2, roles.length);
        
        // Verifikasi kedua role ada
        boolean hasPembeli = false;
        boolean hasPenjual = false;
        
        for (String role : roles) {
            if (role.equals("Pembeli")) hasPembeli = true;
            if (role.equals("Penjual")) hasPenjual = true;
        }
        
        assertTrue(hasPembeli);
        assertTrue(hasPenjual);
    }
    
    @Test
    public void testUpdateUser() {
        userRepo.addUser(testPembeli);
        
        // Ubah balance
        testPembeli.setBalance(150000);
        
        // Update user
        boolean result = userRepo.updateUser(testPembeli);
        assertTrue(result);
        
        // Verifikasi perubahan
        Optional<User> updatedUser = userRepo.getUserByName("testPembeli");
        assertTrue(updatedUser.isPresent());
        assertEquals(150000, updatedUser.get().getBalance());
    }
    
    @Test
    public void testRemoveUser() {
        userRepo.addUser(testPembeli);
        
        // Hapus user
        boolean result = userRepo.removeUser(testPembeli.getId());
        assertTrue(result);
        
        // Verifikasi user dihapus
        Optional<User> user = userRepo.getUserById(testPembeli.getId());
        assertFalse(user.isPresent());
    }
    
    @Test
    public void testSaveAndLoadUsersToCsv() {
        // Tambahkan user
        userRepo.addUser(testPembeli);
        userRepo.addUser(testPenjual);
        userRepo.addUser(testPengirim);
        
        // Buat repository baru untuk memuat dari CSV
        UserRepository newRepo = new UserRepository();
        
        // Verifikasi user dimuat dengan benar
        boolean pembeliFound = false;
        boolean penjualFound = false;
        boolean pengirimFound = false;
        
        for (User user : newRepo.getAll()) {
            if (user.getUsername().equals("testPembeli")) {
                pembeliFound = true;
                assertEquals(100000, user.getBalance());
            }
            if (user.getUsername().equals("testPenjual")) {
                penjualFound = true;
                assertEquals(200000, user.getBalance());
                assertEquals("testToko", ((Penjual) user).getNamaToko());
            }
            if (user.getUsername().equals("testPengirim")) {
                pengirimFound = true;
                assertEquals(50000, user.getBalance());
            }
        }
        
        assertTrue(pembeliFound);
        assertTrue(penjualFound);
        assertTrue(pengirimFound);
    }
}