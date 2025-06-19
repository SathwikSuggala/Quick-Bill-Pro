package controllers;

import DataBase.DashboardDataBase;
import DataBase.DBConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import models.DashboardStats;
import utils.ChartGenerator;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;

public class DashboardController implements Initializable {
    @FXML private ComboBox<String> periodComboBox;
    @FXML private Label totalSalesLabel;
    @FXML private Label totalPurchasesLabel;
    @FXML private Label totalProfitLabel;
    @FXML private Label totalCreditsLabel;
    @FXML private Label totalPaymentsLabel;
    @FXML private Label pendingCreditsLabel;
    @FXML private Label totalCGSTLabel;
    @FXML private Label totalSGSTLabel;
    @FXML private Label totalProductsLabel;
    @FXML private Label lowStockProductsLabel;
    @FXML private Label totalOutletsLabel;

    private final DashboardDataBase dashboardDataBase = new DashboardDataBase();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing DashboardController...");
        setupPeriodComboBox();
        Platform.runLater(this::loadDashboardData);
    }

    private void setupPeriodComboBox() {
        System.out.println("Setting up period combo box...");
        periodComboBox.getItems().addAll("All Time", "This Month");
        periodComboBox.setValue("All Time");
        
        periodComboBox.setOnAction(event -> Platform.runLater(this::loadDashboardData));
    }

    private void loadDashboardData() {
        System.out.println("Loading dashboard data...");
        try {
            DashboardStats stats;
            if ("This Month".equals(periodComboBox.getValue())) {
                stats = dashboardDataBase.getMonthlyStats();
            } else {
                stats = dashboardDataBase.getDashboardStats();
            }
            
            Platform.runLater(() -> updateLabels(stats));
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Show error message to user
        }
    }

    private void updateLabels(DashboardStats stats) {
        System.out.println("Updating labels with stats...");
        
        if (totalSalesLabel != null) {
            totalSalesLabel.setText(String.format("₹%.2f", stats.getTotalSales()));
            System.out.println("Updated totalSalesLabel: " + totalSalesLabel.getText());
        } else {
            System.out.println("totalSalesLabel is null");
        }
        
        if (totalPurchasesLabel != null) {
            totalPurchasesLabel.setText(String.format("₹%.2f", stats.getTotalPurchases()));
            System.out.println("Updated totalPurchasesLabel: " + totalPurchasesLabel.getText());
        } else {
            System.out.println("totalPurchasesLabel is null");
        }
        
        if (totalProfitLabel != null) {
            totalProfitLabel.setText(String.format("₹%.2f", stats.getTotalProfit()));
            System.out.println("Updated totalProfitLabel: " + totalProfitLabel.getText());
        } else {
            System.out.println("totalProfitLabel is null");
        }
        
        if (totalCreditsLabel != null) {
            totalCreditsLabel.setText(String.format("₹%.2f", stats.getTotalCredits()));
            System.out.println("Updated totalCreditsLabel: " + totalCreditsLabel.getText());
        } else {
            System.out.println("totalCreditsLabel is null");
        }
        
        if (totalPaymentsLabel != null) {
            totalPaymentsLabel.setText(String.format("₹%.2f", stats.getTotalPayments()));
            System.out.println("Updated totalPaymentsLabel: " + totalPaymentsLabel.getText());
        } else {
            System.out.println("totalPaymentsLabel is null");
        }
        
        if (pendingCreditsLabel != null) {
            pendingCreditsLabel.setText(String.valueOf(stats.getPendingCredits()));
            System.out.println("Updated pendingCreditsLabel: " + pendingCreditsLabel.getText());
        } else {
            System.out.println("pendingCreditsLabel is null");
        }
        
        if (totalCGSTLabel != null) {
            totalCGSTLabel.setText(String.format("₹%.2f", stats.getTotalCGST()));
            System.out.println("Updated totalCGSTLabel: " + totalCGSTLabel.getText());
        } else {
            System.out.println("totalCGSTLabel is null");
        }
        
        if (totalSGSTLabel != null) {
            totalSGSTLabel.setText(String.format("₹%.2f", stats.getTotalSGST()));
            System.out.println("Updated totalSGSTLabel: " + totalSGSTLabel.getText());
        } else {
            System.out.println("totalSGSTLabel is null");
        }
        
        if (totalProductsLabel != null) {
            totalProductsLabel.setText(String.valueOf(stats.getTotalProducts()));
            System.out.println("Updated totalProductsLabel: " + totalProductsLabel.getText());
        } else {
            System.out.println("totalProductsLabel is null");
        }
        
        if (lowStockProductsLabel != null) {
            lowStockProductsLabel.setText(String.valueOf(stats.getLowStockProducts()));
            System.out.println("Updated lowStockProductsLabel: " + lowStockProductsLabel.getText());
        } else {
            System.out.println("lowStockProductsLabel is null");
        }
        
        if (totalOutletsLabel != null) {
            totalOutletsLabel.setText(String.valueOf(stats.getTotalOutlets()));
            System.out.println("Updated totalOutletsLabel: " + totalOutletsLabel.getText());
        } else {
            System.out.println("totalOutletsLabel is null");
        }
    }

    private void createSalesChart() {
        try {
            Map<String, Double> salesData = new HashMap<>();
            // Get sales data for the last 6 months
            String query = "SELECT strftime('%Y-%m', bill_date) as month, " +
                         "SUM(total_amount) as total " +
                         "FROM bills " +
                         "WHERE bill_date >= date('now', '-6 months') " +
                         "GROUP BY month " +
                         "ORDER BY month";
            
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    salesData.put(rs.getString("month"), rs.getDouble("total"));
                }
            }
            
            JPanel chart = ChartGenerator.createBarChart(
                "Sales Over Last 6 Months",
                "Month",
                "Total Sales (₹)",
                salesData
            );
            ChartGenerator.displayChart(chart);
            
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Show error message to user
        }
    }

    @FXML
    private void showSalesChart() {
        Platform.runLater(this::createSalesChart);
    }
} 