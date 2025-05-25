package com.burhanpedia.model.user;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


public class UserTest {
    private TestUser user;
    
    // Kelas konkrit untuk menguji kelas abstrak User
    private class TestUser extends User {
        public TestUser(String username, String password) {
            super(username, password, "TestRole");
        }
    }
    
    @Before
    public void setUp() {
        user = new TestUser("testuser", "password123");
    }
    
    @Test
    public void testUserConstructor() {
        assertNotNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("TestRole", user.getRole());
        assertEquals(0, user.getBalance());
    }
    
    @Test
    public void testSetUsername() {
        user.setUsername("newusername");
        assertEquals("newusername", user.getUsername());
    }
    
    @Test
    public void testSetPassword() {
        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());
    }
    
    @Test
    public void testSetBalance() {
        user.setBalance(1000);
        assertEquals(1000, user.getBalance());
    }
}