package com.burhanpedia.repository;

public interface DiskonRepository<T> {
    T getById(String id);
    T[] getAll();
    void generate(String berlakuHingga);
}