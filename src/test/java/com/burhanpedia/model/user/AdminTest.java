package com.burhanpedia.model.user;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class AdminTest {
    private Admin admin;
    
    @Before
    public void setUp() {
        admin = new Admin("admin", "admin123");
    }
    
    @Test
    public void testAdminConstructor() {
        assertEquals("admin", admin.getUsername());
        assertEquals("admin123", admin.getPassword());
    }
    
    @Test
    public void testGetUsername() {
        assertEquals("admin", admin.getUsername());
    }
    
    @Test
    public void testGetPassword() {
        assertEquals("admin123", admin.getPassword());
    }
}