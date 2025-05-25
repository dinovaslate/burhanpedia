package com.burhanpedia.repository;

import com.burhanpedia.model.discount.Voucher;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class VoucherRepository implements DiskonRepository<Voucher> {
    private final List<Voucher> voucherList;
    private static final int VOUCHER_CODE_LENGTH = 10;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private final Random random;

    public VoucherRepository() {
        this.voucherList = new ArrayList<>();
        this.random = new Random();
    }

    @Override
    public Voucher getById(String id) {
        return voucherList.stream()
                .filter(voucher -> Objects.equals(voucher.getId(), id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Voucher[] getAll() {
        return voucherList.toArray(new Voucher[0]);
    }

    @Override
    public void generate(String berlakuHingga) {
        try {
            Date date = new SimpleDateFormat(DATE_FORMAT).parse(berlakuHingga);
            String id = generateVoucherCode();
            Voucher voucher = new Voucher(id, date);
            voucherList.add(voucher);
            System.out.println("Voucher berhasil dibuat: " + id);
        } catch (ParseException e) {
            System.out.println("Format tanggal salah! Gunakan format YYYY-MM-DD");
        }
    }

    private String generateVoucherCode() {
        StringBuilder sb = new StringBuilder(VOUCHER_CODE_LENGTH);
        for (int i = 0; i < VOUCHER_CODE_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public void addVoucher(Voucher voucher) {
        if (voucher != null && getById(voucher.getId()) == null) {
            voucherList.add(voucher);
        }
    }

    public void clear() {
        voucherList.clear();
    }
}