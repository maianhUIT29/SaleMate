package com.salesmate.controller;

import java.util.List;

import com.salesmate.dao.ProductDAO;
import com.salesmate.model.Product;

public class ProductController {
    private ProductDAO productDAO;

    public ProductController() {
        productDAO = new ProductDAO();
    }

    // CREATE
    public boolean addProduct(Product product) {
        try {
            return productDAO.createProduct(product);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ ALL
    public List<Product> getAllProducts() {
        try {
            return productDAO.getAllProducts();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // READ by ID
    public Product getProductById(int productId) {
        try {
            return productDAO.getProductById(productId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // UPDATE
    public boolean updateProduct(Product product) {
        try {
            return productDAO.updateProduct(product);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
