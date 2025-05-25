package com.burhanpedia;

import com.burhanpedia.repository.*;
import com.burhanpedia.model.transaction.Transaksi;
import java.util.UUID;

public class Burhanpedia {
    private UserRepository userRepo;
    private AdminRepository adminRepo;
    private VoucherRepository voucherRepo;
    private PromoRepository promoRepo;
    private TransaksiRepository transaksiRepo;
    private ProductRepository produkRepo;
    private String namaToko;
    
    public Burhanpedia() {
        this.userRepo = new UserRepository();
        this.adminRepo = new AdminRepository();
        this.voucherRepo = new VoucherRepository();
        this.promoRepo = new PromoRepository();
        this.produkRepo = new ProductRepository( namaToko);
        this.transaksiRepo = new TransaksiRepository(voucherRepo, promoRepo, produkRepo);
    }
    
    public UserRepository getUserRepo() {
        return userRepo;
    }
    
    public AdminRepository getAdminRepo() {
        return adminRepo;
    }
    
    public VoucherRepository getVoucherRepo() {
        return voucherRepo;
    }
    
    public PromoRepository getPromoRepo() {
        return promoRepo;
    }
    
    public TransaksiRepository getTransaksiRepo() {
        return transaksiRepo;
    }
    
    public ProductRepository getProdukRepo() {
        return produkRepo;
    }
}