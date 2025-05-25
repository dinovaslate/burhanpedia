package com.burhanpedia.model.cart;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class CartTest {
    private Cart cart;
    private UUID productId1;
    private UUID productId2;
    
    @Before
    public void setUp() {
        cart = new Cart();
        productId1 = UUID.randomUUID();
        productId2 = UUID.randomUUID();
    }
    
    @Test
    public void testConstructor() {
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getCartContent().length);
    }
    
    @Test
    public void testAddToCart() {
        cart.addToCart(productId1, 3);
        
        CartProduct[] cartContent = cart.getCartContent();
        assertEquals(1, cartContent.length);
        assertEquals(productId1, cartContent[0].getProductId());
        assertEquals(3, cartContent[0].getProductAmount());
    }
    
    @Test
    public void testAddToCartMultipleProducts() {
        cart.addToCart(productId1, 3);
        cart.addToCart(productId2, 5);
        
        CartProduct[] cartContent = cart.getCartContent();
        assertEquals(2, cartContent.length);
        
        // Verifikasi produk pertama
        boolean foundProduct1 = false;
        boolean foundProduct2 = false;
        
        for (CartProduct product : cartContent) {
            if (product.getProductId().equals(productId1)) {
                assertEquals(3, product.getProductAmount());
                foundProduct1 = true;
            } else if (product.getProductId().equals(productId2)) {
                assertEquals(5, product.getProductAmount());
                foundProduct2 = true;
            }
        }
        
        assertTrue("Produk 1 tidak ditemukan di keranjang", foundProduct1);
        assertTrue("Produk 2 tidak ditemukan di keranjang", foundProduct2);
    }
    
    @Test
    public void testAddToCartExistingProduct() {
        cart.addToCart(productId1, 3);
        cart.addToCart(productId1, 2);
        
        CartProduct[] cartContent = cart.getCartContent();
        assertEquals(1, cartContent.length);
        assertEquals(productId1, cartContent[0].getProductId());
        assertEquals(5, cartContent[0].getProductAmount());
    }
    
    @Test
    public void testDeleteFromCartExistingProduct() {
        cart.addToCart(productId1, 3);
        
        String result = cart.deleteFromCart(productId1);
        assertEquals("Product removed from cart", result);
        assertTrue(cart.isEmpty());
    }
    
    @Test
    public void testDeleteFromCartNonExistingProduct() {
        String result = cart.deleteFromCart(productId1);
        assertEquals("Product not found in cart", result);
    }
    
    @Test
    public void testDeleteFromCartWithMultipleProducts() {
        cart.addToCart(productId1, 3);
        cart.addToCart(productId2, 5);
        
        String result = cart.deleteFromCart(productId1);
        assertEquals("Product removed from cart", result);
        
        CartProduct[] cartContent = cart.getCartContent();
        assertEquals(1, cartContent.length);
        assertEquals(productId2, cartContent[0].getProductId());
        assertEquals(5, cartContent[0].getProductAmount());
    }
    
    @Test
    public void testIsEmpty() {
        assertTrue(cart.isEmpty());
        
        cart.addToCart(productId1, 3);
        assertFalse(cart.isEmpty());
        
        cart.deleteFromCart(productId1);
        assertTrue(cart.isEmpty());
    }
    
    @Test
    public void testGetCartContent() {
        assertEquals(0, cart.getCartContent().length);
        
        cart.addToCart(productId1, 3);
        assertEquals(1, cart.getCartContent().length);
        
        cart.addToCart(productId2, 5);
        assertEquals(2, cart.getCartContent().length);
    }
    
    @Test
    public void testAddToCartWithZeroAmount() {
        cart.addToCart(productId1, 0);
        
        CartProduct[] cartContent = cart.getCartContent();
        assertEquals(1, cartContent.length);
        assertEquals(0, cartContent[0].getProductAmount());
    }
}