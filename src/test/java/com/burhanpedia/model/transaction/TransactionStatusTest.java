package com.burhanpedia.model.transaction;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class TransactionStatusTest {
    private TransactionStatus status;
    private Date timestamp;
    private String statusText;
    
    @Before
    public void setUp() {
        timestamp = new Date();
        statusText = "Sedang Dikemas";
        status = new TransactionStatus(timestamp, statusText);
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(status);
        assertEquals(timestamp, status.getTimestamp());
        assertEquals(statusText, status.getStatus());
    }
    
    @Test
    public void testGetTimestamp() {
        assertEquals(timestamp, status.getTimestamp());
    }
    
    @Test
    public void testGetStatus() {
        assertEquals(statusText, status.getStatus());
    }
    
    @Test
    public void testDifferentStatuses() {
        TransactionStatus status1 = new TransactionStatus(timestamp, "Sedang Dikemas");
        TransactionStatus status2 = new TransactionStatus(timestamp, "Sedang Dikirim");
        
        assertEquals(status1.getTimestamp(), status2.getTimestamp());
        assertNotEquals(status1.getStatus(), status2.getStatus());
    }
    
    @Test
    public void testDifferentTimestamps() {
        Date timestamp1 = new Date();
        try {
            Thread.sleep(10); // Tunggu sedikit agar timestamp berbeda
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Date timestamp2 = new Date();
        
        TransactionStatus status1 = new TransactionStatus(timestamp1, "Sedang Dikemas");
        TransactionStatus status2 = new TransactionStatus(timestamp2, "Sedang Dikemas");
        
        assertNotEquals(status1.getTimestamp(), status2.getTimestamp());
        assertEquals(status1.getStatus(), status2.getStatus());
        assertTrue(timestamp2.after(timestamp1));
    }
}