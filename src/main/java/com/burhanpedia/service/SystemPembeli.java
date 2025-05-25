package com.burhanpedia.service;

import com.burhanpedia.Burhanpedia;
import com.burhanpedia.core.interfaces.SystemMenu;
import com.burhanpedia.model.cart.CartProduct;
import com.burhanpedia.model.discount.Promo;
import com.burhanpedia.model.discount.Voucher;
import com.burhanpedia.model.product.Product;
import com.burhanpedia.model.transaction.Transaksi;
import com.burhanpedia.model.transaction.TransaksiProduct;
import com.burhanpedia.model.user.Pembeli;
import com.burhanpedia.model.user.Penjual;
import com.burhanpedia.model.user.User;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.burhanpedia.exception.*;

public class SystemPembeli implements SystemMenu {
    private Pembeli activePembeli;
    private Penjual activePenjual;
    private final Scanner input;
    private final Burhanpedia mainRepository;

    public SystemPembeli(Scanner input, Burhanpedia mainRepository) {
        this.input = input;
        this.mainRepository = mainRepository;
    }

    public void setActivePembeli(Pembeli pembeli) {
        this.activePembeli = pembeli;
    }

    public void setActivePenjual(Penjual penjual) {
        this.activePenjual = penjual;
    }

    @Override
    public String showMenu() {
        return "\n╔════════════════════════════════════╗\n" +
               "║           MENU PEMBELI             ║\n" +
               "╠════════════════════════════════════╣\n" +
               "║ 1. Cek Saldo                       ║\n" +
               "║ 2. Top Up Saldo                    ║\n" +
               "║ 3. Lihat Daftar Produk             ║\n" +
               "║ 4. Tambah ke Keranjang             ║\n" +
               "║ 5. Checkout                        ║\n" +
               "║ 6. Lacak Pesanan                   ║\n" +
               "║ 7. Lihat Laporan Pengeluaran       ║\n" +
               "║ 8. Lihat Riwayat Transaksi         ║\n" +
               "║ 9. Kembali ke Menu Utama           ║\n" +
               "╚════════════════════════════════════╝";
    }

    @Override
    public void handleMenu() {
        boolean running = true;
        while (running) {
            System.out.println(showMenu());
            System.out.print("Perintah : ");
            String command = input.nextLine();
            try {
                switch (command) {
                    case "1": handleCekSaldo(); break;
                    case "2": handleTopupSaldo(); break;
                    case "3": handleCekDaftarBarang(); break;
                    case "4": handleTambahToKeranjang(); break;
                    case "5": handleCheckout(); break;
                    case "6": handleLacakBarang(); break;
                    case "7": handleLaporanPengeluaran(); break;
                    case "8": handleRiwayatTransaksi(); break;
                    case "9": running = false; break;
                    default: System.out.println("Perintah tidak valid!");
                }
            } catch (TransactionProcessException | InsufficientBalanceException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error: Terjadi kesalahan yang tidak terduga - " + e.getMessage());
            }
        }
    }

    private void handleCekSaldo() throws TransactionProcessException {
        if (activePembeli == null) throw new TransactionProcessException("Pembeli tidak aktif");
        Optional<User> userOpt = mainRepository.getUserRepo().getUserByName(activePembeli.getUsername());
        if (userOpt.isPresent()) {
            Pembeli pembeli = (Pembeli) userOpt.get();
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║              SALDO ANDA            ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.printf("║ Saldo saat ini: %-20.2f%n", pembeli.getBalance() / 100.0);
            System.out.println("╚════════════════════════════════════╝");
        } else {
            throw new TransactionProcessException("User tidak ditemukan");
        }
    }

    private void handleTopupSaldo() throws TransactionProcessException {
        if (activePembeli == null) throw new TransactionProcessException("Pembeli tidak aktif");
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║            TOP UP SALDO            ║");
        System.out.println("╚════════════════════════════════════╝");
        
        System.out.print("Masukkan jumlah saldo yang ingin ditambah: ");
        try {
            long amount = Long.parseLong(input.nextLine());
            if (amount <= 0) {
                System.out.println("\n✗ Jumlah saldo harus positif!");
                return;
            }
            Optional<User> userOpt = mainRepository.getUserRepo().getUserByName(activePembeli.getUsername());
            if (userOpt.isPresent()) {
                Pembeli pembeli = (Pembeli) userOpt.get();
                pembeli.setBalance(pembeli.getBalance() + amount);
                mainRepository.getUserRepo().updateUser(pembeli);
                System.out.printf("\n✓ Saldo berhasil ditambah! Saldo saat ini: %.2f%n", pembeli.getBalance() / 100.0);
            } else {
                System.out.println("\n✗ Pembeli tidak ditemukan");
            }
        } catch (NumberFormatException e) {
            System.out.println("\n✗ Masukkan angka yang valid!");
        }
    }

    private void handleCekDaftarBarang() throws TransactionProcessException {
        if (mainRepository == null) throw new TransactionProcessException("Repository tidak tersedia");
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                     DAFTAR PRODUK                          ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        
        Penjual[] penjuals = getPenjualList();
        if (penjuals.length == 0) {
            System.out.println("║ Belum ada penjual terdaftar!                           ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            return;
        }
        
        System.out.print("Lakukan Filtering [ya/tidak]: ");
        String filterChoice = input.nextLine();
        System.out.print("Lakukan Pengurutan [ya/tidak]: ");
        String sortChoice = input.nextLine();
        boolean foundProducts = false;
        List<Product> allProducts = new ArrayList<>();
        for (Penjual penjual : penjuals) {
            allProducts.addAll(Arrays.asList(penjual.getRepo().getProductList()));
        }
        
        if (filterChoice.equalsIgnoreCase("ya")) {
            System.out.print("Filter berdasarkan (format: ATTRIBUTE#OPERATOR#VALUE): ");
            String[] criteria = input.nextLine().split("#");
            if (criteria.length == 3) {
                String field = criteria[0].toLowerCase();
                String operator = criteria[1].toLowerCase();
                String value = criteria[2];
                allProducts = filterProduk(allProducts, product -> filterProductByCriteria(product, field, operator, value));
            }
        }
        
        if (sortChoice.equalsIgnoreCase("ya")) {
            System.out.print("Urutkan berdasarkan (format: ATTRIBUTE#ORDER): ");
            String[] criteria = input.nextLine().split("#");
            if (criteria.length == 2) {
                String field = criteria[0].toLowerCase();
                String order = criteria[1].toLowerCase();
                Comparator<Product> comparator = getProductComparator(field, order);
                sortProduk(allProducts, comparator);
            }
        }
        
        if (!allProducts.isEmpty()) {
            foundProducts = true;
            for (Product product : allProducts) {
                Penjual penjual = findPenjualObjectByProductId(product.getProductId());
                if (penjual != null) {
                    System.out.printf("║ %-15s │ %-20s │ Harga: %-8.2f │ Stok: %-4d ║%n",
                        penjual.getRepo().getNamaToko(),
                        product.getProductName(),
                        product.getProductPrice() / 100.0,
                        product.getProductStock());
                }
            }
        }
        
        if (!foundProducts) {
            System.out.println("║ Tidak ada produk yang tersedia saat ini!               ║");
        }
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }

    private boolean filterProductByCriteria(Product product, String field, String operator, String value) {
        switch (field) {
            case "namatoko":
                Penjual penjual = findPenjualObjectByProductId(product.getProductId());
                String namaToko = penjual != null ? penjual.getRepo().getNamaToko() : "";
                if (operator.equals("contains")) return namaToko.toLowerCase().contains(value.toLowerCase());
                if (operator.equals("equal")) return namaToko.toLowerCase().equals(value.toLowerCase());
                return false;
            case "namaproduk":
                if (operator.equals("mulaidengan")) return product.getProductName().toLowerCase().startsWith(value.toLowerCase());
                if (operator.equals("berakhirdengan")) return product.getProductName().toLowerCase().endsWith(value.toLowerCase());
                if (operator.equals("contains")) return product.getProductName().toLowerCase().contains(value.toLowerCase());
                return false;
            case "harga":
                if (operator.equals("between")) {
                    String[] range = value.split(" and ");
                    if (range.length == 2) {
                        try {
                            long min = Long.parseLong(range[0].trim());
                            long max = Long.parseLong(range[1].trim());
                            return product.getProductPrice() >= min && product.getProductPrice() <= max;
                        } catch (NumberFormatException e) { return false; }
                    }
                    return false;
                }
                try {
                    long price = Long.parseLong(value);
                    switch (operator) {
                        case ">": return product.getProductPrice() > price;
                        case ">=": return product.getProductPrice() >= price;
                        case "<": return product.getProductPrice() < price;
                        case "<=": return product.getProductPrice() <= price;
                        default: return false;
                    }
                } catch (NumberFormatException e) { return false; }
            case "stok":
                if (operator.equals("between")) {
                    String[] range = value.split(" and ");
                    if (range.length == 2) {
                        try {
                            int min = Integer.parseInt(range[0].trim());
                            int max = Integer.parseInt(range[1].trim());
                            return product.getProductStock() >= min && product.getProductStock() <= max;
                        } catch (NumberFormatException e) { return false; }
                    }
                    return false;
                }
                try {
                    int stock = Integer.parseInt(value);
                    switch (operator) {
                        case ">": return product.getProductStock() > stock;
                        case ">=": return product.getProductStock() >= stock;
                        case "<": return product.getProductStock() < stock;
                        case "<=": return product.getProductStock() <= stock;
                        default: return false;
                    }
                } catch (NumberFormatException e) { return false; }
            default:
                return true;
        }
    }

    private Comparator<Product> getProductComparator(String field, String order) {
        return (p1, p2) -> {
            int result = 0;
            switch (field) {
                case "namaproduk":
                    result = p1.getProductName().compareTo(p2.getProductName());
                    break;
                case "namatoko":
                    Penjual penjual1 = findPenjualObjectByProductId(p1.getProductId());
                    Penjual penjual2 = findPenjualObjectByProductId(p2.getProductId());
                    String namaToko1 = penjual1 != null ? penjual1.getRepo().getNamaToko() : "";
                    String namaToko2 = penjual2 != null ? penjual2.getRepo().getNamaToko() : "";
                    result = namaToko1.compareTo(namaToko2);
                    break;
                case "harga":
                    result = Long.compare(p1.getProductPrice(), p2.getProductPrice());
                    break;
                case "stok":
                    result = Integer.compare(p1.getProductStock(), p2.getProductStock());
                    break;
            }
            return order.equalsIgnoreCase("descending") ? -result : result;
        };
    }

    private void sortProduk(List<Product> products, Comparator<Product> comparator) {
        products.sort(comparator);
    }

    private List<Product> filterProduk(List<Product> products, Predicate<Product> predicate) {
        return products.stream().filter(predicate).collect(Collectors.toList());
    }

    private Penjual[] getPenjualList() {
        List<Penjual> penjuals = new ArrayList<>();
        for (var user : mainRepository.getUserRepo().getAll()) {
            if (user.getRole().equals("Penjual")) penjuals.add((Penjual) user);
        }
        return penjuals.toArray(new Penjual[0]);
    }

    private void handleTambahToKeranjang() throws TransactionProcessException {
        if (activePembeli == null) throw new TransactionProcessException("Pembeli tidak aktif");
        System.out.print("Masukkan toko penjual barang yang ingin dibeli: ");
        String namaToko = input.nextLine();
        Penjual penjual = findPenjualByToko(namaToko);
        if (penjual == null) {
            System.out.println("Toko tidak ditemukan!");
            return;
        }
        System.out.print("Masukkan nama barang yang ingin dibeli: ");
        String namaBarang = input.nextLine();
        Product product = penjual.getRepo().getProductByName(namaBarang);
        if (product == null) {
            System.out.println("Barang tidak ditemukan!");
            return;
        }
        System.out.print("Masukkan jumlah barang yang ingin dibeli: ");
        try {
            int amount = Integer.parseInt(input.nextLine());
            if (amount <= 0) {
                System.out.println("Jumlah barang harus positif!");
                return;
            }
            if (amount > product.getProductStock()) {
                System.out.println("Stok tidak mencukupi!");
                return;
            }
            activePembeli.getCart().addToCart(product.getProductId(), amount);
        } catch (NumberFormatException e) {
            System.out.println("Masukkan angka yang valid!");
        }
    }

    private Penjual findPenjualByToko(String namaToko) {
        for (var user : mainRepository.getUserRepo().getAll()) {
            if (user.getRole().equals("Penjual")) {
                Penjual penjual = (Penjual) user;
                if (penjual.getRepo().getNamaToko().equalsIgnoreCase(namaToko)) return penjual;
            }
        }
        return null;
    }

    private Penjual findPenjualObjectByProductId(UUID productId) {
        for (var user : mainRepository.getUserRepo().getAll()) {
            if (user.getRole().equals("Penjual")) {
                Penjual penjual = (Penjual) user;
                if (penjual.getRepo().getProductById(productId).isPresent()) return penjual;
            }
        }
        return null;
    }

    private void handleCheckout() throws TransactionProcessException, InsufficientBalanceException {
        if (activePembeli == null) throw new TransactionProcessException("Pembeli tidak aktif");
        if (activePembeli.getCart().isEmpty()) throw new TransactionProcessException("Keranjang masih kosong");
        CartProduct[] cartProducts = activePembeli.getCart().getCartContent();
        long subtotal = 0;
        System.out.println("=================================");
        for (CartProduct cp : cartProducts) {
            Product product = findProductById(cp.getProductId());
            if (product != null) {
                long productTotal = product.getProductPrice() * cp.getProductAmount();
                subtotal += productTotal;
                System.out.printf("%s %.2f %d (%.2f)%n", product.getProductName(), product.getProductPrice() / 100.0, cp.getProductAmount(), productTotal / 100.0);
            }
        }
        System.out.printf("Subtotal %.2f%n", subtotal / 100.0);
        System.out.println("=================================");
        System.out.print("Apakah anda yakin dengan produknya? (Y/N): ");
        String confirmation = input.nextLine();
        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("Checkout dibatalkan.");
            return;
        }
        System.out.println("Pilih metode pengiriman:");
        System.out.println("1. Regular (10.000)");
        System.out.println("2. Next Day (15.000)");
        System.out.println("3. Instant (20.000)");
        System.out.print("Pilihan: ");
        String shippingChoice = input.nextLine();
        String shippingMethod;
        switch (shippingChoice) {
            case "1": shippingMethod = "Regular"; break;
            case "2": shippingMethod = "Next Day"; break;
            case "3": shippingMethod = "Instant"; break;
            default: System.out.println("Pilihan tidak valid, menggunakan Regular."); shippingMethod = "Regular";
        }
        System.out.print("Gunakan voucher/promo? (Y/N): ");
        String useVoucherChoice = input.nextLine();
        String discountId = null;
        if (useVoucherChoice.equalsIgnoreCase("Y")) {
            System.out.println("Pilih jenis diskon:");
            System.out.println("1. Voucher");
            System.out.println("2. Promo");
            System.out.print("Pilihan: ");
            String discountType = input.nextLine();
            System.out.print("Masukkan ID diskon: ");
            String inputDiscountId = input.nextLine();
            boolean discountValid = false;
            if (discountType.equals("1")) {
                Voucher voucher = mainRepository.getVoucherRepo().getById(inputDiscountId);
                if (voucher != null && voucher.getSisaPemakaian() > 0) {
                    discountId = inputDiscountId;
                    discountValid = true;
                }
            } else if (discountType.equals("2")) {
                Promo promo = mainRepository.getPromoRepo().getById(inputDiscountId);
                if (promo != null) {
                    discountId = inputDiscountId;
                    discountValid = true;
                }
            }
            if (!discountValid) {
                System.out.println("Diskon tidak valid atau tidak ditemukan.");
            } else {
                System.out.println("Diskon berhasil diterapkan.");
            }
        }
        Transaksi transaksi = new Transaksi(activePembeli.getUsername(), findPenjualByProductId(cartProducts[0].getProductId()).getUsername(), shippingMethod);
        for (CartProduct cp : cartProducts) {
            transaksi.addProduct(cp.getProductId(), cp.getProductAmount());
            Product product = findProductById(cp.getProductId());
            if (product != null) {
                Penjual penjual = findPenjualByProductId(cp.getProductId());
                if (penjual != null) {
                    product.setProductStock(product.getProductStock() - cp.getProductAmount());
                    penjual.getRepo().saveProductsToCsv();
                }
            }
        }
        if (discountId != null) {
            transaksi.setIdDiskon(discountId);
        }
        mainRepository.getTransaksiRepo().addTransaksi(transaksi);
        long total = mainRepository.getTransaksiRepo().calculateTotalTransaksi(transaksi.getId());
        transaksi.setTotalHarga(total);
        if (activePembeli.getBalance() < total) throw new InsufficientBalanceException("Saldo tidak mencukupi untuk melakukan transaksi ini.");
        activePembeli.setBalance(activePembeli.getBalance() - total);
        mainRepository.getUserRepo().updateUser(activePembeli);
        Penjual penjual = findPenjualByProductId(cartProducts[0].getProductId());
        if (penjual != null) {
            long sellerAmount = subtotal;
            penjual.addRevenue(sellerAmount);
            mainRepository.getUserRepo().updateUser(penjual);
        }
        activePembeli.getCart().clear();
        System.out.println("Checkout berhasil! ID Transaksi: " + transaksi.getId());
    }

    private Product findProductById(UUID productId) {
        for (var user : mainRepository.getUserRepo().getAll()) {
            if (user.getRole().equals("Penjual")) {
                Penjual penjual = (Penjual) user;
                Optional<Product> productOpt = penjual.getRepo().getProductById(productId);
                if (productOpt.isPresent()) return productOpt.get();
            }
        }
        return null;
    }

    private void createTransaction(CartProduct[] cartProducts, String jenisTransaksi, String kodeDiskon, long totalPrice) {
        Product firstProduct = findProductById(cartProducts[0].getProductId());
        Penjual penjual = findPenjualByProductId(cartProducts[0].getProductId());
        String namaPenjual = penjual != null ? penjual.getUsername() : "";
        if (firstProduct != null && namaPenjual != null) {
            Transaksi transaksi = new Transaksi(activePembeli.getUsername(), namaPenjual, jenisTransaksi);
            for (CartProduct cp : cartProducts) {
                transaksi.addProduct(cp.getProductId(), cp.getProductAmount());
                Product product = findProductById(cp.getProductId());
                if (product != null) {
                    product.setProductStock(product.getProductStock() - cp.getProductAmount());
                    product.addProduct(activePenjual.getRepo());
                }
            }
            transaksi.setTotalHarga(totalPrice);
            if (!kodeDiskon.equalsIgnoreCase("skip")) {
                transaksi.setIdDiskon(kodeDiskon);
            }
            mainRepository.getTransaksiRepo().addTransaksi(transaksi);
        }
    }

    private Penjual findPenjualByProductId(UUID productId) {
        for (var user : mainRepository.getUserRepo().getAll()) {
            if (user.getRole().equals("Penjual")) {
                Penjual penjual = (Penjual) user;
                if (penjual.getRepo().getProductById(productId).isPresent()) return penjual;
            }
        }
        return null;
    }

    private void handleLacakBarang() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                   LACAK PESANAN                           ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        
        boolean found = false;
        for (Transaksi transaksi : mainRepository.getTransaksiRepo().getList()) {
            if (transaksi.getNamePembeli().equals(activePembeli.getUsername())) {
                found = true;
                SimpleDateFormat fmt = new SimpleDateFormat("EEEE, dd MMMM yyyy");
                String formattedDate = fmt.format(transaksi.getLatestStatusDate());
                System.out.printf("║ ID: %-10s │ Tanggal: %-20s ║%n", 
                    transaksi.getId(), formattedDate);
                System.out.printf("║ Toko: %-10s │ Status: %-20s ║%n", 
                    transaksi.getNamePenjual(), transaksi.getCurrentStatus());
                if (transaksi.getNamePengirim() != null) {
                    System.out.printf("║ Pengirim: %-40s ║%n", transaksi.getNamePengirim());
                }
                System.out.println("╠════════════════════════════════════════════════════════════╣");
            }
        }
        
        if (!found) {
            System.out.println("║ Belum ada pesanan!                                      ║");
        }
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }

    private void handleLaporanPengeluaran() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                LAPORAN PENGELUARAN                        ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        
        boolean found = false;
        long totalPengeluaran = 0;
        
        for (Transaksi transaksi : mainRepository.getTransaksiRepo().getList()) {
            if (transaksi.getNamePembeli().equals(activePembeli.getUsername())) {
                found = true;
                
                SimpleDateFormat fmt = new SimpleDateFormat("EEEE, dd MMMM yyyy");
                String formattedDate = fmt.format(transaksi.getLatestStatusDate());
                System.out.printf("║ ID: %-10s │ Tanggal: %-20s ║%n", 
                    transaksi.getId(), formattedDate);
                
                for (TransaksiProduct tp : transaksi.getProdukDibeli()) {
                    Product product = findProductById(tp.getProductId());
                    if (product != null) {
                        System.out.printf("║ %-20s │ %d x %d │ Total: %-10.2f ║%n", 
                            product.getProductName(),
                            product.getProductPrice() / 100.0,
                            tp.getProductAmount(),
                            (product.getProductPrice() * tp.getProductAmount()) / 100.0);
                    }
                }
                
                long subtotal = calculateSubtotal(transaksi);
                long discount = calculateDiscount(transaksi);
                long tax = ((subtotal - discount) * 3) / 100;
                long totalHarga = subtotal - discount + tax + transaksi.getBiayaOngkir();
                totalPengeluaran += totalHarga;
                
                System.out.println("╠════════════════════════════════════════════════════════════╣");
                System.out.printf("║ Subtotal: %-40.2f ║%n", subtotal / 100.0);
                System.out.printf("║ Diskon: %-42.2f ║%n", discount / 100.0);
                System.out.printf("║ Pajak (3%%): %-38.2f ║%n", tax / 100.0);
                System.out.printf("║ Pengiriman: %-38.2f ║%n", transaksi.getBiayaOngkir() / 100.0);
                System.out.printf("║ Total: %-42.2f ║%n", totalHarga / 100.0);
                System.out.println("╠════════════════════════════════════════════════════════════╣");
            }
        }
        
        if (!found) {
            System.out.println("║ Belum ada laporan pengeluaran!                         ║");
        } else {
            System.out.printf("║ Total Keseluruhan: %-35.2f ║%n", totalPengeluaran / 100.0);
        }
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }

    private long calculateSubtotal(Transaksi transaksi) {
        long subtotal = 0;
        for (TransaksiProduct tp : transaksi.getProdukDibeli()) {
            Product product = findProductById(tp.getProductId());
            if (product != null) subtotal += product.getProductPrice() * tp.getProductAmount();
        }
        return subtotal;
    }

    private long calculateDiscount(Transaksi transaksi) {
        if (transaksi.getIdDiskon() == null) return 0;
        long subtotal = calculateSubtotal(transaksi);
        Voucher voucher = mainRepository.getVoucherRepo().getById(transaksi.getIdDiskon());
        if (voucher != null) return voucher.calculateDisc(subtotal);
        Promo promo = mainRepository.getPromoRepo().getById(transaksi.getIdDiskon());
        if (promo != null) return promo.calculateDisc(subtotal);
        return 0;
    }

    private void handleRiwayatTransaksi() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                RIWAYAT TRANSAKSI                          ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        
        SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy");
        boolean found = false;
        
        for (Transaksi transaksi : mainRepository.getTransaksiRepo().getList()) {
            if (transaksi.getNamePembeli().equals(activePembeli.getUsername())) {
                found = true;
                String formattedDate = fmt.format(transaksi.getLatestStatusDate());
                long subtotal = calculateSubtotal(transaksi);
                long discount = calculateDiscount(transaksi);
                long tax = ((subtotal - discount) * 3) / 100;
                long totalHarga = subtotal - discount + tax + transaksi.getBiayaOngkir();
                
                System.out.printf("║ %-10s │ %-12s │ - %-10.2f │ Pembelian produk ║%n", 
                    transaksi.getId(), 
                    formattedDate, 
                    totalHarga / 100.0);
            }
        }
        
        if (!found) {
            System.out.println("║ Belum ada riwayat transaksi!                           ║");
        }
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
}
