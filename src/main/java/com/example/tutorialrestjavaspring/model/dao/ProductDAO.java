package com.example.tutorialrestjavaspring.model.dao;

import com.example.tutorialrestjavaspring.model.Product;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface ProductDAO extends Repository<Product, Long> {
    List<Product> findAll();
}
