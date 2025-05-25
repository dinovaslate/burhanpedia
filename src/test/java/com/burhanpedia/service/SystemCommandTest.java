package com.burhanpedia.service;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import com.burhanpedia.core.command.SystemCommand;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.burhanpedia.util.BurhanpediaLogger;

public class SystemCommandTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private SystemCommand systemCommand;
    private BurhanpediaLogger logger;
    
    @Before
    public void setUp() {
        // Redirect System.out untuk menangkap output
        System.setOut(new PrintStream(outContent));
        
        // Dapatkan instance logger dan reset
        logger = BurhanpediaLogger.getInstance();
        logger.disable();
    }
    
    @After
    public void tearDown() {
        // Kembalikan System.out dan System.in
        System.setOut(originalOut);
        System.setIn(originalIn);
    }
    
    @Test
    public void testInvalidCommand() {
        // Simulasi input: command tidak valid diikuti exit
        String input = "invalid command\nexit\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        // Buat SystemCommand dengan Scanner baru
        systemCommand = new SystemCommand(new Scanner(System.in));
        
        // Jalankan handleCommand
        systemCommand.handleCommand();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue("Output harus menunjukkan command tidak valid", 
                output.contains("Command tidak valid!"));
    }
    
    @Test
    public void testBurhanLoggerEnableWithoutFlags() {
        // Simulasi input: aktifkan logger tanpa flag, lalu exit
        String input = "burhanlogger enable\nexit\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        // Buat SystemCommand dengan Scanner baru
        systemCommand = new SystemCommand(new Scanner(System.in));
        
        // Jalankan handleCommand
        systemCommand.handleCommand();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue("Output harus menunjukkan logger diaktifkan", 
                output.contains("Logger diaktifkan dengan konfigurasi:"));
        assertTrue("Output harus menunjukkan timestamp nonaktif", 
                output.contains("- Timestamp: nonaktif"));
        assertTrue("Output harus menunjukkan category nonaktif", 
                output.contains("- Category: nonaktif"));
    }
    
    @Test
    public void testBurhanLoggerEnableWithFlags() {
        // Simulasi input: aktifkan logger dengan flag timestamp dan category, lalu exit
        String input = "burhanlogger enable -t -m\nexit\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        // Buat SystemCommand dengan Scanner baru
        systemCommand = new SystemCommand(new Scanner(System.in));
        
        // Jalankan handleCommand
        systemCommand.handleCommand();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue("Output harus menunjukkan logger diaktifkan", 
                output.contains("Logger diaktifkan dengan konfigurasi:"));
        assertTrue("Output harus menunjukkan timestamp aktif", 
                output.contains("- Timestamp: aktif"));
        assertTrue("Output harus menunjukkan category aktif", 
                output.contains("- Category: aktif"));
    }
    
    @Test
    public void testBurhanLoggerDisable() {
        // Simulasi input: nonaktifkan logger, lalu exit
        String input = "burhanlogger disable\nexit\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        // Buat SystemCommand dengan Scanner baru
        systemCommand = new SystemCommand(new Scanner(System.in));
        
        // Jalankan handleCommand
        systemCommand.handleCommand();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue("Output harus menunjukkan logger dinonaktifkan", 
                output.contains("Logger dinonaktifkan"));
    }
    
    @Test
    public void testInvalidBurhanLoggerCommand() {
        // Simulasi input: command BurhanLogger tidak valid, lalu exit
        String input = "burhanlogger invalid\nexit\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        
        // Buat SystemCommand dengan Scanner baru
        systemCommand = new SystemCommand(new Scanner(System.in));
        
        // Jalankan handleCommand
        systemCommand.handleCommand();
        
        // Verifikasi output
        String output = outContent.toString();
        assertTrue("Output harus menunjukkan command BurhanLogger tidak valid", 
                output.contains("Command BurhanLogger tidak valid!"));
    }
}