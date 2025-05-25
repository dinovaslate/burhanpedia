package com.burhanpedia.model.transaction;

import java.util.UUID;

public class TransaksiProduct {
    private final UUID productId;
    private int amount;
    
    public TransaksiProduct(UUID productId, int amount) {
        this.productId = productId;
        this.amount = amount;
    }
    
    public UUID getProductId() {
        return productId;
    }
    
    public int getProductAmount() {
        return amount;
    }

    public void setProductId(UUID productId) {
        throw new UnsupportedOperationException("Product ID cannot be changed after creation");
    }
}