package com.burhanpedia.repository;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import com.burhanpedia.model.product.Product;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public class ProductRepositoryTest {
    
    private ProductRepository productRepo;
    private final String TEST_TOKO = "TestToko";
    private Product testProduct;
    
    @Before
    public void setUp() {
        productRepo = new ProductRepository(TEST_TOKO);
        testProduct = new Product("TestProduct", 10, 50000);
        productRepo.productList.add(testProduct);
    }
    
    @After
    public void tearDown() {
        // Hapus file CSV test jika ada
        File file = new File("data/products.csv");
        if (file.exists()) {
            file.delete();
        }
    }
    
    @Test
    public void testConstructor() {
        assertEquals(TEST_TOKO, productRepo.getNamaToko());
        assertNotNull(productRepo.productList);
    }
    
    @Test
    public void testGetNamaToko() {
        assertEquals(TEST_TOKO, productRepo.getNamaToko());
    }
    
    @Test
    public void testGetProductById_ExistingProduct() {
        UUID productId = testProduct.getProductId();
        Optional<Product> product = productRepo.getProductById(productId);
        
        assertTrue(product.isPresent());
        assertEquals(productId, product.get().getProductId());
        assertEquals("TestProduct", product.get().getProductName());
    }
    
    @Test
    public void testGetProductById_NonExistingProduct() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<Product> product = productRepo.getProductById(nonExistentId);
        
        assertFalse(product.isPresent());
    }
    
    @Test
    public void testGetProductByName_ExistingProduct() {
        Product product = productRepo.getProductByName("TestProduct");
        
        assertNotNull(product);
        assertEquals("TestProduct", product.getProductName());
        assertEquals(10, product.getProductStock());
        assertEquals(50000, product.getProductPrice());
    }
    
    @Test
    public void testGetProductByName_NonExistingProduct() {
        Product product = productRepo.getProductByName("NonExistentProduct");
        
        assertNull(product);
    }
    
    @Test
    public void testGetProductList() {
        Product[] products = productRepo.getProductList();
        
        assertNotNull(products);
        assertEquals(1, products.length);
        assertEquals("TestProduct", products[0].getProductName());
    }
    
    @Test
    public void testSaveAndLoadProductsToCsv() {
        // Simpan produk ke CSV
        productRepo.saveProductsToCsv();
        
        // Buat repository baru untuk memuat dari CSV
        ProductRepository newRepo = new ProductRepository(TEST_TOKO);
        
        // Verifikasi produk dimuat dengan benar
        assertEquals(1, newRepo.productList.size());
        assertEquals("TestProduct", newRepo.productList.get(0).getProductName());
        assertEquals(10, newRepo.productList.get(0).getProductStock());
        assertEquals(50000, newRepo.productList.get(0).getProductPrice());
    }
}