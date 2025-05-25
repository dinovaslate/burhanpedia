package com.burhanpedia.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CsvReader implements AutoCloseable {
    private final BufferedReader reader;
    private static final char SEPARATOR = ',';
    private static final char QUOTE = '"';
    
    /**
     * Creates a new CSV reader for the specified file path.
     * 
     * @param path the file path to read from
     * @throws IOException if the file cannot be read
     */
    public CsvReader(String path) throws IOException {
        Objects.requireNonNull(path, "File path cannot be null");
        this.reader = createReader(path);
    }
    
    private BufferedReader createReader(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("File not found: " + path);
        }
        return new BufferedReader(
            new InputStreamReader(
                new FileInputStream(file),
                StandardCharsets.UTF_8
            )
        );
    }
    
    /**
     * Reads the next line of the CSV file and parses it into a list of values.
     * This method handles quoted fields and fields containing commas.
     * 
     * @return a list of field values or null if end of file
     * @throws IOException if there is an error reading the file
     */
    public List<String> readNext() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }
        return parseCsvLine(line);
    }
    
    private List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == QUOTE) {
                handleQuote(line, i, inQuotes, currentField);
                inQuotes = !inQuotes;
            } else if (c == SEPARATOR && !inQuotes) {
                fields.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }
        
        fields.add(currentField.toString().trim());
        return fields;
    }
    
    private void handleQuote(String line, int currentIndex, boolean inQuotes, StringBuilder currentField) {
        if (inQuotes && currentIndex + 1 < line.length() && line.charAt(currentIndex + 1) == QUOTE) {
            currentField.append(QUOTE);
        }
    }
    
    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
}