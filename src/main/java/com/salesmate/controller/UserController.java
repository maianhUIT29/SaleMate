package com.salesmate.controller;

import com.salesmate.dao.UserDAO;
import com.salesmate.model.User;

public class UserController {
    private UserDAO userDAO;

    public UserController() {
        userDAO = new UserDAO();
    }

    // Hàm đăng nhập với email và password
    public User login(String email, String password) {
        return userDAO.login(email, password);
    }
}
