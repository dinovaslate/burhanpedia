package com.burhanpedia.annotation;

import java.lang.reflect.Field;

public class ProductValidator {
    
    /**
     * Validates a product using reflection to check fields with @ProductValidation annotation
     * 
     * @param product Object to validate
     * @return String containing error message, or null if validation succeeds
     */
    public static String validate(Object product) {
        if (product == null) {
            return "Product cannot be null";
        }
        
        Class<?> clazz = product.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            if (field.isAnnotationPresent(ProductValidation.class)) {
                field.setAccessible(true);
                
                try {
                    ProductValidation annotation = field.getAnnotation(ProductValidation.class);
                    Object value = field.get(product);
                    
                    // Check numeric values for negativity
                    if (value instanceof Number) {
                        Number numValue = (Number) value;
                        
                        // Check if negative values are allowed
                        if (!annotation.allowNegative() && numValue.doubleValue() < 0) {
                            return annotation.message();
                        }
                    }
                    
                } catch (IllegalAccessException e) {
                    return "Error accessing field: " + field.getName();
                }
            }
        }
        
        return null; // Validation passed
    }
}