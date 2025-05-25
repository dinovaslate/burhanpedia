package com.burhanpedia.model.user;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class PengirimTest {
    private Pengirim pengirim;
    
    @Before
    public void setUp() {
        pengirim = new Pengirim("pengirim", "password123");
    }
    
    @Test
    public void testPengirimConstructor() {
        assertEquals("pengirim", pengirim.getUsername());
        assertEquals("password123", pengirim.getPassword());
        assertEquals("Pengirim", pengirim.getRole());
    }
}