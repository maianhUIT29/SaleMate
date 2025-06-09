package com.salesmate.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.salesmate.model.Product;

public class ExcelImporter {
    
    public static List<Object[]> importFromExcel(File file) throws IOException {
        List<Object[]> data = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            boolean isFirstRow = true;
            
            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue; // Skip header row
                }
                
                Object[] rowData = new Object[row.getLastCellNum()];
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i);
                    rowData[i] = getCellValue(cell);
                }
                data.add(rowData);
            }
        }
        
        return data;
    }
    
    public static List<Object[]> importFromExcel(File file, List<Integer> selectedColumns) throws IOException {
        List<Object[]> data = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            boolean isFirstRow = true;
            
            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue; // Skip header row
                }
                
                Object[] rowData = new Object[selectedColumns.size()];
                for (int i = 0; i < selectedColumns.size(); i++) {
                    int columnIndex = selectedColumns.get(i);
                    Cell cell = row.getCell(columnIndex);
                    rowData[i] = getCellValue(cell);
                }
                data.add(rowData);
            }
        }
        
        return data;
    }
    
    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
                
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                // Check if the number is actually an integer
                double numericValue = cell.getNumericCellValue();
                if (numericValue == Math.floor(numericValue)) {
                    return (int) numericValue;
                }
                return new BigDecimal(String.valueOf(numericValue));
                
            case BOOLEAN:
                return cell.getBooleanCellValue();
                
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return cell.getNumericCellValue();
                    } catch (Exception ex) {
                        return null;
                    }
                }
                
            default:
                return null;
        }
    }
    
    public static boolean validateExcelFile(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() < 2) { // At least header + 1 data row
                return false;
            }
            
            Row headerRow = sheet.getRow(0);
            if (headerRow == null || headerRow.getLastCellNum() < 1) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static String[] getColumnHeaders(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            
            if (headerRow == null) {
                return new String[0];
            }
            
            String[] headers = new String[headerRow.getLastCellNum()];
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                headers[i] = cell != null ? cell.getStringCellValue() : "Column " + (i + 1);
            }
            
            return headers;
        }
    }
    
    public static List<Product> importProducts(File file) throws IOException {
        List<Product> products = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean firstRow = true;
            for (Row row : sheet) {
                if (firstRow) { firstRow = false; continue; } // Bỏ qua header
                Product p = new Product();
                // Đảm bảo kiểm tra null cho từng cell
                p.setProductName(getCellString(row.getCell(0)));
                p.setPrice(getCellBigDecimal(row.getCell(1)));
                p.setQuantity(getCellInt(row.getCell(2)));
                p.setBarcode(getCellString(row.getCell(3)));
                p.setCategory(getCellString(row.getCell(4)));
                // ...set các trường khác nếu cần
                products.add(p);
            }
        }
        return products;
    }

    // Helper methods for safe cell extraction
    private static String getCellString(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf(cell.getNumericCellValue());
        return null;
    }

    private static BigDecimal getCellBigDecimal(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) return BigDecimal.valueOf(cell.getNumericCellValue());
        if (cell.getCellType() == CellType.STRING) {
            try {
                return new BigDecimal(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private static int getCellInt(Cell cell) {
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.NUMERIC) return (int) cell.getNumericCellValue();
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}