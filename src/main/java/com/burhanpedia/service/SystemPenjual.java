package com.burhanpedia.service;

import com.burhanpedia.Burhanpedia;
import com.burhanpedia.core.interfaces.SystemMenu;
import com.burhanpedia.model.product.Product;
import com.burhanpedia.model.transaction.Transaksi;
import com.burhanpedia.model.user.Penjual;
import com.burhanpedia.exception.*;
import com.burhanpedia.util.BurhanpediaLogger;

import java.util.List;
import java.util.Scanner;
import java.util.Objects;

public class SystemPenjual implements SystemMenu {
    private Penjual activePenjual;
    private final Scanner input;
    private final Burhanpedia mainRepository;
    private final BurhanpediaLogger logger;
    
    public SystemPenjual(Scanner input, Burhanpedia mainRepository) {
        this.input = Objects.requireNonNull(input);
        this.mainRepository = Objects.requireNonNull(mainRepository);
        this.logger = BurhanpediaLogger.getInstance();
    }
    
    public void setActivePenjual(Penjual penjual) {
        this.activePenjual = penjual;
    }
    
    @Override
    public String showMenu() {
        return "\n╔════════════════════════════════════╗\n" +
               "║           MENU PENJUAL             ║\n" +
               "╠════════════════════════════════════╣\n" +
               "║ 1. Lihat Daftar Produk             ║\n" +
               "║ 2. Tambah Produk Baru              ║\n" +
               "║ 3. Tambah Stok Produk              ║\n" +
               "║ 4. Ubah Harga Produk               ║\n" +
               "║ 5. Kirim Pesanan                   ║\n" +
               "║ 6. Lihat Laporan Pendapatan        ║\n" +
               "║ 7. Cek Saldo                       ║\n" +
               "║ 8. Lihat Riwayat Transaksi         ║\n" +
               "║ 9. Kembali ke Menu Utama           ║\n" +
               "╚════════════════════════════════════╝";
    }
    
    @Override
    public void handleMenu() {
        boolean isRunning = true;
        
        while (isRunning) {
            System.out.println(showMenu());
            System.out.print("Perintah : ");
            String command = input.nextLine();
            
            try {
                switch (command) {
                    case "1":
                        handleCekProduk();
                        break;
                    case "2":
                        handleTambahProduk();
                        break;
                    case "3":
                        handleTambahStok();
                        break;
                    case "4":
                        handleUbahHarga();
                        break;
                    case "5":
                        handleKirimBarang();
                        break;
                    case "6":
                        handleLaporanPendapatan();
                        break;
                    case "7":
                        handleCekSaldo();
                        break;
                    case "8":
                        handleRiwayatTransaksi();
                        break;
                    case "9":
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Perintah tidak valid!");
                }
            } catch (TransactionProcessException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error: Terjadi kesalahan yang tidak terduga - " + e.getMessage());
            }
        }
    }
    
    public void handleCekProduk() throws TransactionProcessException {
        ensureActivePenjual();
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                     DAFTAR PRODUK                          ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        
        Product[] products = activePenjual.getRepo().getProductList();
        
        if (products.length == 0) {
            System.out.println("║ Belum ada produk yang tersedia                           ║");
        } else {
            for (Product product : products) {
                System.out.printf("║ %-20s │ Harga: %-8.2f │ Stok: %-4d ║%n", 
                    product.getProductName(), 
                    product.getProductPrice() / 100.0, 
                    product.getProductStock());
            }
        }
        
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
    
    public void handleTambahProduk() throws TransactionProcessException {
        ensureActivePenjual();
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║         TAMBAH PRODUK BARU         ║");
        System.out.println("╚════════════════════════════════════╝");
        
        System.out.print("Masukkan nama produk: ");
        String name = input.nextLine();
        
        if (activePenjual.getRepo().getProductByName(name) != null) {
            logger.log("Penjual", "Gagal menambah produk: " + name + " (sudah ada)");
            System.out.println("\n✗ Produk dengan nama tersebut sudah ada!");
            return;
        }
        
        System.out.print("Masukkan stok produk: ");
        try {
            int stok = Integer.parseInt(input.nextLine());
            
            if (stok <= 0) {
                System.out.println("\n✗ Stok produk harus positif!");
                return;
            }
            
            System.out.print("Masukkan harga produk: ");
            long price = Long.parseLong(input.nextLine());
            
            if (price <= 0) {
                System.out.println("\n✗ Harga produk harus positif!");
                return;
            }
            
            Product newProduct = new Product(name, stok, price);
            newProduct.addProduct(activePenjual.getRepo());
            
            System.out.println("\n✓ Produk berhasil ditambahkan!");
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ Masukkan angka yang valid!");
        }
    }
    
    public void handleTambahStok() throws TransactionProcessException {
        ensureActivePenjual();
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║         TAMBAH STOK PRODUK         ║");
        System.out.println("╚════════════════════════════════════╝");
        
        System.out.print("Masukkan nama produk: ");
        String name = input.nextLine();
        
        Product product = activePenjual.getRepo().getProductByName(name);
        
        if (product == null) {
            System.out.println("\n✗ Produk tidak ditemukan!");
            return;
        }
        
        System.out.print("Masukkan jumlah stok yang ingin ditambah: ");
        try {
            int additionalStock = Integer.parseInt(input.nextLine());
            
            if (additionalStock <= 0) {
                System.out.println("\n✗ Jumlah stok yang ditambahkan harus positif!");
                return;
            }
            
            int newStock = product.getProductStock() + additionalStock;
            product.setProductStock(newStock);
            activePenjual.getRepo().saveProductsToCsv();
            
            System.out.printf("\n✓ Stok %s berhasil ditambah! Stok saat ini: %d%n", 
                            product.getProductName(), newStock);
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ Masukkan angka yang valid!");
        }
    }
    
    public void handleUbahHarga() throws TransactionProcessException {
        ensureActivePenjual();
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║         UBAH HARGA PRODUK          ║");
        System.out.println("╚════════════════════════════════════╝");
        
        System.out.print("Masukkan nama produk: ");
        String name = input.nextLine();
        
        Product product = activePenjual.getRepo().getProductByName(name);
        
        if (product == null) {
            System.out.println("\n✗ Produk tidak ditemukan!");
            return;
        }
        
        System.out.print("Masukkan harga baru: ");
        try {
            long newPrice = Long.parseLong(input.nextLine());
            
            if (newPrice <= 0) {
                System.out.println("\n✗ Harga produk harus positif!");
                return;
            }
            
            product.setProductPrice(newPrice);
            activePenjual.getRepo().saveProductsToCsv();
            
            System.out.printf("\n✓ Harga produk berhasil diperbarui");
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ Masukkan angka yang valid!");
        }
    }
    
    public void handleKirimBarang() throws TransactionProcessException {
        ensureActivePenjual();
        if (!reloadTransactions()) return;
        
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                   PESANAN MENUNGGU PENGIRIMAN              ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        
        boolean found = false;
        Transaksi[] transactions = mainRepository.getTransaksiRepo().getList();
        
        if (transactions.length == 0) {
            System.out.println("║ Tidak ada pesanan yang tersedia                           ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            return;
        }
        
        for (Transaksi transaksi : transactions) {
            if (transaksi.getNamePenjual().equalsIgnoreCase(activePenjual.getUsername()) && 
                "Sedang Dikemas".equals(transaksi.getCurrentStatus().trim())) {
                
                found = true;
                System.out.printf("║ ID: %-10s │ Tanggal: %-20s │ Status: %-15s ║%n",
                    transaksi.getId(),
                    formatDate(transaksi.getLatestStatusDate()),
                    transaksi.getCurrentStatus());
            }
        }
        
        if (!found) {
            System.out.println("║ Tidak ada pesanan yang menunggu pengiriman               ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            return;
        }
        
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        System.out.print("\nMasukkan ID pesanan yang akan dikirim (ketik 'batal' untuk membatalkan): ");
        String transactionId = input.nextLine();
        
        if (transactionId.equalsIgnoreCase("batal")) {
            return;
        }
        
        if (!reloadTransactions()) return;
        
        Transaksi transaksi = mainRepository.getTransaksiRepo().getTransaksiById(transactionId);
        
        if (transaksi == null) {
            System.out.println("\n✗ Pesanan tidak ditemukan!");
            return;
        }
        
        if (!transaksi.getNamePenjual().equals(activePenjual.getUsername())) {
            System.out.println("\n✗ Pesanan ini bukan milik toko Anda!");
            return;
        }
        
        if (!"Sedang Dikemas".equals(transaksi.getCurrentStatus())) {
            System.out.println("\n✗ Pesanan ini sudah tidak dalam proses pengemasan!");
            return;
        }
        
        mainRepository.getTransaksiRepo().updateTransactionStatus(transactionId, "Menunggu Pengirim");
        
        try {
            mainRepository.getTransaksiRepo().saveTransactionsToCsv();
            System.out.println("\n✓ Status pesanan berhasil diperbarui menjadi 'Menunggu Pengirim'");
            logger.log("Penjual", "Status pesanan " + transactionId + " diubah ke 'Menunggu Pengirim' oleh " + activePenjual.getUsername());
        } catch (Exception e) {
            System.err.println("\n✗ Error menyimpan perubahan pesanan: " + e.getMessage());
            logger.log("Error", "Gagal menyimpan pesanan " + transactionId + ": " + e.getMessage());
        }
    }
    
    private boolean reloadTransactions() {
        try {
            mainRepository.getTransaksiRepo().loadTransactionsFromCsv();
            return true;
        } catch (Exception e) {
            logger.log("Error", "Gagal memuat data transaksi: " + e.getMessage());
            System.out.println("Error: Gagal memuat data transaksi. Silakan coba lagi.");
            return false;
        }
    }
    
    private String formatDate(java.util.Date date) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("EEEE, dd MMMM yyyy");
        return formatter.format(date);
    }
    
    private void handleLaporanPendapatan() throws TransactionProcessException {
        ensureActivePenjual();
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                   LAPORAN PENDAPATAN                       ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        
        boolean found = false;
        long totalPendapatan = 0;
        
        for (Transaksi transaksi : mainRepository.getTransaksiRepo().getList()) {
            if (transaksi.getNamePenjual().equals(activePenjual.getUsername()) && 
                ("Pesanan Selesai".equals(transaksi.getCurrentStatus()) || 
                 "Selesai".equals(transaksi.getCurrentStatus()))) {
                
                found = true;
                totalPendapatan += transaksi.getTotalHarga();
                
                System.out.printf("║ ID: %-10s │ Tanggal: %-20s │ Total: %-10.2f ║%n",
                    transaksi.getId(),
                    formatDate(transaksi.getLatestStatusDate()),
                    transaksi.getTotalHarga() / 100.0);
            }
        }
        
        if (!found) {
            System.out.println("║ Belum ada laporan pendapatan                            ║");
        } else {
            System.out.println("╠════════════════════════════════════════════════════════════╣");
            System.out.printf("║ Total Pendapatan: %-40.2f ║%n", totalPendapatan / 100.0);
        }
        
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
    
    public void handleCekSaldo() throws TransactionProcessException {
        ensureActivePenjual();
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║              SALDO TOKO            ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.printf("║ Saldo saat ini: %-20.2f ║%n", activePenjual.getBalance() / 100.0);
        System.out.println("╚════════════════════════════════════╝");
    }
    
    private void handleRiwayatTransaksi() throws TransactionProcessException {
        ensureActivePenjual();
        activePenjual.getRiwayatTransaksi(mainRepository.getTransaksiRepo().getList());
    }

    private void ensureActivePenjual() throws TransactionProcessException {
        if (activePenjual == null) {
            throw new TransactionProcessException("Penjual tidak aktif");
        }
    }
}