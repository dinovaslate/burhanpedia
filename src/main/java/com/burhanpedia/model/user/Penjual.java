package com.burhanpedia.model.user;

import com.burhanpedia.repository.ProductRepository;

public class Penjual extends User {
    private ProductRepository productRepo;
    
    public Penjual(String username, String password, String namaToko) {
        super(username, password, "Penjual");
        this.productRepo = new ProductRepository(namaToko);
        if (this.getBalance() == 0) {
            this.setBalance(0);
        }
    }
    
    public ProductRepository getRepo() {
        return productRepo;

    }
    public void setRepo(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    public String getNamaToko() {
        return productRepo.getNamaToko();
    }

    public void addRevenue(long amount) {
        if (amount > 0) {
            this.setBalance(this.getBalance() + amount);
        }
    }
    public String getFormattedBalance() {
        return String.format("%.2f", this.getBalance() / 100.0);
    }
    public boolean hasSufficientBalance(long amount) {
        return this.getBalance() >= amount;
    }
    public boolean withdraw(long amount) {
        if (hasSufficientBalance(amount)) {
            this.setBalance(this.getBalance() - amount);
            return true;
        }
        return false;
    }
}