package com.burhanpedia.repository;

import com.burhanpedia.model.transaction.Transaksi;
import com.burhanpedia.model.transaction.TransaksiProduct;
import com.burhanpedia.model.transaction.TransactionStatus;
import com.burhanpedia.util.CsvReader;
import com.burhanpedia.util.CsvWriter;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransaksiRepository {
    private static final Logger LOGGER = Logger.getLogger(TransaksiRepository.class.getName());
    private static final String CSV_DIRECTORY = "data";
    private static final String TRANSACTION_CSV_FILE = CSV_DIRECTORY + File.separator + "transactions.csv";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private final List<Transaksi> transaksiList;
    private final VoucherRepository voucherRepo;
    private final PromoRepository promoRepo;
    private final ProductRepository productRepo;
    
    public TransaksiRepository(VoucherRepository voucherRepo, PromoRepository promoRepo, ProductRepository productRepo) {
        this.transaksiList = new ArrayList<>();
        this.voucherRepo = voucherRepo;
        this.promoRepo = promoRepo;
        this.productRepo = productRepo;
        loadTransactionsFromCsv();
    }
    
    public void addTransaksi(Transaksi transaksi) {
        transaksiList.add(transaksi);
        saveTransactionsToCsv();
    }
    
    public Transaksi[] getList() {
        return transaksiList.toArray(new Transaksi[0]);
    }
    
    public Transaksi getTransaksiById(String id) {
        return transaksiList.stream()
                .filter(t -> Objects.equals(t.getId(), id))
                .findFirst()
                .orElse(null);
    }
    
    public Transaksi[] getAvailableJobs() {
        return transaksiList.stream()
                .filter(t -> "Menunggu Pengirim".equals(t.getCurrentStatus()))
                .toArray(Transaksi[]::new);
    }
    
    public void prosesTransaksi() {
        Date today = new Date();
        boolean updated = false;
        for (Transaksi transaksi : transaksiList) {
            String status = transaksi.getCurrentStatus();
            String jenis = transaksi.getJenisTransaksi();
            Date lastStatusDate = transaksi.getLatestStatusDate();
            Calendar deadline = Calendar.getInstance();
            deadline.setTime(lastStatusDate);
            if ("Sedang Dikirim".equals(status)) {
                switch (jenis) {
                    case "Instant":
                        transaksi.refund();
                        updated = true;
                        break;
                    case "Next Day":
                        deadline.add(Calendar.DAY_OF_MONTH, 1);
                        if (today.after(deadline.getTime())) {
                            transaksi.refund();
                            updated = true;
                    }
                        break;
                    case "Regular":
                        deadline.add(Calendar.DAY_OF_MONTH, 2);
                        if (today.after(deadline.getTime())) {
                            transaksi.refund();
                            updated = true;
                    }
                        break;
                }
            }
        }
        if (updated) {
            saveTransactionsToCsv();
        }
    }
    
    public void updateTransactionStatus(String id, String status) {
        Transaksi transaksi = getTransaksiById(id);
        if (transaksi != null) {
            transaksi.addStatus("Menunggu Pengirim");
            saveTransactionsToCsv();
        }
    }
    
    public void loadTransactionsFromCsv() {
        File file = new File(TRANSACTION_CSV_FILE);
        if (!file.exists()) {
            File dir = new File(CSV_DIRECTORY);
            if (!dir.exists() && !dir.mkdirs()) {
            }
            return;
        }
        
        try (CsvReader reader = new CsvReader(TRANSACTION_CSV_FILE)) {
            List<String> header = reader.readNext();
            if (header == null || header.isEmpty()) {
                return;
            }
            
            // Clear existing transactions before loading
            transaksiList.clear();
            
            List<String> row;
            while ((row = reader.readNext()) != null) {
                try {
                    if (!row.isEmpty()) {
                        String[] parts = row.get(0).split(",");
                        if (parts.length >= 7) {
                            List<String> parsed = new ArrayList<>();
                            for (String part : parts) parsed.add(part.trim());
                            Transaksi transaksi = createTransaksiFromCsvRow(parsed);
                            if (transaksi != null) transaksiList.add(transaksi);
                        } else {
                        }
                    }
                } catch (Exception e) {
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading transactions from CSV: {0}", e.getMessage());
        }
    }
    
    public void saveTransactionsToCsv() {
        try (CsvWriter writer = new CsvWriter(TRANSACTION_CSV_FILE)) {
            // Write header as a single column
            writer.writeRow(List.of("id,namaPembeli,namaPenjual,jenisTransaksi,biayaOngkir,produkDibeli,historyStatus"));
            
            // Write transaction data
            for (Transaksi transaksi : transaksiList) {
                List<String> csvFields = createCsvRowFromTransaksi(transaksi);
                // Join all fields with comma to create a single column CSV
                String singleColumn = String.join(",", csvFields);
                writer.writeRow(List.of(singleColumn));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving transactions to CSV", e);
        }
    }
    
    private Transaksi createTransaksiFromCsvRow(List<String> row) {
        if (row.size() < 7) {
            return null;
        }

        try {
            String id = row.get(0);
            String pembeli = row.get(1);
            String penjual = row.get(2);
            String jenis = row.get(3);
            long ongkir = Long.parseLong(row.get(4));
            String produkStr = row.get(5);
            String statusStr = row.get(6);

            // Buat objek transaksi tanpa status
            Transaksi transaksi = new Transaksi(pembeli, penjual, jenis);

            // Set ID dan biaya ongkir
            setField(transaksi, "id", id);
            setField(transaksi, "biayaOngkir", ongkir);

            // Produk
            if (!produkStr.isEmpty()) {
                for (String prod : produkStr.split(";")) {
                    String[] parts = prod.split(":");
                    try {
                        UUID productId = UUID.fromString(parts[0]);
                        int qty = (parts.length > 1) ? Integer.parseInt(parts[1]) : 1;
                        transaksi.addProduct(productId, qty);
                    } catch (Exception e) {
                    }
                }
            }

            // Status history
            ArrayList<TransactionStatus> historyStatus = getHistoryStatusList(transaksi);
            historyStatus.clear();

            if (!statusStr.isEmpty()) {
                for (String entry : statusStr.split(";")) {
                    int idx = entry.indexOf(':');
                    int idx2 = entry.indexOf(':', idx + 1);
                    if (idx != -1 && idx2 != -1) {
                        String dateStr = entry.substring(0, idx2);
                        String status = entry.substring(idx2 + 1);
                        try {
                            Date timestamp = DATE_FORMAT.parse(dateStr);
                            historyStatus.add(new TransactionStatus(timestamp, status));
                        } catch (ParseException e) {
                        }
                    } else {
                    }
                }
            }

            // Fallback jika tidak ada status valid
            if (historyStatus.isEmpty()) {
                historyStatus.add(new TransactionStatus(new Date(), "Sedang Dikemas"));
            }

            return transaksi;
        } catch (Exception e) {
            return null;
        }
    }
    
    private void setField(Transaksi transaksi, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = Transaksi.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(transaksi, value);
        } catch (Exception e) {
        }
    }
    
    @SuppressWarnings("unchecked")
    private ArrayList<TransactionStatus> getHistoryStatusList(Transaksi transaksi) {
        try {
            java.lang.reflect.Field field = Transaksi.class.getDeclaredField("historyStatus");
            field.setAccessible(true);
            return (ArrayList<TransactionStatus>) field.get(transaksi);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    private List<String> createCsvRowFromTransaksi(Transaksi transaksi) {
        List<String> row = new ArrayList<>();
        
        row.add(transaksi.getId());
        row.add(transaksi.getNamePembeli());
        row.add(transaksi.getNamePenjual());
        row.add(transaksi.getJenisTransaksi());
        row.add(String.valueOf(transaksi.getBiayaOngkir()));
        
        StringBuilder productsStr = new StringBuilder();
        TransaksiProduct[] products = transaksi.getProdukDibeli();
        for (int i = 0; i < products.length; i++) {
            if (i > 0) {
                productsStr.append(";");
            }
            productsStr.append(products[i].getProductId())
                       .append(":")
                       .append(products[i].getProductAmount());
        }
        row.add(productsStr.toString());
        
        // Status history (format: timestamp:status;timestamp:status;...)
        StringBuilder statusStr = new StringBuilder();
        TransactionStatus[] statuses = transaksi.getHistoryStatus();
        for (int i = 0; i < statuses.length; i++) {
            if (i > 0) {
                statusStr.append(";");
            }
            statusStr.append(DATE_FORMAT.format(statuses[i].getTimestamp()))
                    .append(":")
                    .append(statuses[i].getStatus());
        }
        row.add(statusStr.toString());
        
        return row;
    }

    public void clear() {
        transaksiList.clear();
        saveTransactionsToCsv();
    }

    public long calculateTotalTransaksi(String transaksiId) {
        Transaksi transaksi = getTransaksiById(transaksiId);
        if (transaksi == null) {
            return 0;
        }
        
        // Calculate subtotal
        long subtotal = 0;
        for (var product : transaksi.getProdukDibeli()) {
            UUID productId = product.getProductId();
            int amount = product.getProductAmount();
            
            // Get product price from repository
            var productOpt = productRepo.getProductById(productId);
            if (productOpt.isPresent()) {
                long price = productOpt.get().getProductPrice();
                subtotal += price * amount;
            }
        }
        
        // Calculate discount if applicable
        long discount = 0;
        String idDiskon = transaksi.getIdDiskon();
        if (idDiskon != null) {
            // Check if it's a voucher
            var voucher = voucherRepo.getById(idDiskon);
            if (voucher != null) {
                discount = voucher.calculateDisc(subtotal);
            } else {
                // Check if it's a promo
                var promo = promoRepo.getById(idDiskon);
                if (promo != null) {
                    discount = promo.calculateDisc(subtotal);
                }
            }
        }
        
        // Calculate total with tax and shipping
        long afterDiscount = subtotal - discount;
        long tax = (afterDiscount * 3) / 100; // 3% tax
        long shipping = transaksi.getBiayaOngkir();
        
        return afterDiscount + tax + shipping;
    }
}