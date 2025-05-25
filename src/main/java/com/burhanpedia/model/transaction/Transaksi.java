package com.burhanpedia.model.transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Transaksi {
    private String id;
    private String namePembeli;
    private String namePenjual;
    private String namePengirim;
    private String idDiskon;
    private ArrayList<TransactionStatus> historyStatus;
    private ArrayList<TransaksiProduct> produkDibeli;
    private String jenisTransaksi;
    private long biayaOngkir;
    private long totalHarga;
    
    public Transaksi(String namePembeli, String namePenjual, String jenisTransaksi) {
        // Generate ID like TRX202503190001
        this.id = "TRX" + generateTransactionId();
        this.namePembeli = namePembeli;
        this.namePenjual = namePenjual;
        this.namePengirim = null;
        this.idDiskon = null;
        this.historyStatus = new ArrayList<>();
        this.produkDibeli = new ArrayList<>();
        this.jenisTransaksi = jenisTransaksi;
        
        // Set ongkir based on jenis transaksi
        if ("Instant".equals(jenisTransaksi)) {
            this.biayaOngkir = 20000;
        } else if ("Next Day".equals(jenisTransaksi)) {
            this.biayaOngkir = 15000;
        } else { // Regular
            this.biayaOngkir = 10000;
        }
        
        // Initialize with "Sedang Dikemas" status
        this.historyStatus.add(new TransactionStatus(new Date(), "Sedang Dikemas"));
    }
    
    private String generateTransactionId() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateStr = dateFormat.format(new Date());
        // Add random string suffix to ensure ID length is 16 characters (8 for date + 8 for random)
        // which makes total ID length 19 characters including "TRX" prefix
        return dateStr + String.format("%08d", (int)(Math.random() * 100000000));
    }
    
    public String getId() {
        return id;
    }
    
    public String getNamePembeli() {
        return namePembeli;
    }
    
    public String getNamePenjual() {
        return namePenjual;
    }
    
    public String getNamePengirim() {
        return namePengirim;
    }
    
    public void setNamePengirim(String namePengirim) {
        this.namePengirim = namePengirim;
    }
    
    public String getIdDiskon() {
        return idDiskon;
    }
    
    public void setIdDiskon(String idDiskon) {
        this.idDiskon = idDiskon;
    }
    
    public TransactionStatus[] getHistoryStatus() {
        return historyStatus.toArray(new TransactionStatus[0]);
    }
    
    public void addStatus(String status) {
        historyStatus.add(new TransactionStatus(new Date(), status));
    }
    
    public TransaksiProduct[] getProdukDibeli() {
        return produkDibeli.toArray(new TransaksiProduct[0]);
    }
    
    public void addProduct(UUID productId, int amount) {
        produkDibeli.add(new TransaksiProduct(productId, amount));
    }
    
    public String getJenisTransaksi() {
        return jenisTransaksi;
    }
    
    public long getBiayaOngkir() {
        return biayaOngkir;
    }
    
    public String getCurrentStatus() {
        if (historyStatus.isEmpty()) {
            return "Unknown";
        }
        return historyStatus.get(historyStatus.size() - 1).getStatus();
    }
    
    public TransactionStatus getLatestStatus() {
        if (historyStatus.isEmpty()) {
            return new TransactionStatus(new Date(), "Unknown");
        }
        return historyStatus.get(historyStatus.size() - 1);
    }
    
    
    public Date getLatestStatusDate() {
        if (historyStatus.isEmpty()) {
            return new Date();
        }
        return historyStatus.get(historyStatus.size() - 1).getTimestamp();
    }
    
    public void refund() {
        addStatus("Dikembalikan");
        // Logic for refunding would be here
    }
    
    public long getTotalHarga() {
        return totalHarga;
    }
    
    public void setTotalHarga(long totalHarga) {
        this.totalHarga = totalHarga;
    }

    public void setId(String transaksiId) {
        this.id = transaksiId;
    }

    public void setBiayaOngkir(int biayaOngkir) {
        this.biayaOngkir = biayaOngkir;
    }
}