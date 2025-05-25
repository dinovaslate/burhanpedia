package com.burhanpedia.model.user;

import com.burhanpedia.model.cart.Cart;

public class Pembeli extends User {
    private Cart keranjang;
    
    public Pembeli(String username, String password) {
        super(username, password, "Pembeli");
        this.keranjang = new Cart();
    }
    
    public Cart getCart() {
        return keranjang;
    }
}