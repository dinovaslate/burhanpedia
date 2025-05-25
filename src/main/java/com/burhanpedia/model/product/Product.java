package com.burhanpedia.model.product;

import com.burhanpedia.annotation.ProductValidation;
import com.burhanpedia.repository.ProductRepository;

import java.util.UUID;

public class Product {
    private UUID productId;
    private String name;
    
    @ProductValidation(allowNegative = false, message = "Stock cannot be negative")
    private int stok;
    
    @ProductValidation(allowNegative = false, message = "Price must be positive")
    private long price;
    
    public Product(String name, int stok, long price) {
        this.productId = UUID.randomUUID();
        this.name = name;
        this.stok = stok;
        this.price = price;
    }
    
    public UUID getProductId() {
        return productId;
    }
    
    public String getProductName() {
        return name;
    }
    
    public int getProductStock() {
        return stok;
    }
    
    public long getProductPrice() {
        return price;
    }
    
    public void setProductStock(int stok) {
        this.stok = stok;
    }
    
    public void setProductPrice(long price) {
        this.price = price;
    }

    public void addProduct(ProductRepository productRepository) {
        productRepository.productList.add(this);
        productRepository.saveProductsToCsv();
    }
}