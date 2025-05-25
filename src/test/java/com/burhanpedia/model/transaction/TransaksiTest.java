package com.burhanpedia.model.transaction;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

public class TransaksiTest {
    private Transaksi transaksi;
    private String namePembeli = "testPembeli";
    private String namePenjual = "testPenjual";
    private String jenisTransaksi = "Regular";
    
    @Before
    public void setUp() {
        transaksi = new Transaksi(namePembeli, namePenjual, jenisTransaksi);
    }
    
    @Test
    public void testTransaksiConstructor() {
        assertNotNull(transaksi);
        assertNotNull(transaksi.getId());
        assertTrue(transaksi.getId().startsWith("TRX"));
        assertEquals(namePembeli, transaksi.getNamePembeli());
        assertEquals(namePenjual, transaksi.getNamePenjual());
        assertEquals(jenisTransaksi, transaksi.getJenisTransaksi());
        assertNull(transaksi.getNamePengirim());
        assertNull(transaksi.getIdDiskon());
        assertEquals("Sedang Dikemas", transaksi.getCurrentStatus());
        assertEquals(10000, transaksi.getBiayaOngkir());
    }
    
    @Test
    public void testOngkirInstant() {
        Transaksi instantTransaksi = new Transaksi(namePembeli, namePenjual, "Instant");
        assertEquals(20000, instantTransaksi.getBiayaOngkir());
    }
    
    @Test
    public void testOngkirNextDay() {
        Transaksi nextDayTransaksi = new Transaksi(namePembeli, namePenjual, "Next Day");
        assertEquals(15000, nextDayTransaksi.getBiayaOngkir());
    }
    
    @Test
    public void testOngkirRegular() {
        assertEquals(10000, transaksi.getBiayaOngkir());
    }
    
    @Test
    public void testAddProduct() {
        UUID productId = UUID.randomUUID();
        int amount = 5;
        transaksi.addProduct(productId, amount);
        
        TransaksiProduct[] products = transaksi.getProdukDibeli();
        assertEquals(1, products.length);
        assertEquals(productId, products[0].getProductId());
        assertEquals(amount, products[0].getProductAmount());
    }
    
    @Test
    public void testAddMultipleProducts() {
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        
        transaksi.addProduct(productId1, 3);
        transaksi.addProduct(productId2, 2);
        
        TransaksiProduct[] products = transaksi.getProdukDibeli();
        assertEquals(2, products.length);
    }
    
    @Test
    public void testAddStatus() {
        assertEquals("Sedang Dikemas", transaksi.getCurrentStatus());
        
        transaksi.addStatus("Menunggu Pengirim");
        assertEquals("Menunggu Pengirim", transaksi.getCurrentStatus());
        
        TransactionStatus[] history = transaksi.getHistoryStatus();
        assertEquals(2, history.length);
        assertEquals("Sedang Dikemas", history[0].getStatus());
        assertEquals("Menunggu Pengirim", history[1].getStatus());
    }
    
    @Test
    public void testSetNamePengirim() {
        assertNull(transaksi.getNamePengirim());
        
        String namePengirim = "testPengirim";
        transaksi.setNamePengirim(namePengirim);
        
        assertEquals(namePengirim, transaksi.getNamePengirim());
    }
    
    @Test
    public void testSetIdDiskon() {
        assertNull(transaksi.getIdDiskon());
        
        String idDiskon = "DISC123";
        transaksi.setIdDiskon(idDiskon);
        
        assertEquals(idDiskon, transaksi.getIdDiskon());
    }
    
    @Test
    public void testRefund() {
        assertEquals("Sedang Dikemas", transaksi.getCurrentStatus());
        
        transaksi.refund();
        
        assertEquals("Dikembalikan", transaksi.getCurrentStatus());
    }
    
    @Test
    public void testGetLatestStatus() {
        TransactionStatus initialStatus = transaksi.getLatestStatus();
        assertEquals("Sedang Dikemas", initialStatus.getStatus());
        
        transaksi.addStatus("Menunggu Pengirim");
        transaksi.addStatus("Sedang Dikirim");
        
        TransactionStatus latestStatus = transaksi.getLatestStatus();
        assertEquals("Sedang Dikirim", latestStatus.getStatus());
    }
    
    @Test
    public void testGetLatestStatusDate() {
        Date initialDate = transaksi.getLatestStatusDate();
        assertNotNull(initialDate);
        
        try {
            Thread.sleep(10); // Tunggu sedikit agar tanggal berbeda
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        transaksi.addStatus("Menunggu Pengirim");
        Date newDate = transaksi.getLatestStatusDate();
        
        assertTrue(newDate.after(initialDate));
    }
    
    @Test
    public void testSetAndGetTotalHarga() {
        long totalHarga = 150000;
        transaksi.setTotalHarga(totalHarga);
        assertEquals(totalHarga, transaksi.getTotalHarga());
    }
    
    
    @Test
    public void testGenerateTransactionId() {
        String id = transaksi.getId();
        assertTrue(id.startsWith("TRX"));
        assertEquals(19, id.length()); // Format: TRX + yyyyMMdd + 4 digit random
    }
}