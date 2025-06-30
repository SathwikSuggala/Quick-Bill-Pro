package controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import models.Bill;
import models.BillItem;
import models.Outlet;
import DataBase.BillsDataBase;
import DataBase.OutletsDataBase;
import DataBase.InventoryDBSetup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsController {
    @FXML
    private AnchorPane rootPane;
    // Add settings logic here

    @FXML
    private void onExportExcelClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Bills Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        Window window = rootPane.getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);
        if (file == null) return;

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Bills");
            int rowIdx = 0;
            Row header = sheet.createRow(rowIdx++);
            int col = 0;
            header.createCell(col++).setCellValue("Bill ID");
            header.createCell(col++).setCellValue("Bill Date");
            header.createCell(col++).setCellValue("Outlet Name");
            header.createCell(col++).setCellValue("Outlet Address");
            header.createCell(col++).setCellValue("Outlet GSTIN");
            header.createCell(col++).setCellValue("Total Amount");
            header.createCell(col++).setCellValue("Total CGST");
            header.createCell(col++).setCellValue("Total SGST");
            header.createCell(col++).setCellValue("Payment Type");
            header.createCell(col++).setCellValue("Remarks");
            header.createCell(col++).setCellValue("Product Name(s)");
            header.createCell(col++).setCellValue("Product Quantity(s)");
            header.createCell(col++).setCellValue("Product Price(s)");
            header.createCell(col++).setCellValue("Product HSN(s)");

            List<Bill> bills = BillsDataBase.getAllBills();
            Map<Integer, Outlet> outletMap = new HashMap<>();
            for (Outlet outlet : new OutletsDataBase().getAllOutlets()) {
                outletMap.put(outlet.getId(), outlet);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (Bill bill : bills) {
                Outlet outlet = outletMap.get(bill.getOutletId());
                String outletName = outlet != null ? outlet.getName() : "";
                String outletAddress = outlet != null ? outlet.getAddress() : "";
                String outletGSTIN = outlet != null ? outlet.getGstin() : "";
                // Aggregate product info as comma-separated strings
                StringBuilder productNames = new StringBuilder();
                StringBuilder productQuantities = new StringBuilder();
                StringBuilder productPrices = new StringBuilder();
                StringBuilder productHSNs = new StringBuilder();
                for (BillItem item : bill.getBillItems()) {
                    if (productNames.length() > 0) {
                        productNames.append(", ");
                        productQuantities.append(", ");
                        productPrices.append(", ");
                        productHSNs.append(", ");
                    }
                    productNames.append(item.getProductName());
                    productQuantities.append(item.getQuantity());
                    productPrices.append(item.getPrice());
                    // Format HSN as integer (no decimals, no scientific notation)
                    productHSNs.append(String.valueOf((long)item.getHsn()));
                }
                Row row = sheet.createRow(rowIdx++);
                int c = 0;
                row.createCell(c++).setCellValue(bill.getBillId());
                row.createCell(c++).setCellValue(sdf.format(bill.getBillDate()));
                row.createCell(c++).setCellValue(outletName);
                row.createCell(c++).setCellValue(outletAddress);
                row.createCell(c++).setCellValue(outletGSTIN);
                row.createCell(c++).setCellValue(bill.getTotalAmount());
                row.createCell(c++).setCellValue(bill.getTotalCGST());
                row.createCell(c++).setCellValue(bill.getTotalSGST());
                row.createCell(c++).setCellValue(bill.getPaymentType());
                row.createCell(c++).setCellValue(bill.getRemarks());
                row.createCell(c++).setCellValue(productNames.toString());
                row.createCell(c++).setCellValue(productQuantities.toString());
                row.createCell(c++).setCellValue(productPrices.toString());
                row.createCell(c++).setCellValue(productHSNs.toString());
            }
            for (int i = 0; i < col; i++) sheet.autoSizeColumn(i);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            // Optionally show an alert dialog here
        }
    }

    private void showAlert(String title, String content, javafx.scene.control.Alert.AlertType type) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void onDeleteLogClicked() {
        // Path to the log file in the user's home QuickBillPro directory
        String userHome = System.getProperty("user.home");
        File logFile = new File(userHome + java.io.File.separator + "QuickBillPro" + java.io.File.separator + "QuickBillPro.log");
        if (logFile.exists()) {
            try (java.io.FileWriter fw = new java.io.FileWriter(logFile, false)) {
                fw.write(""); // Clear the file contents
                showAlert("Success", "Log file cleared successfully.", javafx.scene.control.Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Error", "Failed to clear log file: " + e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Info", "Log file does not exist.", javafx.scene.control.Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void onResetDataClicked() {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
            dialog.setTitle("Confirm Data Reset");
            dialog.setHeaderText("Are you sure you want to reset all data? This action cannot be undone.\nEnter the passcode to confirm:");
            dialog.setContentText("Passcode:");
            java.util.Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String passcode = result.get();
                if ("9290872634".equals(passcode)) {
                    try {
                        DataBase.InventoryDBSetup.resetAllDataExceptCore();
                        showAlert("Success", "All data (except inlets, outlets, products, stock, users) has been reset.", javafx.scene.control.Alert.AlertType.INFORMATION);
                    } catch (Exception e) {
                        showAlert("Error", "Failed to reset data: " + e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
                    }
                } else {
                    showAlert("Incorrect Passcode", "The passcode you entered is incorrect. Data reset cancelled.", javafx.scene.control.Alert.AlertType.ERROR);
                }
            } // else: user cancelled, do nothing
        });
    }
} 