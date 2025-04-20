package com.salesmate.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JTable;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelExporter {
    
    public static void exportToExcel(JTable table, File file, boolean includeHeaders) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        // Create header font
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        
        // Create header cell style
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        int rowNum = 0;
        
        // Write headers if requested
        if (includeHeaders) {
            Row headerRow = sheet.createRow(rowNum++);
            for (int col = 0; col < table.getColumnCount(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(table.getColumnName(col));
                cell.setCellStyle(headerStyle);
            }
        }

        // Write data
        for (int row = 0; row < table.getRowCount(); row++) {
            Row currentRow = sheet.createRow(rowNum++);
            for (int col = 0; col < table.getColumnCount(); col++) {
                Cell cell = currentRow.createCell(col);
                Object value = table.getValueAt(row, col);
                if (value != null) {
                    cell.setCellValue(value.toString());
                }
            }
        }

        // Autosize columns
        for (int col = 0; col < table.getColumnCount(); col++) {
            sheet.autoSizeColumn(col);
        }

        // Write to file
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            workbook.write(outputStream);
        }
        workbook.close();
    }

    public static void exportToCSV(JTable table, File file, boolean includeHeaders) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            // Write headers if requested
            if (includeHeaders) {
                for (int col = 0; col < table.getColumnCount(); col++) {
                    pw.print(table.getColumnName(col));
                    if (col < table.getColumnCount() - 1) {
                        pw.print(",");
                    }
                }
                pw.println();
            }

            // Write data
            for (int row = 0; row < table.getRowCount(); row++) {
                for (int col = 0; col < table.getColumnCount(); col++) {
                    Object value = table.getValueAt(row, col);
                    if (value != null) {
                        pw.print(value.toString());
                    }
                    if (col < table.getColumnCount() - 1) {
                        pw.print(",");
                    }
                }
                pw.println();
            }
        }
    }

    public static void openFile(File file) throws IOException {
        Desktop.getDesktop().open(file);
    }
}
