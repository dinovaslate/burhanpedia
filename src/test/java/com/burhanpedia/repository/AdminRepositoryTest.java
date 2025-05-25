package com.burhanpedia.repository;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.burhanpedia.model.user.Admin;

public class AdminRepositoryTest {
    
    private AdminRepository adminRepo;
    
    @Before
    public void setUp() {
        adminRepo = new AdminRepository();
    }
    
    @Test
    public void testConstructor() {
        // Verifikasi bahwa constructor menambahkan admin default
        Admin[] admins = adminRepo.getAll();
        assertNotNull(admins);
        assertEquals(1, admins.length);
        assertEquals("admin", admins[0].getUsername());
        assertEquals("admin123", admins[0].getPassword());
    }
    
    @Test
    public void testGetUserByName_ExistingUser() {
        // Test mendapatkan admin yang ada
        Admin admin = adminRepo.getUserByName("admin");
        assertNotNull(admin);
        assertEquals("admin", admin.getUsername());
        assertEquals("admin123", admin.getPassword());
    }
    
    @Test
    public void testGetUserByName_NonExistingUser() {
        // Test mendapatkan admin yang tidak ada
        Admin admin = adminRepo.getUserByName("nonexistent");
        assertNull(admin);
    }
    
    @Test
    public void testGetAll() {
        // Test mendapatkan semua admin
        Admin[] admins = adminRepo.getAll();
        assertNotNull(admins);
        assertEquals(1, admins.length);
    }
}