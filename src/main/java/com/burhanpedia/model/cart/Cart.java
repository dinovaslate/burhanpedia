package com.burhanpedia.model.cart;

import java.util.ArrayList;
import java.util.UUID;

public class Cart {
    private ArrayList<CartProduct> keranjang;
    
    public Cart() {
        this.keranjang = new ArrayList<>();
    }
    
    public void addToCart(UUID productId, int amount) {
        boolean found = false;
        for (CartProduct product : keranjang) {
            if (product.getProductId().equals(productId)) {
                // Update existing product
                int newAmount = product.getProductAmount() + amount;
                // Remove and add with new amount
                keranjang.remove(product);
                keranjang.add(new CartProduct(productId, newAmount));
                found = true;
                break;
            }
        }
        
        if (!found) {
            keranjang.add(new CartProduct(productId, amount));
        }
    }
    
    public String deleteFromCart(UUID productId) {
        for (CartProduct product : keranjang) {
            if (product.getProductId().equals(productId)) {
                keranjang.remove(product);
                return "Product removed from cart";
            }
        }
        return "Product not found in cart";
    }
    
    public CartProduct[] getCartContent() {
        return keranjang.toArray(new CartProduct[0]);
    }
    
    public boolean isEmpty() {
        return keranjang.isEmpty();
    }

    public void clear() {
        keranjang.clear();
    }
}