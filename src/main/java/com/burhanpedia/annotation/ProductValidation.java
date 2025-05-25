package com.burhanpedia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for validating product fields
 * Used with ProductValidator to automatically validate fields in Product class
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ProductValidation {
    /**
     * Whether negative values are allowed for numeric fields
     */
    boolean allowNegative() default false;
    
    /**
     * Validation error message
     */
    String message() default "Invalid value";
}