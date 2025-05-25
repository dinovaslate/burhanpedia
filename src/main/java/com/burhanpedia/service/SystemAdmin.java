package com.burhanpedia.service;

import com.burhanpedia.Burhanpedia;
import com.burhanpedia.core.interfaces.SystemMenu;
import com.burhanpedia.model.discount.Promo;
import com.burhanpedia.model.discount.Voucher;
import com.burhanpedia.model.user.Admin;
import com.burhanpedia.util.BurhanpediaLogger;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class SystemAdmin implements SystemMenu {
    private Admin activeAdmin;
    private final Scanner input;
    private final Burhanpedia mainRepository;
    private final BurhanpediaLogger logger;

    public SystemAdmin(Scanner input, Burhanpedia mainRepository) {
        this.input = input;
        this.mainRepository = mainRepository;
        this.logger = BurhanpediaLogger.getInstance();
    }

    public void setActiveAdmin(Admin admin) {
        this.activeAdmin = admin;
    }

    @Override
    public String showMenu() {
        return "\n╔════════════════════════════════════╗\n" +
               "║           MENU ADMIN SISTEM         ║\n" +
               "╠════════════════════════════════════╣\n" +
               "║ 1. Buat Voucher Baru               ║\n" +
               "║ 2. Buat Promo Baru                 ║\n" +
               "║ 3. Kelola Voucher                  ║\n" +
               "║ 4. Kelola Promo                    ║\n" +
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
                    handleGenerateVoucher();
                    break;
                case "2":
                    handleGeneratePromo();
                    break;
                case "3":
                    handleLihatVoucher();
                    break;
                case "4":
                    handleLihatPromo();
                    break;
                case "5":
                    running = false;
                    break;
                default:
                    System.out.println("Perintah tidak valid!");
            }
        }
    }

    private void handleGenerateVoucher() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║         BUAT VOUCHER BARU          ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.print("Masukkan tanggal kadaluarsa (YYYY-MM-DD): ");
        String expiry = input.nextLine();
        try {
            mainRepository.getVoucherRepo().generate(expiry);
            System.out.println("\n✓ Voucher berhasil dibuat!");
            logger.log("Admin", "Voucher baru dibuat dengan tanggal kadaluarsa: " + expiry);
        } catch (Exception e) {
            logger.log("Error", "Gagal membuat voucher: " + e.getMessage());
            System.out.println("\n✗ Gagal membuat voucher. Pastikan format tanggal YYYY-MM-DD.");
        }
    }

    private void handleGeneratePromo() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║           BUAT PROMO BARU          ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.print("Masukkan tanggal kadaluarsa (YYYY-MM-DD): ");
        String expiry = input.nextLine();
        try {
            mainRepository.getPromoRepo().generate(expiry);
            System.out.println("\n✓ Promo berhasil dibuat!");
        } catch (Exception e) {
            System.out.println("\n✗ Gagal membuat promo. Pastikan format tanggal YYYY-MM-DD.");
        }
    }

    private void handleLihatVoucher() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║         KELOLA VOUCHER             ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ 1. Tampilkan Semua Voucher         ║");
        System.out.println("║ 2. Cari Voucher Berdasarkan ID     ║");
        System.out.println("║ 3. Kembali                         ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.print("Pilihan Anda: ");
        String command = input.nextLine();
        switch (command) {
            case "1":
                showAllVouchers();
                break;
            case "2":
                showVoucherById();
                break;
            case "3":
                break;
            default:
                System.out.println("\n✗ Pilihan tidak valid!");
        }
    }

    private void showAllVouchers() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                     DAFTAR VOUCHER                         ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        Voucher[] vouchers = mainRepository.getVoucherRepo().getAll();
        if (vouchers.length == 0) {
            System.out.println("║ Belum ada voucher yang tersedia                         ║");
        } else {
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
            for (Voucher v : vouchers) {
                String date = fmt.format(v.getBerlakuHingga());
                System.out.printf("║ %-10s │ Sisa: %-3d │ Berlaku hingga: %-12s ║%n", 
                    v.getId(), v.getSisaPemakaian(), date);
            }
        }
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }

    private void showVoucherById() {
        System.out.print("\nMasukkan ID Voucher: ");
        String id = input.nextLine();
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                     DETAIL VOUCHER                         ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        Voucher v = mainRepository.getVoucherRepo().getById(id);
        if (v == null) {
            System.out.println("║ Voucher dengan ID " + id + " tidak ditemukan              ║");
        } else {
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
            String date = fmt.format(v.getBerlakuHingga());
            System.out.printf("║ ID: %-10s │ Sisa: %-3d │ Berlaku hingga: %-12s ║%n", 
                v.getId(), v.getSisaPemakaian(), date);
        }
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }

    private void handleLihatPromo() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║           KELOLA PROMO             ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ 1. Tampilkan Semua Promo           ║");
        System.out.println("║ 2. Cari Promo Berdasarkan ID       ║");
        System.out.println("║ 3. Kembali                         ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.print("Pilihan Anda: ");
        String command = input.nextLine();
        switch (command) {
            case "1":
                showAllPromos();
                break;
            case "2":
                showPromoById();
                break;
            case "3":
                break;
            default:
                System.out.println("\n✗ Pilihan tidak valid!");
        }
    }

    private void showAllPromos() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                      DAFTAR PROMO                          ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        Promo[] promos = mainRepository.getPromoRepo().getAll();
        if (promos.length == 0) {
            System.out.println("║ Belum ada promo yang tersedia                           ║");
        } else {
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
            for (Promo p : promos) {
                String date = fmt.format(p.getBerlakuHingga());
                System.out.printf("║ %-10s │ Berlaku hingga: %-12s ║%n", p.getId(), date);
            }
        }
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }

    private void showPromoById() {
        System.out.print("\nMasukkan ID Promo: ");
        String id = input.nextLine();
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                      DETAIL PROMO                          ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        Promo p = mainRepository.getPromoRepo().getById(id);
        if (p == null) {
            System.out.println("║ Promo dengan ID " + id + " tidak ditemukan                ║");
        } else {
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
            String date = fmt.format(p.getBerlakuHingga());
            System.out.printf("║ ID: %-10s │ Berlaku hingga: %-12s ║%n", p.getId(), date);
        }
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
}