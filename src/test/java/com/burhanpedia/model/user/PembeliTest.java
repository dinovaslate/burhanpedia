package com.burhanpedia.model.user;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.burhanpedia.model.cart.Cart;

public class PembeliTest {
    private Pembeli pembeli;
    
    @Before
    public void setUp() {
        pembeli = new Pembeli("pembeli", "password123");
    }
    
    @Test
    public void testPembeliConstructor() {
        assertEquals("pembeli", pembeli.getUsername());
        assertEquals("password123", pembeli.getPassword());
        assertEquals("Pembeli", pembeli.getRole());
        assertNotNull(pembeli.getCart());
    }
    
    @Test
    public void testGetCart() {
        Cart cart = pembeli.getCart();
        assertNotNull(cart);
    }
}