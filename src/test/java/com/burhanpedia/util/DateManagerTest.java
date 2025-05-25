package com.burhanpedia.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DateManagerTest {
    private DateManager dateManager;
    private static final String DATE_FILE = "data/date.txt";
    
    @Before
    public void setUp() {
        // Dapatkan instance DateManager
        dateManager = DateManager.getInstance();
    }
    
    @Test
    public void testSingleton() {
        // Verifikasi bahwa getInstance selalu mengembalikan instance yang sama
        DateManager instance1 = DateManager.getInstance();
        DateManager instance2 = DateManager.getInstance();
        assertSame("getInstance harus mengembalikan instance yang sama", instance1, instance2);
    }
    
    @Test
    public void testGetCurrentDate() {
        // Dapatkan tanggal saat ini
        LocalDate currentDate = dateManager.getCurrentDate();
        
        // Verifikasi tanggal tidak null
        assertNotNull("Tanggal saat ini tidak boleh null", currentDate);
    }
    
    @Test
    public void testGetCurrentDateFormatted() {
        // Dapatkan tanggal terformat
        String formattedDate = dateManager.getCurrentDateFormatted();
        
        // Verifikasi format tanggal (contoh: "Monday, 01 January 2023")
        assertNotNull("Tanggal terformat tidak boleh null", formattedDate);
        assertTrue("Format tanggal harus sesuai", 
                formattedDate.matches("[A-Za-z]+, \\d{2} [A-Za-z]+ \\d{4}"));
    }
    
    @Test
    public void testNextDay() throws IOException {
        // Simpan tanggal saat ini
        LocalDate beforeNextDay = dateManager.getCurrentDate();
        
        // Panggil nextDay
        dateManager.nextDay();
        
        // Dapatkan tanggal setelah nextDay
        LocalDate afterNextDay = dateManager.getCurrentDate();
        
        // Verifikasi tanggal bertambah 1 hari
        assertEquals("Tanggal harus bertambah 1 hari", 
                beforeNextDay.plusDays(1), afterNextDay);
        
        // Verifikasi file date.txt ada
        File dateFile = new File(DATE_FILE);
        assertTrue("File date.txt harus ada", dateFile.exists());
    }
    
    @Test
    public void testDateFilePersistence() throws IOException {
        // Simpan tanggal saat ini
        LocalDate originalDate = dateManager.getCurrentDate();
        
        // Panggil nextDay untuk mengubah tanggal
        dateManager.nextDay();
        
        // Buat instance DateManager baru (akan membaca dari file)
        DateManager newInstance = DateManager.getInstance();
        
        // Verifikasi tanggal sama dengan yang disimpan
        assertEquals("Tanggal harus sama dengan yang disimpan", 
                originalDate.plusDays(1), newInstance.getCurrentDate());
    }
}