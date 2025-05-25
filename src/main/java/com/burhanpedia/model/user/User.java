package com.burhanpedia.model.user;

import com.burhanpedia.model.transaction.Transaksi;
import java.text.SimpleDateFormat;
import java.util.UUID;

public abstract class User {
    private UUID id;
    private String username;
    private String password;
    private String role;
    private long balance;

    public User(String username, String password, String role) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.role = role;
        this.balance = 0;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public long getBalance() {
        return balance;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public void getRiwayatTransaksi(Transaksi[] transaksis) {
        System.out.println("===================== RIWAYAT TRANSAKSI ======================");
        System.out.println("ID Transaksi\tTanggal\tNominal\tKeterangan");
        
        boolean empty = true;
        for (Transaksi transaksi : transaksis) {
            if (transaksi != null) {
                if ((role.equals("Pembeli") && transaksi.getNamePembeli().equals(username)) ||
                    (role.equals("Penjual") && transaksi.getNamePenjual().equals(username)) ||
                    (role.equals("Pengirim") && transaksi.getNamePengirim() != null && 
                     transaksi.getNamePengirim().equals(username))) {
                    
                    String nominal = "";
                    String keterangan = "Pembelian produk";
                    
                    if (role.equals("Pembeli")) {
                        nominal = "- " + transaksi.getTotalHarga();
                    } else if (role.equals("Penjual")) {
                        nominal = "+ " + transaksi.getTotalHarga();
                    } else if (role.equals("Pengirim")) {
                        nominal = "+ " + transaksi.getBiayaOngkir();
                        keterangan = "Pengiriman barang";
                    }
                    
                    SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
                    String formattedDate = formatter.format(transaksi.getLatestStatusDate());
                    
                    System.out.println(transaksi.getId() + "\t" + formattedDate + "\t" + nominal + "\t" + keterangan);
                    empty = false;
                }
            }
        }
        
        if (empty) {
            System.out.println("Riwayat transaksi masih kosong!");
        }
        
        System.out.println("==============================================================");
    }
}