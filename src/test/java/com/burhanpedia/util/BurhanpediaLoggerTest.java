package com.burhanpedia.util;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BurhanpediaLoggerTest {
    private BurhanpediaLogger logger;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private static final String LOG_FILE = "data/logs.csv";
    
    @Before
    public void setUp() {
        // Redirect System.out untuk menangkap output
        System.setOut(new PrintStream(outContent));
        
        // Dapatkan instance logger
        logger = BurhanpediaLogger.getInstance();
        
        // Reset status logger
        logger.disable();
    }
    
    @After
    public void tearDown() {
        // Kembalikan System.out
        System.setOut(originalOut);
    }
    
    @Test
    public void testSingleton() {
        // Verifikasi bahwa getInstance selalu mengembalikan instance yang sama
        BurhanpediaLogger instance1 = BurhanpediaLogger.getInstance();
        BurhanpediaLogger instance2 = BurhanpediaLogger.getInstance();
        assertSame("getInstance harus mengembalikan instance yang sama", instance1, instance2);
    }
    
    @Test
    public void testLoggerDisabled() {
        // Pastikan logger dinonaktifkan
        logger.disable();
        
        // Log pesan
        logger.log("Test", "Test message");
        
        // Verifikasi tidak ada output
        assertEquals("Tidak boleh ada output saat logger dinonaktifkan", "", outContent.toString());
    }
    
    @Test
    public void testLoggerEnabled() {
        // Aktifkan logger tanpa timestamp dan category
        logger.enable(false, false);
        
        // Log pesan
        logger.log("Test", "Test message");
        
        // Verifikasi output
        assertEquals("Output harus sesuai", "Test message\n", outContent.toString());
    }
    
    @Test
    public void testLoggerWithTimestamp() {
        // Aktifkan logger dengan timestamp
        logger.enable(true, false);
        
        // Log pesan
        logger.log("Test", "Test message");
        
        // Verifikasi output mengandung timestamp (format: [yyyy-MM-dd HH:mm:ss])
        String output = outContent.toString();
        assertTrue("Output harus mengandung timestamp", output.matches("\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\] Test message\n"));
    }
    
    @Test
    public void testLoggerWithCategory() {
        // Aktifkan logger dengan category
        logger.enable(false, true);
        
        // Log pesan
        logger.log("Test", "Test message");
        
        // Verifikasi output
        assertEquals("Output harus sesuai", "[Test] Test message\n", outContent.toString());
    }
    
    @Test
    public void testLoggerWithTimestampAndCategory() {
        // Aktifkan logger dengan timestamp dan category
        logger.enable(true, true);
        
        // Log pesan
        logger.log("Test", "Test message");
        
        // Verifikasi output mengandung timestamp dan category
        String output = outContent.toString();
        assertTrue("Output harus mengandung timestamp dan category", 
                output.matches("\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\] \\[Test\\] Test message\n"));
    }
    
    @Test
    public void testLogFileCreation() {
        // Verifikasi file log dibuat
        File logFile = new File(LOG_FILE);
        assertTrue("File log harus ada", logFile.exists());
    }
}