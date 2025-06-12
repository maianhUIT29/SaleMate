# SalesMate

## Stock Forecast API Integration

SalesMate giờ đây tích hợp với Python FastAPI để dự đoán sản phẩm sắp hết hàng trên Dashboard.

### Các bước để chạy:

1. **Cài đặt thư viện Python**:
   - Chạy `install_api_requirements.bat` để cài đặt các thư viện Python cần thiết.

2. **Tạo mô hình dự đoán mẫu**:
   - Chạy `create_sample_models.bat` để tạo các mô hình dự đoán mẫu (cần thiết cho bước 3).

3. **Khởi động API**:
   - Chạy `start_api.bat` để khởi động Python FastAPI trên http://localhost:8000.
   - API Documentation có thể xem tại http://localhost:8000/docs.

4. **Chạy SalesMate**:
   - Khởi động ứng dụng SalesMate như bình thường.
   - Dashboard sẽ hiển thị "Sản phẩm sắp hết hàng (AI Prediction)" sử dụng dự đoán từ API.

### Xử lý lỗi:

- Nếu API không khả dụng, SalesMate sẽ tự động trở về phương thức dự đoán đơn giản dựa trên truy vấn database.
- Nếu không tìm thấy mô hình cho sản phẩm, SalesMate sẽ sử dụng dự đoán rule-based.