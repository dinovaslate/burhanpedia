package com.burhanpedia.service;

import com.burhanpedia.Burhanpedia;
import com.burhanpedia.core.interfaces.SystemMenu;
import com.burhanpedia.model.transaction.Transaksi;
import com.burhanpedia.model.user.Pengirim;
import com.burhanpedia.util.BurhanpediaLogger;
import java.util.Scanner;

public class SystemPengirim implements SystemMenu {
    private Pengirim activePengirim;
    private final Scanner input;
    private final Burhanpedia mainRepository;
    private final BurhanpediaLogger logger;
    
    public SystemPengirim(Scanner input, Burhanpedia mainRepository) {
        this.input = input;
        this.mainRepository = mainRepository;
        this.logger = BurhanpediaLogger.getInstance();
    }
    
    public void setActivePengirim(Pengirim pengirim) {
        this.activePengirim = pengirim;
    }
    
    @Override
    public String showMenu() {
        return "\n╔════════════════════════════════════╗\n" +
               "║           MENU PENGIRIM            ║\n" +
               "╠════════════════════════════════════╣\n" +
               "║ 1. Cari Pesanan                    ║\n" +
               "║ 2. Ambil Pesanan                   ║\n" +
               "║ 3. Konfirmasi Pengiriman           ║\n" +
               "║ 4. Lihat Riwayat Transaksi         ║\n" +
               "║ 5. Kembali ke Menu Utama           ║\n" +
               "╚════════════════════════════════════╝";
    }
    
    @Override
    public void handleMenu() {
        boolean running = true;
        
        while (running) {
            System.out.println(showMenu());
            System.out.print("Perintah : ");
            String command = input.nextLine();
            
            switch (command) {
                case "1":
                    handleFindJob();
                    break;
                case "2":
                    handleTakeJob();
                    break;
                case "3":
                    handleConfirmJob();
                    break;
                case "4":
                    handleRiwayatTransaksi();
                    break;
                case "5":
                    running = false;
                    break;
                default:
                    System.out.println("Perintah tidak valid!");
            }
        }
    }
    
    private void handleFindJob() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                   PESANAN TERSEDIA                         ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        
        Transaksi[] availableJobs = mainRepository.getTransaksiRepo().getAvailableJobs();
        
        if (availableJobs.length == 0) {
            System.out.println("║ Tidak ada pesanan yang tersedia saat ini!               ║");
        } else {
            for (Transaksi job : availableJobs) {
                System.out.printf("║ ID: %-10s │ Pembeli: %-15s │ Penjual: %-15s ║%n",
                    job.getId(),
                    job.getNamePembeli(),
                    job.getNamePenjual());
                System.out.println("╠════════════════════════════════════════════════════════════╣");
            }
        }
        
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
    
    private void handleTakeJob() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║           AMBIL PESANAN            ║");
        System.out.println("╚════════════════════════════════════╝");
        
        System.out.print("Masukkan ID pesanan yang ingin diambil: ");
        String transactionId = input.nextLine();
        
        Transaksi transaksi = mainRepository.getTransaksiRepo().getTransaksiById(transactionId);
        
        if (transaksi == null) {
            logger.log("Pengirim", "Gagal mengambil job: ID " + transactionId + " tidak ditemukan");
            System.out.println("\n✗ Tidak ada pesanan untuk ID tersebut!");
            return;
        }
        
        if (!"Menunggu Pengirim".equals(transaksi.getCurrentStatus())) {
            System.out.println("\n✗ Tidak dapat mengambil pesanan ini!");
            return;
        }
        
        // Check delivery deadline based on jenis transaksi
        String jenisTransaksi = transaksi.getJenisTransaksi();
        java.util.Date orderDate = transaksi.getLatestStatusDate();
        java.util.Date today = new java.util.Date();
        
        java.util.Calendar deadline = java.util.Calendar.getInstance();
        deadline.setTime(orderDate);
        
        boolean isOverdue = false;
        
        if ("Instant".equals(jenisTransaksi)) {
            // Same day delivery
            if (!isSameDay(orderDate, today)) {
                isOverdue = true;
            }
        } else if ("Next Day".equals(jenisTransaksi)) {
            // Next day delivery
            deadline.add(java.util.Calendar.DAY_OF_MONTH, 1);
            if (today.after(deadline.getTime())) {
                isOverdue = true;
            }
        }
        
        if (isOverdue) {
            System.out.println("\n✗ Pesanan sudah melewati batas waktu pengiriman!");
            return;
        }
        
        // Update transaction status and set pengirim
        transaksi.setNamePengirim(activePengirim.getUsername());
        transaksi.addStatus("Sedang Dikirim");
        
        System.out.printf("\n✓ Pesanan berhasil diambil oleh %s%n", activePengirim.getUsername());
    }
    
    private boolean isSameDay(java.util.Date d1, java.util.Date d2) {
        java.util.Calendar c1 = java.util.Calendar.getInstance();
        java.util.Calendar c2 = java.util.Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(java.util.Calendar.YEAR) == c2.get(java.util.Calendar.YEAR) &&
               c1.get(java.util.Calendar.DAY_OF_YEAR) == c2.get(java.util.Calendar.DAY_OF_YEAR);
    }
    
    private void handleConfirmJob() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║       KONFIRMASI PENGIRIMAN        ║");
        System.out.println("╚════════════════════════════════════╝");
        
        System.out.print("Masukkan ID pesanan yang ingin dikonfirmasi: ");
        String transactionId = input.nextLine();
        
        Transaksi transaksi = mainRepository.getTransaksiRepo().getTransaksiById(transactionId);
        
        if (transaksi == null) {
            System.out.println("\n✗ Tidak ada pesanan untuk ID tersebut!");
            return;
        }
        
        if (!transaksi.getNamePengirim().equals(activePengirim.getUsername())) {
            System.out.println("\n✗ Pesanan ini bukan milik Anda!");
            return;
        }
        
        if (!"Sedang Dikirim".equals(transaksi.getCurrentStatus())) {
            System.out.println("\n✗ Pesanan tidak dalam status pengiriman!");
            return;
        }
        
        // Complete the delivery
        transaksi.addStatus("Pesanan Selesai");
        
        // Update balances
        long deliveryFee = transaksi.getBiayaOngkir();
        activePengirim.setBalance(activePengirim.getBalance() + deliveryFee);
        
        System.out.printf("\n✓ Pesanan berhasil diselesaikan oleh %s%n", activePengirim.getUsername());
        System.out.printf("✓ Biaya pengiriman sebesar %.2f telah ditambahkan ke saldo Anda%n", deliveryFee / 100.0);
    }
    
    private void handleRiwayatTransaksi() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                RIWAYAT PENGIRIMAN                         ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        
        boolean found = false;
        for (Transaksi transaksi : mainRepository.getTransaksiRepo().getList()) {
            if (transaksi.getNamePengirim() != null && 
                transaksi.getNamePengirim().equals(activePengirim.getUsername())) {
                found = true;
                System.out.printf("║ ID: %-10s │ Status: %-20s │ Biaya: %-10.2f ║%n",
                    transaksi.getId(),
                    transaksi.getCurrentStatus(),
                    transaksi.getBiayaOngkir() / 100.0);
                System.out.println("╠════════════════════════════════════════════════════════════╣");
            }
        }
        
        if (!found) {
            System.out.println("║ Belum ada riwayat pengiriman!                           ║");
        }
        
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
}