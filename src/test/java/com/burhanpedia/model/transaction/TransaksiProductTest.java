package com.burhanpedia.model.transaction;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class TransaksiProductTest {
    private TransaksiProduct transaksiProduct;
    private UUID productId;
    private int amount;
    
    @Before
    public void setUp() {
        productId = UUID.randomUUID();
        amount = 5;
        transaksiProduct = new TransaksiProduct(productId, amount);
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(transaksiProduct);
        assertEquals(productId, transaksiProduct.getProductId());
        assertEquals(amount, transaksiProduct.getProductAmount());
    }
    
    @Test
    public void testGetProductId() {
        assertEquals(productId, transaksiProduct.getProductId());
    }
    
    @Test
    public void testGetProductAmount() {
        assertEquals(amount, transaksiProduct.getProductAmount());
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testSetProductId() {
        UUID newProductId = UUID.randomUUID();
        transaksiProduct.setProductId(newProductId);
    }
    
    @Test
    public void testConstructorWithZeroAmount() {
        TransaksiProduct zeroProduct = new TransaksiProduct(productId, 0);
        assertEquals(0, zeroProduct.getProductAmount());
    }
    
    @Test
    public void testConstructorWithNegativeAmount() {
        // Meskipun tidak ideal, kita tetap menguji perilaku saat ini
        TransaksiProduct negativeProduct = new TransaksiProduct(productId, -1);
        assertEquals(-1, negativeProduct.getProductAmount());
        // Catatan: Idealnya, kelas TransaksiProduct harus memvalidasi jumlah tidak boleh negatif
    }
    
    @Test
    public void testEqualProductIds() {
        TransaksiProduct product1 = new TransaksiProduct(productId, 5);
        TransaksiProduct product2 = new TransaksiProduct(productId, 10);
        
        assertEquals(product1.getProductId(), product2.getProductId());
        assertNotEquals(product1.getProductAmount(), product2.getProductAmount());
    }
}