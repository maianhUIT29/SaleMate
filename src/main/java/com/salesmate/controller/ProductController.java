/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.salesmate.controller;

/**
 *
 * @author meiln
 */

import com.salesmate.dao.ProductDAO;
import com.salesmate.model.Product;
import java.util.List;

public class ProductController {
    private ProductDAO productDAO;

    public ProductController() {
        productDAO = new ProductDAO();
    }

    public boolean addProduct(Product product) {
        try {
            return productDAO.createProduct(product);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProduct(Product product) {
        try {
            return productDAO.updateProduct(product);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(int productId) {
        try {
            return productDAO.deleteProduct(productId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Product getProductById(int productId) {
        try {
            return productDAO.getProductById(productId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Product> getAllProducts() {
        try {
            return productDAO.getAllProducts();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
