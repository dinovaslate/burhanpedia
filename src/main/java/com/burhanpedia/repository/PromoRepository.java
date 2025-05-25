package com.burhanpedia.repository;

import com.burhanpedia.model.discount.Promo;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PromoRepository implements DiskonRepository<Promo> {
    private final List<Promo> promoList;
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
    private static final int RANDOM_PART_LENGTH = 8;
    private static final int TIMESTAMP_PART_LENGTH = 8;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private final SecureRandom random;

    public PromoRepository() {
        this.promoList = new ArrayList<>();
        this.random = new SecureRandom();
    }

    @Override
    public Promo getById(String id) {
        return promoList.stream()
                .filter(promo -> Objects.equals(promo.getId(), id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Promo[] getAll() {
        return promoList.toArray(new Promo[0]);
    }

    @Override
    public void generate(String berlakuHingga) {
        try {
            Date date = new SimpleDateFormat(DATE_FORMAT).parse(berlakuHingga);
            String id = generatePromoCode();
            Promo promo = new Promo(id, date);
            promoList.add(promo);
            System.out.println("Promo berhasil dibuat: " + id);
        } catch (ParseException e) {
            System.out.println("Format tanggal salah! Gunakan format YYYY-MM-DD");
        }
    }

    private String generatePromoCode() {
        StringBuilder sb = new StringBuilder(RANDOM_PART_LENGTH + TIMESTAMP_PART_LENGTH);
        for (int i = 0; i < RANDOM_PART_LENGTH; i++) {
            int idx = random.nextInt(CHAR_POOL.length());
            sb.append(CHAR_POOL.charAt(idx));
        }
        long timestamp = System.currentTimeMillis() / 1000L;
        String encodedTimestamp = Base64.getEncoder().encodeToString(String.valueOf(timestamp).getBytes());
        sb.append(encodedTimestamp, 0, Math.min(TIMESTAMP_PART_LENGTH, encodedTimestamp.length()));
        return sb.toString();
    }

    public void addPromo(Promo promo) {
        if (promo != null && getById(promo.getId()) == null) {
            promoList.add(promo);
        }
    }

    public void clear() {
        promoList.clear();
    }
}