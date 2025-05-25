package com.burhanpedia.annotation;

import static org.junit.Assert.*;
import org.junit.Test;

import com.burhanpedia.model.product.Product;
public class ProductValidatorTest {
    
    @Test
    public void testValidateValidProduct() {
        // Create a valid product
        Product product = new Product("Test Product", 10, 100);
        
        // Validate
        String result = ProductValidator.validate(product);
        
        // Should be null (no validation errors)
        assertNull(result);
    }
    
    @Test
    public void testValidateNegativeStock() {
        // Create product with negative stock
        Product product = new Product("Test Product", -5, 100);
        
        // Set negative stock (bypass constructor validation)
        product.setProductStock(-5);
        
        // Validate
        String result = ProductValidator.validate(product);
        
        // Should return error message
        assertNotNull(result);
        assertEquals("Stock cannot be negative", result);
    }
    
    @Test
    public void testValidateNegativePrice() {
        // Create product with negative price
        Product product = new Product("Test Product", 10, 100);
        
        // Set negative price (bypass constructor validation)
        product.setProductPrice(-50);
        
        // Validate
        String result = ProductValidator.validate(product);
        
        // Should return error message
        assertNotNull(result);
        assertEquals("Price must be positive", result);
    }
    
    @Test
    public void testValidateNullProduct() {
        // Validate null product
        String result = ProductValidator.validate(null);
        
        // Should return error message
        assertNotNull(result);
        assertEquals("Product cannot be null", result);
    }
}