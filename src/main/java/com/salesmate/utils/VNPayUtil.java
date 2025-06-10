package com.salesmate.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class VNPayUtil {
    
    /**
     * Tạo chữ ký HMAC-SHA512 chuẩn VNPay
     */
    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException("Key và data không được null");
            }
            
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            System.err.println("Lỗi tạo HMAC-SHA512: " + ex.getMessage());
            ex.printStackTrace();
            return "";
        }
    }
    
    /**
     * Lấy IP address mặc định
     */
    public static String getIpAddress() {
        return "127.0.0.1";
    }
    
    /**
     * Tạo số ngẫu nhiên
     */
    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    /**
     * Tạo URL thanh toán VNPay - VERSION NGẮN GỌN VÀ ĐÚNG CHUẨN
     * Điểm quan trọng: hashData KHÔNG encode, query CÓ encode
     */
    public static String createPaymentUrl(String orderId, long amount, String orderInfo) throws Exception {
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", VNPayConfig.getMerchantId());
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
        vnp_Params.put("vnp_CurrCode", VNPayConfig.getCurrencyCode());
        vnp_Params.put("vnp_TxnRef", orderId);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", VNPayConfig.getOrderType());
        vnp_Params.put("vnp_Locale", VNPayConfig.getLocale());
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", getIpAddress());

        // Thời gian tạo & hết hạn
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));
        cld.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        /* ------------ Build hashData & query ---------------- */
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();

        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);

            // hashData: KHÔNG encode - để băm chữ ký
            hashData.append(fieldName).append('=').append(fieldValue);

            // query: CÓ encode UTF-8 - để tạo URL hợp lệ
            query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()))
                 .append('=')
                 .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));

            if (itr.hasNext()) {
                hashData.append('&');
                query.append('&');
            }
        }

        String vnp_SecureHash = hmacSHA512(VNPayConfig.getSecretKey(), hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        String paymentUrl = VNPayConfig.getApiUrl() + "?" + query.toString();
        
        // Debug output
        System.out.println("VNPay Hash Data (raw): " + hashData.toString());
        System.out.println("VNPay Secure Hash: " + vnp_SecureHash);
        System.out.println("VNPay Payment URL: " + paymentUrl);
        
        return paymentUrl;
    }

    /**
     * Tạo hash cho tất cả fields (dùng cho validation) - RAW DATA
     */
    public static String hashAllFields(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        
        StringBuilder sb = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            
            if (fieldValue != null && fieldValue.length() > 0) {
                sb.append(fieldName).append("=").append(fieldValue);
                
                if (itr.hasNext()) {
                    sb.append("&");
                }
            }
        }
        
        String hashString = sb.toString();
        System.out.println("Hash All Fields - Raw data: " + hashString);
        return hmacSHA512(VNPayConfig.getSecretKey(), hashString);
    }
    
    /**
     * Validate chữ ký VNPay response
     */
    public static boolean validateSignature(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            System.err.println("Params null hoặc rỗng");
            return false;
        }
        
        String vnp_SecureHash = params.get("vnp_SecureHash");
        if (vnp_SecureHash == null || vnp_SecureHash.isEmpty()) {
            System.err.println("Thiếu vnp_SecureHash trong response");
            return false;
        }
        
        // Tạo bản sao để không thay đổi Map gốc
        Map<String, String> sortedParams = new HashMap<>(params);
        
        // Remove các field liên quan đến hash
        sortedParams.remove("vnp_SecureHashType");
        sortedParams.remove("vnp_SecureHash");
        
        // Tính chữ ký từ các params còn lại
        String calculatedHash = hashAllFields(sortedParams);
        
        // So sánh
        boolean isValid = calculatedHash.equals(vnp_SecureHash);
        
        System.out.println("=== VALIDATE SIGNATURE ===");
        System.out.println("Received hash: " + vnp_SecureHash);
        System.out.println("Calculated hash: " + calculatedHash);
        System.out.println("Is valid: " + isValid);
        System.out.println("===========================");
        
        return isValid;
    }
    
    /**
     * Parse query string thành Map parameters
     */
    public static Map<String, String> getVNPayResponseParameters(String queryString) {
        Map<String, String> params = new HashMap<>();
        
        if (queryString == null || queryString.isEmpty()) {
            return params;
        }
        
        try {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    String key = java.net.URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.toString());
                    String value = java.net.URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.toString());
                    params.put(key, value);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi parse VNPay response parameters: " + e.getMessage());
        }
        
        return params;
    }
    
    /**
     * Xử lý kết quả thanh toán từ VNPay
     */
    public static PaymentResult processPaymentResult(Map<String, String> vnpayData) {
        PaymentResult result = new PaymentResult();
        
        try {
            result.setOrderId(vnpayData.get("vnp_TxnRef"));
            result.setTransactionId(vnpayData.get("vnp_TransactionNo"));
            
            String amountStr = vnpayData.get("vnp_Amount");
            if (amountStr != null && !amountStr.isEmpty()) {
                try {
                    result.setAmount(Long.parseLong(amountStr) / 100); // Convert from cents
                } catch (NumberFormatException e) {
                    System.err.println("Lỗi parse amount: " + amountStr);
                    result.setAmount(0);
                }
            }
            
            result.setResponseCode(vnpayData.get("vnp_ResponseCode"));
            result.setTransactionStatus(vnpayData.get("vnp_TransactionStatus"));
            result.setPayDate(vnpayData.get("vnp_PayDate"));
            result.setBankCode(vnpayData.get("vnp_BankCode"));
            result.setCardType(vnpayData.get("vnp_CardType"));
            
            // Kiểm tra trạng thái giao dịch
            String responseCode = vnpayData.get("vnp_ResponseCode");
            String transactionStatus = vnpayData.get("vnp_TransactionStatus");
            
            if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
                result.setSuccess(true);
                result.setMessage("Giao dịch thành công");
            } else {
                result.setSuccess(false);
                result.setMessage(getErrorMessage(responseCode));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("Lỗi xử lý kết quả thanh toán: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Hiển thị thông báo lỗi
     */
    public static void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, message, "Lỗi VNPay", JOptionPane.ERROR_MESSAGE);
        });
    }
    
    /**
     * Hiển thị thông báo thành công
     */
    public static void showSuccess(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    /**
     * Lấy thông báo lỗi từ response code
     */
    private static String getErrorMessage(String responseCode) {
        if (responseCode == null) return "Mã lỗi không xác định";
        
        switch (responseCode) {
            case "00": return "Giao dịch thành công";
            case "07": return "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).";
            case "09": return "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.";
            case "10": return "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần";
            case "11": return "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.";
            case "12": return "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.";
            case "13": return "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.";
            case "24": return "Giao dịch không thành công do: Khách hàng hủy giao dịch";
            case "51": return "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.";
            case "65": return "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.";
            case "75": return "Ngân hàng thanh toán đang bảo trì.";
            case "79": return "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch";
            case "99": return "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)";
            default: return "Giao dịch không thành công. Mã lỗi: " + responseCode;
        }
    }
    
    /**
     * Verify payment (alias cho validateSignature)
     */
    public static boolean verifyPayment(Map<String, String> params) {
        return validateSignature(params);
    }
}
