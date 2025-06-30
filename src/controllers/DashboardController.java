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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger logger = LogManager.getLogger(DashboardController.class);
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
        logger.info("Initializing DashboardController...");
        setupPeriodComboBox();
        Platform.runLater(this::loadDashboardData);
    }

    private void setupPeriodComboBox() {
        logger.info("Setting up period combo box...");
        periodComboBox.getItems().addAll("All Time", "This Month");
        periodComboBox.setValue("All Time");
        
        periodComboBox.setOnAction(event -> Platform.runLater(this::loadDashboardData));
    }

    private void loadDashboardData() {
        logger.info("Loading dashboard data...");
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
        logger.info("Updating labels with stats...");
        
        if (totalSalesLabel != null) {
            totalSalesLabel.setText(String.format("₹%.2f", stats.getTotalSales()));
            logger.info("Updated totalSalesLabel: " + totalSalesLabel.getText());
        } else {
            logger.info("totalSalesLabel is null");
        }
        
        if (totalPurchasesLabel != null) {
            totalPurchasesLabel.setText(String.format("₹%.2f", stats.getTotalPurchases()));
            logger.info("Updated totalPurchasesLabel: " + totalPurchasesLabel.getText());
        } else {
            logger.info("totalPurchasesLabel is null");
        }
        
        if (totalProfitLabel != null) {
            totalProfitLabel.setText(String.format("₹%.2f", stats.getTotalProfit()));
            logger.info("Updated totalProfitLabel: " + totalProfitLabel.getText());
        } else {
            logger.info("totalProfitLabel is null");
        }
        
        if (totalCreditsLabel != null) {
            totalCreditsLabel.setText(String.format("₹%.2f", stats.getTotalCredits()));
            logger.info("Updated totalCreditsLabel: " + totalCreditsLabel.getText());
        } else {
            logger.info("totalCreditsLabel is null");
        }
        
        if (totalPaymentsLabel != null) {
            totalPaymentsLabel.setText(String.format("₹%.2f", stats.getTotalPayments()));
            logger.info("Updated totalPaymentsLabel: " + totalPaymentsLabel.getText());
        } else {
            logger.info("totalPaymentsLabel is null");
        }
        
        if (pendingCreditsLabel != null) {
            pendingCreditsLabel.setText(String.valueOf(stats.getPendingCredits()));
            logger.info("Updated pendingCreditsLabel: " + pendingCreditsLabel.getText());
        } else {
            logger.info("pendingCreditsLabel is null");
        }
        
        if (totalCGSTLabel != null) {
            totalCGSTLabel.setText(String.format("₹%.2f", stats.getTotalCGST()));
            logger.info("Updated totalCGSTLabel: " + totalCGSTLabel.getText());
        } else {
            logger.info("totalCGSTLabel is null");
        }
        
        if (totalSGSTLabel != null) {
            totalSGSTLabel.setText(String.format("₹%.2f", stats.getTotalSGST()));
            logger.info("Updated totalSGSTLabel: " + totalSGSTLabel.getText());
        } else {
            logger.info("totalSGSTLabel is null");
        }
        
        if (totalProductsLabel != null) {
            totalProductsLabel.setText(String.valueOf(stats.getTotalProducts()));
            logger.info("Updated totalProductsLabel: " + totalProductsLabel.getText());
        } else {
            logger.info("totalProductsLabel is null");
        }
        
        if (lowStockProductsLabel != null) {
            lowStockProductsLabel.setText(String.valueOf(stats.getLowStockProducts()));
            logger.info("Updated lowStockProductsLabel: " + lowStockProductsLabel.getText());
        } else {
            logger.info("lowStockProductsLabel is null");
        }
        
        if (totalOutletsLabel != null) {
            totalOutletsLabel.setText(String.valueOf(stats.getTotalOutlets()));
            logger.info("Updated totalOutletsLabel: " + totalOutletsLabel.getText());
        } else {
            logger.info("totalOutletsLabel is null");
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