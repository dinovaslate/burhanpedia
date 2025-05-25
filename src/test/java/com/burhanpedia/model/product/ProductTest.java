package com.burhanpedia.model.product;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.burhanpedia.repository.ProductRepository;
import java.util.UUID;

public class ProductTest {
    private Product product;
    
    @Before
    public void setUp() {
        product = new Product("Test Product", 10, 50000);
    }
    
    @Test
    public void testProductConstructor() {
        assertNotNull(product.getProductId());
        assertEquals("Test Product", product.getProductName());
        assertEquals(10, product.getProductStock());
        assertEquals(50000, product.getProductPrice());
    }
    
    @Test
    public void testGetProductId() {
        UUID id = product.getProductId();
        assertNotNull(id);
    }
    
    @Test
    public void testGetProductName() {
        assertEquals("Test Product", product.getProductName());
    }
    
    @Test
    public void testGetProductStock() {
        assertEquals(10, product.getProductStock());
    }
    
    @Test
    public void testGetProductPrice() {
        assertEquals(50000, product.getProductPrice());
    }
    
    @Test
    public void testSetProductStock() {
        product.setProductStock(20);
        assertEquals(20, product.getProductStock());
    }
    
    @Test
    public void testSetProductPrice() {
        product.setProductPrice(75000);
        assertEquals(75000, product.getProductPrice());
    }
    
    @Test
    public void testAddProduct() {
        ProductRepository repo = new ProductRepository("Test Toko");
        int initialSize = repo.productList.size();
        
        product.addProduct(repo);
        
        assertEquals(initialSize + 1, repo.productList.size());
        assertTrue(repo.productList.contains(product));
    }
}