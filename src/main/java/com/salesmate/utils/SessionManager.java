package com.salesmate.utils;

import com.salesmate.model.User;

public class SessionManager {

    private static SessionManager instance;  // Biến singleton
    private User loggedInUser;  // Lưu trữ thông tin người dùng đã đăng nhập

    // Constructor riêng tư để tránh khởi tạo từ bên ngoài
    private SessionManager() {
    }

    // Phương thức để lấy instance duy nhất của SessionManager
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Lấy thông tin người dùng đã đăng nhập
    public User getLoggedInUser() {
        return loggedInUser;
    }

    // Thiết lập người dùng đăng nhập
    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    // Đăng xuất (reset trạng thái người dùng)
    public void logout() {
        this.loggedInUser = null;
    }

    // Kiểm tra trạng thái đăng nhập
    public boolean isUserLoggedIn() {
        return loggedInUser != null;
    }
}
