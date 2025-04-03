package com.salesmate.controller;

import java.util.List;

import com.salesmate.dao.ProductDAO;
import com.salesmate.model.Product;

public class ProductController{

    private ProductDAO productDAO;

    public ProductController() {
        productDAO = new ProductDAO();
    }


    // Lấy danh sách tất cả sản phẩm
    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    // lấy sản phẩm theo ID
    public Product getProductById(int productId) {
        return productDAO.getProductById(productId);
    }
}