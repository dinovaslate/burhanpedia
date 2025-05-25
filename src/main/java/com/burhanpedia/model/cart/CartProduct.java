package com.burhanpedia.model.cart;

import java.util.UUID;

public class CartProduct {
    private UUID productId;
    private int amount;
    
    public CartProduct(UUID productId, int amount) {
        this.productId = productId;
        this.amount = amount;
    }
    
    public UUID getProductId() {
        return productId;
    }
    
    public int getProductAmount() {
        return amount;
    }
}