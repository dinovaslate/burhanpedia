package com.burhanpedia.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class CsvWriter implements AutoCloseable {
    private final BufferedWriter writer;
    private static final char SEPARATOR = ',';
    private static final char QUOTE = '"';
    private static final String LINE_SEPARATOR = System.lineSeparator();
    
    /**
     * Creates a new CSV writer for the specified file path.
     * 
     * @param path the file path to write to
     * @throws IOException if the file cannot be created or written to
     */
    public CsvWriter(String path) throws IOException {
        Objects.requireNonNull(path, "File path cannot be null");
        this.writer = createWriter(path);
    }
    
    private BufferedWriter createWriter(String path) throws IOException {
        File file = new File(path);
        ensureDirectoryExists(file);
        return new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(file),
                StandardCharsets.UTF_8
            )
        );
    }
    
    private void ensureDirectoryExists(File file) throws IOException {
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
            }
        }
    }
    
    /**
     * Writes a row of values to the CSV file.
     * This method handles escaping and quoting fields when necessary.
     * 
     * @param row a list of field values
     * @throws IOException if there is an error writing to the file
     */
    public void writeRow(List<String> row) throws IOException {
        if (row == null || row.isEmpty()) {
            return;
        }
        
        String csvLine = convertToCsvLine(row);
        writer.write(csvLine);
        writer.write(LINE_SEPARATOR);
        writer.flush();
    }
    
    private String convertToCsvLine(List<String> row) {
        return row.stream()
                 .map(this::formatField)
                 .reduce((field1, field2) -> field1 + SEPARATOR + field2)
                 .orElse("");
    }
    
    private String formatField(String field) {
        if (field == null) {
            return "";
        }
        
        if (needsQuoting(field)) {
            return QUOTE + escapeQuotes(field) + QUOTE;
        }
        
        return field;
    }
    
    private boolean needsQuoting(String field) {
        return field.contains(String.valueOf(SEPARATOR)) ||
               field.contains(String.valueOf(QUOTE)) ||
               field.contains("\n") ||
               field.contains("\r");
    }
    
    private String escapeQuotes(String field) {
        return field.replace(String.valueOf(QUOTE), String.valueOf(QUOTE) + String.valueOf(QUOTE));
    }
    
    /**
     * Writes a header row to the CSV file.
     * 
     * @param headers a list of header values
     * @throws IOException if there is an error writing to the file
     */
    public void writeHeader(List<String> headers) throws IOException {
        writeRow(headers);
    }
    
    @Override
    public void close() throws IOException {
        if (writer != null) {
            try {
                writer.flush();
            } finally {
                writer.close();
            }
        }
    }
}