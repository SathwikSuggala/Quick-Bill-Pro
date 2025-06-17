package DataBase;

import models.DashboardStats;
import java.sql.*;
import java.time.LocalDate;

public class DashboardDataBase {
    
    public DashboardStats getDashboardStats() throws SQLException {
        System.out.println("Fetching dashboard stats...");
        try (Connection conn = DBConnection.getConnection()) {
            double totalSales = getTotalSales(conn);
            System.out.println("Total Sales: " + totalSales);
            
            double totalPurchases = getTotalPurchases(conn);
            System.out.println("Total Purchases: " + totalPurchases);
            
            double totalCredits = getTotalCredits(conn);
            System.out.println("Total Credits: " + totalCredits);
            
            double totalPayments = getTotalPayments(conn);
            System.out.println("Total Payments: " + totalPayments);
            
            int totalProducts = getTotalProducts(conn);
            System.out.println("Total Products: " + totalProducts);
            
            int totalOutlets = getTotalOutlets(conn);
            System.out.println("Total Outlets: " + totalOutlets);
            
            int totalInlets = getTotalInlets(conn);
            System.out.println("Total Inlets: " + totalInlets);
            
            double totalCGST = getTotalCGST(conn);
            System.out.println("Total CGST: " + totalCGST);
            
            double totalSGST = getTotalSGST(conn);
            System.out.println("Total SGST: " + totalSGST);
            
            int lowStockProducts = getLowStockProducts(conn);
            System.out.println("Low Stock Products: " + lowStockProducts);
            
            int pendingCredits = getPendingCredits(conn);
            System.out.println("Pending Credits: " + pendingCredits);
            
            double totalProfit = calculateProfit(totalSales, totalPurchases);
            System.out.println("Total Profit: " + totalProfit);

            return new DashboardStats(
                totalSales, totalPurchases, totalCredits, totalPayments,
                totalProducts, totalOutlets, totalInlets, totalCGST, totalSGST,
                lowStockProducts, pendingCredits, totalProfit
            );
        }
    }

    private double getTotalSales(Connection conn) throws SQLException {
        String query = "SELECT COALESCE(SUM(total_amount), 0) FROM bills";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            double result = rs.next() ? rs.getDouble(1) : 0;
            System.out.println("Executing query: " + query);
            System.out.println("Result: " + result);
            return result;
        }
    }

    private double getTotalPurchases(Connection conn) throws SQLException {
        String query = "SELECT COALESCE(SUM(quantity * cost_price), 0) FROM purchases";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            double result = rs.next() ? rs.getDouble(1) : 0;
            System.out.println("Executing query: " + query);
            System.out.println("Result: " + result);
            return result;
        }
    }

    private double getTotalCredits(Connection conn) throws SQLException {
        String query = "SELECT COALESCE(SUM(credit_amount), 0) FROM outlet_credits";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            double result = rs.next() ? rs.getDouble(1) : 0;
            System.out.println("Executing query: " + query);
            System.out.println("Result: " + result);
            return result;
        }
    }

    private double getTotalPayments(Connection conn) throws SQLException {
        String query = "SELECT COALESCE(SUM(payment_amount), 0) FROM credit_payments";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            double result = rs.next() ? rs.getDouble(1) : 0;
            System.out.println("Executing query: " + query);
            System.out.println("Result: " + result);
            return result;
        }
    }

    private int getTotalProducts(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM products";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            int result = rs.next() ? rs.getInt(1) : 0;
            System.out.println("Executing query: " + query);
            System.out.println("Result: " + result);
            return result;
        }
    }

    private int getTotalOutlets(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM outlets";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            int result = rs.next() ? rs.getInt(1) : 0;
            System.out.println("Executing query: " + query);
            System.out.println("Result: " + result);
            return result;
        }
    }

    private int getTotalInlets(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM inlets";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            int result = rs.next() ? rs.getInt(1) : 0;
            System.out.println("Executing query: " + query);
            System.out.println("Result: " + result);
            return result;
        }
    }

    private double getTotalCGST(Connection conn) throws SQLException {
        String query = "SELECT COALESCE(SUM(total_CGST), 0) FROM bills";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            double result = rs.next() ? rs.getDouble(1) : 0;
            System.out.println("Executing query: " + query);
            System.out.println("Result: " + result);
            return result;
        }
    }

    private double getTotalSGST(Connection conn) throws SQLException {
        String query = "SELECT COALESCE(SUM(total_SGST), 0) FROM bills";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            double result = rs.next() ? rs.getDouble(1) : 0;
            System.out.println("Executing query: " + query);
            System.out.println("Result: " + result);
            return result;
        }
    }

    private int getLowStockProducts(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM stock WHERE quantity < 10";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            int result = rs.next() ? rs.getInt(1) : 0;
            System.out.println("Executing query: " + query);
            System.out.println("Result: " + result);
            return result;
        }
    }

    private int getPendingCredits(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM outlet_credits WHERE status = 'PENDING'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            int result = rs.next() ? rs.getInt(1) : 0;
            System.out.println("Executing query: " + query);
            System.out.println("Result: " + result);
            return result;
        }
    }

    private double calculateProfit(double totalSales, double totalPurchases) {
        return totalSales - totalPurchases;
    }

    // Get monthly statistics
    public DashboardStats getMonthlyStats() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
            String dateStr = firstDayOfMonth.toString();

            double totalSales = getTotalSalesForPeriod(conn, dateStr);
            double totalPurchases = getTotalPurchasesForPeriod(conn, dateStr);
            double totalCredits = getTotalCreditsForPeriod(conn, dateStr);
            double totalPayments = getTotalPaymentsForPeriod(conn, dateStr);
            double totalCGST = getTotalCGSTForPeriod(conn, dateStr);
            double totalSGST = getTotalSGSTForPeriod(conn, dateStr);
            double totalProfit = calculateProfit(totalSales, totalPurchases);

            return new DashboardStats(
                totalSales, totalPurchases, totalCredits, totalPayments,
                0, 0, 0, totalCGST, totalSGST, 0, 0, totalProfit
            );
        }
    }

    private double getTotalSalesForPeriod(Connection conn, String startDate) throws SQLException {
        String query = "SELECT COALESCE(SUM(total_amount), 0) FROM bills WHERE bill_date >= ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, startDate);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    private double getTotalPurchasesForPeriod(Connection conn, String startDate) throws SQLException {
        String query = "SELECT COALESCE(SUM(quantity * cost_price), 0) FROM purchases WHERE purchase_date >= ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, startDate);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    private double getTotalCreditsForPeriod(Connection conn, String startDate) throws SQLException {
        String query = "SELECT COALESCE(SUM(credit_amount), 0) FROM outlet_credits WHERE credit_date >= ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, startDate);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    private double getTotalPaymentsForPeriod(Connection conn, String startDate) throws SQLException {
        String query = "SELECT COALESCE(SUM(payment_amount), 0) FROM credit_payments WHERE payment_date >= ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, startDate);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    private double getTotalCGSTForPeriod(Connection conn, String startDate) throws SQLException {
        String query = "SELECT COALESCE(SUM(total_CGST), 0) FROM bills WHERE bill_date >= ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, startDate);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    private double getTotalSGSTForPeriod(Connection conn, String startDate) throws SQLException {
        String query = "SELECT COALESCE(SUM(total_SGST), 0) FROM bills WHERE bill_date >= ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, startDate);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }
} 