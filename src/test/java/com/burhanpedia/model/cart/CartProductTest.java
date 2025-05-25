package com.burhanpedia.model.cart;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class CartProductTest {
    private CartProduct cartProduct;
    private UUID productId;
    
    @Before
    public void setUp() {
        productId = UUID.randomUUID();
        cartProduct = new CartProduct(productId, 5);
    }
    
    @Test
    public void testConstructor() {
        assertEquals(productId, cartProduct.getProductId());
        assertEquals(5, cartProduct.getProductAmount());
    }
    
    @Test
    public void testGetProductId() {
        assertEquals(productId, cartProduct.getProductId());
    }
    
    @Test
    public void testGetProductAmount() {
        assertEquals(5, cartProduct.getProductAmount());
    }
    
    @Test
    public void testConstructorWithZeroAmount() {
        CartProduct zeroProduct = new CartProduct(productId, 0);
        assertEquals(0, zeroProduct.getProductAmount());
    }
    
    @Test
    public void testConstructorWithNegativeAmount() {
        // Meskipun tidak ideal, kita tetap menguji perilaku saat ini
        CartProduct negativeProduct = new CartProduct(productId, -1);
        assertEquals(-1, negativeProduct.getProductAmount());
        // Catatan: Idealnya, kelas CartProduct harus memvalidasi jumlah tidak boleh negatif
    }
}