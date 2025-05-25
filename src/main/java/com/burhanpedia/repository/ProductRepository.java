package com.burhanpedia.repository;

import com.burhanpedia.model.product.Product;
import com.burhanpedia.util.CsvReader;
import com.burhanpedia.util.CsvWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductRepository {
    private static final Logger LOGGER = Logger.getLogger(ProductRepository.class.getName());
    private static final String CSV_DIRECTORY = "data";
    private static final String PRODUCT_CSV_FILE = CSV_DIRECTORY + File.separator + "products.csv";
    
    private final String namaToko;
    public final List<Product> productList;
    
    public ProductRepository(String namaToko) {
        this.namaToko = namaToko;
        this.productList = new ArrayList<>();
        loadProductsFromCsv();
    }
    
    public String getNamaToko() {
        return namaToko;
    }
    
    private void loadProductsFromCsv() {
        File file = new File(PRODUCT_CSV_FILE);
        if (!file.exists()) {
            File dir = new File(CSV_DIRECTORY);
            if (!dir.exists() && !dir.mkdirs()) {
            }
            return;
        }
        
        try (CsvReader reader = new CsvReader(PRODUCT_CSV_FILE)) {
            List<String> header = reader.readNext();
            if (header == null) {
                return;
            }
            
            List<String> row;
            while ((row = reader.readNext()) != null) {
                try {
                    if (row.size() >= 5 && row.get(1).equals(this.namaToko)) {
                        Product product = createProductFromCsvRow(row);
                        if (product != null) {
                            productList.add(product);
                        }
                    }
                } catch (Exception e) {
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading products from CSV", e);
        }
    }
    
    public void saveProductsToCsv() {
        try (CsvWriter writer = new CsvWriter(PRODUCT_CSV_FILE)) {
            File file = new File(PRODUCT_CSV_FILE);
            if (!file.exists() || file.length() == 0) {
                List<String> header = List.of("productId", "namaToko", "productName", "stock", "price");
                writer.writeHeader(header);
            }
            
            for (Product product : productList) {
                List<String> row = createCsvRowFromProduct(product);
                writer.writeRow(row);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving products to CSV", e);
        }
    }
    
    private Product createProductFromCsvRow(List<String> row) {
        try {
            UUID productId = UUID.fromString(row.get(0));
            String productName = row.get(2);
            int stock = Integer.parseInt(row.get(3));
            long price = Long.parseLong(row.get(4));
            
            Product product = new Product(productName, stock, price);
            product.setProductStock(stock);
            
            setField(product, "productId", productId);
            
            return product;
        } catch (Exception e) {
            return null;
        }
    }
    
    private void setField(Product product, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = Product.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(product, value);
        } catch (Exception e) {
        }
    }
    
    private List<String> createCsvRowFromProduct(Product product) {
        List<String> row = new ArrayList<>();
        row.add(product.getProductId().toString());
        row.add(this.namaToko);
        row.add(product.getProductName());
        row.add(String.valueOf(product.getProductStock()));
        row.add(String.valueOf(product.getProductPrice()));
        return row;
    }
    
    public Optional<Product> getProductById(UUID id) {
        return productList.stream()
                .filter(product -> product.getProductId().equals(id))
                .findFirst();
    }
    
    public Product getProductByName(String name) {
        return productList.stream()
                .filter(product -> product.getProductName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
    
    public Product[] getProductList() {
        return productList.toArray(new Product[0]);
    }

    public void addProduct(Product product) {
        productList.add(product);
        saveProductsToCsv();
    }
}