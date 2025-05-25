package com.burhanpedia.util;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CsvWriterTest {
    private static final String TEST_FILE = "test_output.csv";
    private CsvWriter csvWriter;
    
    @Before
    public void setUp() throws IOException {
        // Buat instance baru untuk setiap test
        csvWriter = new CsvWriter(TEST_FILE);
    }
    
    @After
    public void tearDown() {
        // Hapus file test setelah selesai
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
    
    @Test
    public void testWriteRow() throws IOException {
        // Tulis baris data sederhana
        List<String> row = Arrays.asList("data1", "data2", "data3");
        csvWriter.writeRow(row);
        csvWriter.close();
        
        // Baca file dan verifikasi
        try (BufferedReader reader = new BufferedReader(new FileReader(TEST_FILE))) {
            String line = reader.readLine();
            assertEquals("Baris yang ditulis harus sesuai", "data1,data2,data3", line);
        }
    }
    
    @Test
    public void testWriteHeader() throws IOException {
        // Tulis header
        List<String> headers = Arrays.asList("Header1", "Header2", "Header3");
        csvWriter.writeHeader(headers);
        csvWriter.close();
        
        // Baca file dan verifikasi
        try (BufferedReader reader = new BufferedReader(new FileReader(TEST_FILE))) {
            String line = reader.readLine();
            assertEquals("Header yang ditulis harus sesuai", "Header1,Header2,Header3", line);
        }
    }
    
    @Test
    public void testWriteRowWithSpecialCharacters() throws IOException {
        // Tulis baris dengan karakter khusus
        List<String> row = Arrays.asList("data with, comma", "data with \"quotes\"", "normal data");
        csvWriter.writeRow(row);
        csvWriter.close();
        
        // Baca file dan verifikasi
        try (BufferedReader reader = new BufferedReader(new FileReader(TEST_FILE))) {
            String line = reader.readLine();
            assertEquals("Baris dengan karakter khusus harus ditulis dengan benar", 
                    "\"data with, comma\",\"data with \"\"quotes\"\"\",normal data", line);
        }
    }
    
    @Test
    public void testWriteMultipleRows() throws IOException {
        // Tulis beberapa baris
        List<String> header = Arrays.asList("Name", "Age", "City");
        List<String> row1 = Arrays.asList("John", "30", "New York");
        List<String> row2 = Arrays.asList("Jane", "25", "Los Angeles");
        
        csvWriter.writeHeader(header);
        csvWriter.writeRow(row1);
        csvWriter.writeRow(row2);
        csvWriter.close();
        
        // Baca file dan verifikasi
        try (BufferedReader reader = new BufferedReader(new FileReader(TEST_FILE))) {
            String line1 = reader.readLine();
            String line2 = reader.readLine();
            String line3 = reader.readLine();
            
            assertEquals("Header harus sesuai", "Name,Age,City", line1);
            assertEquals("Baris 1 harus sesuai", "John,30,New York", line2);
            assertEquals("Baris 2 harus sesuai", "Jane,25,Los Angeles", line3);
        }
    }
}