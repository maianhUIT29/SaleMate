package com.salesmate.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP client để gọi Stock Forecast API (Python FastAPI)
 * Không sử dụng thư viện Jackson, chỉ dùng các thư viện tiêu chuẩn của Java
 */
public class StockForecastAPI {
    private static final String API_BASE_URL = "http://localhost:8000";
    
    /**
     * Response model cho predict API
     */
    public static class PredictResponse {
        public int productId;
        public int horizon;
        public Map<String, Double> dailyForecast;
        public String predictedOutDate;
        public String modelName;
        
        public PredictResponse() {
            this.dailyForecast = new HashMap<>();
        }
    }
    
    /**
     * Model cho low stock prediction result
     */
    public static class LowStockPrediction {
        public int productId;
        public String productName;
        public int currentStock;
        public int daysUntilOutOfStock;
        public String predictedOutDate;
        public String modelName;
        
        public LowStockPrediction(int productId, String productName, int currentStock, 
                                 int daysUntilOutOfStock, String predictedOutDate, String modelName) {
            this.productId = productId;
            this.productName = productName;
            this.currentStock = currentStock;
            this.daysUntilOutOfStock = daysUntilOutOfStock;
            this.predictedOutDate = predictedOutDate;
            this.modelName = modelName;
        }
    }
      /**
     * Dự đoán ngày hết hàng cho một sản phẩm cụ thể
     */
    public static PredictResponse predictStockOut(int productId, int currentStock, int horizon) 
            throws IOException {
        HttpURLConnection conn = null;
        
        try {
            URL url = new URL(API_BASE_URL + "/predict");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000); // 10 seconds
            conn.setReadTimeout(30000); // 30 seconds
            
            // Tạo JSON request body
            String jsonBody = String.format(
                "{\"product_id\":%d,\"current_stock\":%d,\"horizon\":%d}", 
                productId, currentStock, horizon
            );
            
            // Gửi request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            int responseCode = conn.getResponseCode();
            
            if (responseCode == 200) {
                // Đọc response
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    
                    StringBuilder responseBuilder = new StringBuilder();
                    String responseLine;
                    while ((responseLine = reader.readLine()) != null) {
                        responseBuilder.append(responseLine.trim());
                    }
                    
                    return parseJsonResponse(responseBuilder.toString());
                }
            } else if (responseCode == 404) {
                throw new IOException("Model not found for product ID: " + productId);
            } else {
                // Đọc error message
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                    
                    StringBuilder responseBuilder = new StringBuilder();
                    String responseLine;
                    while ((responseLine = reader.readLine()) != null) {
                        responseBuilder.append(responseLine.trim());
                    }
                    
                    throw new IOException("API call failed with status: " + responseCode + 
                                        ", body: " + responseBuilder.toString());
                }
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
      /**
     * Parse JSON response từ API mà không sử dụng Jackson
     */
    private static PredictResponse parseJsonResponse(String json) {
        PredictResponse response = new PredictResponse();
        
        try {
            // Cách tiếp cận đơn giản để parse JSON mà không dùng thư viện
            // Có thể triển khai parser phức tạp hơn nếu cần
            
            // Đảm bảo JSON không rỗng
            if (json == null || json.trim().isEmpty()) {
                System.err.println("Received empty JSON response");
                return response;
            }
            
            // Extract productId
            int productIdStart = json.indexOf("\"product_id\":") + 13;
            if (productIdStart < 13) {
                System.err.println("Product ID not found in JSON");
                return response;
            }
            int productIdEnd = json.indexOf(",", productIdStart);
            if (productIdEnd < 0) {
                productIdEnd = json.indexOf("}", productIdStart);
            }
            if (productIdEnd > productIdStart) {
                String productIdStr = json.substring(productIdStart, productIdEnd).trim();
                try {
                    response.productId = Integer.parseInt(productIdStr);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing product_id: " + productIdStr);
                }
            }
            
            // Extract horizon
            int horizonStart = json.indexOf("\"horizon\":") + 10;
            if (horizonStart >= 10) {
                int horizonEnd = json.indexOf(",", horizonStart);
                if (horizonEnd < 0) {
                    horizonEnd = json.indexOf("}", horizonStart);
                }
                if (horizonEnd > horizonStart) {
                    String horizonStr = json.substring(horizonStart, horizonEnd).trim();
                    try {
                        response.horizon = Integer.parseInt(horizonStr);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing horizon: " + horizonStr);
                    }
                }
            }
            
            // Extract predictedOutDate (có thể null)
            int outDateStart = json.indexOf("\"predicted_out_date\":") + 20;
            if (outDateStart >= 20) {
                int outDateEndComma = json.indexOf(",", outDateStart);
                int outDateEndBrace = json.indexOf("}", outDateStart);
                
                // Chọn dấu kết thúc gần nhất
                int outDateEnd;
                if (outDateEndComma < 0) {
                    outDateEnd = outDateEndBrace;
                } else if (outDateEndBrace < 0) {
                    outDateEnd = outDateEndComma;
                } else {
                    outDateEnd = Math.min(outDateEndComma, outDateEndBrace);
                }                  if (outDateEnd > outDateStart) {
                    String outDateValue = json.substring(outDateStart, outDateEnd).trim();
                    // Kiểm tra giá trị null
                    if (outDateValue.equals("null") || outDateValue.equals("\"null\"") || 
                        outDateValue.contains(":null")) {
                        response.predictedOutDate = null;
                    } else {
                        // Loại bỏ dấu ngoặc kép nếu có
                        outDateValue = outDateValue.replaceAll("\"", "").trim();
                        // Validate if date is in proper format
                        if (!outDateValue.isEmpty()) {
                            if (outDateValue.startsWith(":")) {
                                outDateValue = outDateValue.substring(1).trim();
                            }
                            try {
                                // Try to parse the date to validate format
                                if (!outDateValue.equals("null")) {
                                    LocalDate.parse(outDateValue);
                                    response.predictedOutDate = outDateValue;
                                } else {
                                    response.predictedOutDate = null;
                                }
                            } catch (Exception e) {
                                System.err.println("Invalid date format: " + outDateValue);
                                response.predictedOutDate = null;
                            }
                        } else {
                            response.predictedOutDate = null;
                        }
                    }
                }
            }
              // Extract modelName
            int modelNameStart = json.indexOf("\"model_name\":") + 13;
            if (modelNameStart >= 13) {
                int nextComma = json.indexOf(",", modelNameStart);
                int nextBrace = json.indexOf("}", modelNameStart);
                
                int modelNameEnd;
                if (nextComma < 0) {
                    modelNameEnd = nextBrace;
                } else if (nextBrace < 0) {
                    modelNameEnd = nextComma;
                } else {
                    modelNameEnd = Math.min(nextComma, nextBrace);
                }
                  if (modelNameEnd > modelNameStart) {
                    String modelNameValue = json.substring(modelNameStart, modelNameEnd).trim();
                    // Loại bỏ dấu ngoặc kép
                    modelNameValue = modelNameValue.replaceAll("\"", "").trim();
                    if (modelNameValue.startsWith(":")) {
                        modelNameValue = modelNameValue.substring(1).trim();
                    }
                    if (!modelNameValue.isEmpty() && !modelNameValue.equals("null")) {
                        response.modelName = modelNameValue;
                    } else {
                        response.modelName = "unknown";
                    }
                } else {
                    response.modelName = "unknown";
                }
            } else {
                response.modelName = "unknown";
            }
            
            // Không cố gắng parse dailyForecast từ JSON vì phức tạp
            // và trong trường hợp này chúng ta không cần sử dụng dữ liệu dự báo hàng ngày chi tiết
            
            return response;
        } catch (Exception e) {
            System.err.println("Lỗi khi parse JSON: " + e.getMessage());
            System.err.println("JSON: " + json);
            e.printStackTrace();
            return response; // Trả về đối tượng mặc định
        }
    }
    
    /**
     * Kiểm tra xem API có hoạt động không
     */
    public static boolean isAPIAvailable() {
        try {
            URL url = new URL(API_BASE_URL + "/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); // 5 seconds
            conn.setReadTimeout(5000); // 5 seconds
            
            int responseCode = conn.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            System.err.println("API không khả dụng: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy danh sách sản phẩm sắp hết hàng (sử dụng API prediction)
     * @param products Danh sách sản phẩm với thông tin id, name, currentStock
     * @param daysThreshold Ngưỡng số ngày để coi là "sắp hết hàng"
     * @return Danh sách sản phẩm sắp hết hàng
     */    public static List<LowStockPrediction> getLowStockPredictions(List<Map<String, Object>> products, 
                                                                  int daysThreshold) {
        List<LowStockPrediction> results = new ArrayList<>();
        
        for (Map<String, Object> product : products) {
            try {
                int productId = (Integer) product.get("product_id");
                String productName = (String) product.get("product_name");
                int currentStock = (Integer) product.get("quantity");
                
                PredictResponse prediction;
                try {
                    // Gọi API để dự đoán
                    prediction = predictStockOut(productId, currentStock, 90);
                } catch (IOException e) {
                    if (e.getMessage() != null && e.getMessage().contains("Model not found for product ID")) {
                        System.out.println("Không tìm thấy model cho sản phẩm " + productId + ", sử dụng rule-based forecast");
                        // Sử dụng dự đoán đơn giản khi không có model
                        prediction = predictStockOutWithRuleBased(productId, productName, currentStock);
                    } else {
                        System.err.println("Lỗi khi gọi API dự báo cho sản phẩm " + productId + ": " + e.getMessage());
                        // Sử dụng dự đoán đơn giản cho các trường hợp lỗi khác
                        prediction = predictStockOutWithRuleBased(productId, productName, currentStock);
                        continue;
                    }
                }
                  
                // Kiểm tra và xử lý ngày dự kiến hết hàng
                if (prediction.predictedOutDate != null) {
                    try {
                        LocalDate outDate = LocalDate.parse(prediction.predictedOutDate);
                        LocalDate today = LocalDate.now();
                        int daysUntilOut = (int) today.until(outDate).getDays();
                        
                        // Tăng số ngày lên 30 để hiển thị thêm sản phẩm
                        if (daysUntilOut <= Math.max(daysThreshold * 4, 30) && daysUntilOut > 0) {
                            results.add(new LowStockPrediction(
                                productId, 
                                productName, 
                                currentStock, 
                                daysUntilOut,
                                prediction.predictedOutDate,
                                prediction.modelName != null ? prediction.modelName : "unknown"
                            ));
                        }
                    } catch (Exception e) {
                        System.err.println("Lỗi khi parse ngày hết hàng cho sản phẩm " + productId + 
                            ": " + prediction.predictedOutDate + " - " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi dự đoán cho sản phẩm " + 
                                 product.get("product_id") + ": " + e.getMessage());
            }
        }
        
        // Sắp xếp theo số ngày sắp hết hàng (tăng dần)
        results.sort((a, b) -> Integer.compare(a.daysUntilOutOfStock, b.daysUntilOutOfStock));
        
        return results;
    }
      /**
     * Chuyển đổi LowStockPrediction thành format của AdDashBoard
     */    public static List<Map<String, Object>> convertToLegacyFormat(List<LowStockPrediction> predictions) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (LowStockPrediction pred : predictions) {
            Map<String, Object> row = new HashMap<>();
            row.put("product_id", pred.productId);
            row.put("product_name", pred.productName);
            row.put("quantity", pred.currentStock);
            row.put("days_left", pred.daysUntilOutOfStock);
            
            // Chuyển đổi ngày từ yyyy-MM-dd sang dd/MM/yyyy
            try {
                LocalDate outDate = LocalDate.parse(pred.predictedOutDate);
                row.put("out_of_stock_date", java.sql.Date.valueOf(outDate));
            } catch (Exception e) {
                row.put("out_of_stock_date", null);
            }
            
            results.add(row);
        }
        
        return results;
    }
    
    /**
     * Tạo dữ liệu mẫu cho thử nghiệm khi API bị lỗi
     * @param count Số lượng sản phẩm mẫu cần tạo
     * @return Danh sách dự đoán sản phẩm sắp hết hàng
     */
    public static List<LowStockPrediction> generateSamplePredictions(int count) {
        List<LowStockPrediction> predictions = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        String[] productNames = {
            "Laptop Dell XPS 13", "MacBook Pro 16", "iPhone 14 Pro", "Samsung Galaxy S23", 
            "Bàn phím cơ Logitech", "Chuột không dây Logitech", "Màn hình Dell 27 inch", 
            "Tai nghe Sony WH-1000XM4", "iPad Pro 12.9 inch", "Apple Watch Series 8",
            "Đồ chơi LEGO", "Sách giáo khoa", "Balo laptop", "Đồng hồ Casio", 
            "Giày Nike Air Max", "Kem đánh răng Colgate", "Dầu gội đầu", "Nước rửa tay",
            "Pin sạc dự phòng", "Cáp sạc Type-C"
        };
        
        String[] modelTypes = {
            "arima011", "sarima", "prophet", "holtwin", "Rule-based"
        };
        
        for (int i = 1; i <= count; i++) {
            int productId = i + 100; // Tránh trùng với ID sản phẩm thật
            String productName = productNames[i % productNames.length];
            int stock = 10 + (i % 50); // Tồn kho từ 10 đến 60
            
            // Tạo dự đoán ngày hết hàng
            int days = 3 + (i % 45); // 3-47 ngày
            LocalDate outDate = today.plusDays(days);
            
            // Chọn loại mô hình
            String modelName = modelTypes[i % modelTypes.length];
            if (modelName.equals("Rule-based")) {
                modelName += " (" + productName + ")";
            }
            
            predictions.add(new LowStockPrediction(
                productId, productName, stock, days, outDate.toString(), modelName
            ));
        }
        
        // Sắp xếp theo số ngày sắp hết hàng
        predictions.sort((a, b) -> Integer.compare(a.daysUntilOutOfStock, b.daysUntilOutOfStock));
        
        return predictions;
    }
    
    /**
     * Dự đoán hết hàng dựa trên mô hình đơn giản khi không có model AI
     * Công thức đơn giản: Giả định tiêu thụ 1 đơn vị/ngày
     */    private static PredictResponse predictStockOutWithRuleBased(int productId, String productName, int currentStock) {
        PredictResponse response = new PredictResponse();
        response.productId = productId;
        response.horizon = 90;
        response.modelName = "Rule-based (" + productName + ")";
        
        // Biến thiên dự đoán dựa trên ID để tạo dữ liệu đa dạng
        double dailyRate = 1.0;
        if (productId % 5 == 0) {
            dailyRate = 0.5; // Sản phẩm bán chậm
        } else if (productId % 3 == 0) {
            dailyRate = 1.5; // Sản phẩm bán nhanh
        } else if (productId % 7 == 0) {
            dailyRate = 2.0; // Sản phẩm bán rất nhanh
        }
        
        // Tính ngày hết hàng đơn giản - dựa vào daily rate
        LocalDate today = LocalDate.now();
        int daysUntilOutOfStock = (int) Math.ceil(currentStock / dailyRate);
        LocalDate predictedDate = today.plusDays(daysUntilOutOfStock);
        response.predictedOutDate = predictedDate.toString();
        
        // Tạo dự báo đơn giản - bán theo daily rate
        Map<String, Double> forecast = new HashMap<>();
        for (int i = 1; i <= 90; i++) {
            LocalDate forecastDay = today.plusDays(i);
            forecast.put(forecastDay.toString(), dailyRate);
        }
        response.dailyForecast = forecast;
        
        return response;
    }
}
