package com.example.tutorialrestjavaspring.service;

import com.example.tutorialrestjavaspring.model.Product;
import com.example.tutorialrestjavaspring.model.dao.ProductDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }
}
