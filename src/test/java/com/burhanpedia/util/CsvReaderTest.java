package com.burhanpedia.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CsvReaderTest {
    private static final String TEST_FILE = "test_input.csv";
    
    @Before
    public void setUp() throws IOException {
        // Buat file test
        try (FileWriter writer = new FileWriter(TEST_FILE)) {
            writer.write("Header1,Header2,Header3\n");
            writer.write("Value1,Value2,Value3\n");
            writer.write("\"Value with, comma\",\"Value with \"\"quotes\"\"\",Simple Value\n");
        }
    }
    
    @After
    public void tearDown() {
        // Hapus file test
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
    
    @Test
    public void testReadNext() throws IOException {
        try (CsvReader reader = new CsvReader(TEST_FILE)) {
            // Baca header
            List<String> header = reader.readNext();
            assertNotNull("Header tidak boleh null", header);
            assertEquals("Jumlah kolom header harus 3", 3, header.size());
            assertEquals("Header1", header.get(0));
            assertEquals("Header2", header.get(1));
            assertEquals("Header3", header.get(2));
            
            // Baca baris pertama
            List<String> row1 = reader.readNext();
            assertNotNull("Baris 1 tidak boleh null", row1);
            assertEquals("Jumlah kolom baris 1 harus 3", 3, row1.size());
            assertEquals("Value1", row1.get(0));
            assertEquals("Value2", row1.get(1));
            assertEquals("Value3", row1.get(2));
            
            // Baca baris dengan karakter khusus
            List<String> row2 = reader.readNext();
            assertNotNull("Baris 2 tidak boleh null", row2);
            assertEquals("Jumlah kolom baris 2 harus 3", 3, row2.size());
            assertEquals("Value with, comma", row2.get(0));
            assertEquals("Value with \"quotes\"", row2.get(1));
            assertEquals("Simple Value", row2.get(2));
            
            // Pastikan sudah mencapai akhir file
            List<String> endOfFile = reader.readNext();
            assertNull("Seharusnya sudah mencapai akhir file", endOfFile);
        }
    }
    
    @Test(expected = IOException.class)
    public void testFileNotFound() throws IOException {
        // Coba baca file yang tidak ada
        new CsvReader("non_existent_file.csv");
    }
}