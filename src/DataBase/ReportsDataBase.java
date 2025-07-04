package DataBase;

import models.ReportItem;
import models.BillViewItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReportsDataBase {
    private static final Logger logger = LogManager.getLogger(ReportsDataBase.class);

    public List<ReportItem> getSalesReport(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<ReportItem> salesReport = new ArrayList<>();
        String query = "SELECT " +
                       "b.bill_date, " +
                       "b.bill_id, " +
                       "o.name AS outlet_name, " +
                       "bi.product_id, " +
                       "p.name AS product_name, " +
                       "bi.quantity, " +
                       "bi.price, " +
                       "bi.CGST, " +
                       "bi.SGST " +
                       "FROM bills b " +
                       "JOIN bill_items bi ON b.bill_id = bi.bill_id " +
                       "JOIN outlets o ON b.outlet_id = o.outlet_id " +
                       "JOIN products p ON bi.product_id = p.product_id " +
                       "WHERE b.bill_date BETWEEN ? AND ? " +
                       "ORDER BY b.bill_date DESC, b.bill_id ASC;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, startDate.toString());
            stmt.setString(2, endDate.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate saleDate = LocalDate.parse(rs.getString("bill_date"));
                    int billId = rs.getInt("bill_id");
                    String outletName = rs.getString("outlet_name");
                    String productName = rs.getString("product_name");
                    int quantity = rs.getInt("quantity");
                    double unitPrice = rs.getDouble("price");
                    double cgst = rs.getDouble("CGST");
                    double sgst = rs.getDouble("SGST");

                    // Calculate total amount for the item based on price, quantity, CGST, and SGST
                    double itemSubtotal = quantity * unitPrice;
                    double itemCgstAmount = itemSubtotal * (cgst / 100.0);
                    double itemSgstAmount = itemSubtotal * (sgst / 100.0);
                    double itemTotalAmount = itemSubtotal + itemCgstAmount + itemSgstAmount;

                    ReportItem item = new ReportItem(
                        saleDate,
                        billId,
                        outletName,
                        productName,
                        quantity,
                        unitPrice,
                        cgst,
                        sgst,
                        itemTotalAmount
                    );
                    salesReport.add(item);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching sales report: " + e.getMessage(), e);
        }
        return salesReport;
    }

    public BillViewItem getBillDetails(int billId) throws SQLException {
        String query = "SELECT " +
                      "b.bill_id, " +
                      "b.bill_date, " +
                      "o.name AS outlet_name, " +
                      "o.address AS outlet_address, " +
                      "b.total_amount, " +
                      "b.total_CGST, " +
                      "b.total_SGST " +
                      "FROM bills b " +
                      "JOIN outlets o ON b.outlet_id = o.outlet_id " +
                      "WHERE b.bill_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, billId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new BillViewItem(
                        rs.getInt("bill_id"),
                        LocalDate.parse(rs.getString("bill_date")),
                        rs.getString("outlet_name"),
                        rs.getString("outlet_address"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("total_CGST"),
                        rs.getDouble("total_SGST")
                    );
                }
            }
        }
        return null;
    }

    public List<ReportItem> getBillItems(int billId) throws SQLException {
        List<ReportItem> billItems = new ArrayList<>();
        String query = "SELECT " +
                       "b.bill_date, " +
                       "b.bill_id, " +
                       "o.name AS outlet_name, " +
                       "p.name AS product_name, " +
                       "p.hsn, " +
                       "bi.quantity, " +
                       "bi.price, " +
                       "bi.CGST, " +
                       "bi.SGST " +
                       "FROM bills b " +
                       "JOIN bill_items bi ON b.bill_id = bi.bill_id " +
                       "JOIN outlets o ON b.outlet_id = o.outlet_id " +
                       "JOIN products p ON bi.product_id = p.product_id " +
                       "WHERE b.bill_id = ? " +
                       "ORDER BY bi.bill_item_id ASC;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, billId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate saleDate = LocalDate.parse(rs.getString("bill_date"));
                    int billIdVal = rs.getInt("bill_id");
                    String outletName = rs.getString("outlet_name");
                    String productName = rs.getString("product_name");
                    int quantity = rs.getInt("quantity");
                    double unitPrice = rs.getDouble("price");
                    double cgst = rs.getDouble("CGST");
                    double sgst = rs.getDouble("SGST");
                    double hsn = rs.getDouble("hsn");

                    double itemSubtotal = quantity * unitPrice;
                    double itemCgstAmount = itemSubtotal * (cgst / 100.0);
                    double itemSgstAmount = itemSubtotal * (sgst / 100.0);
                    double itemTotalAmount = itemSubtotal + itemCgstAmount + itemSgstAmount;

                    ReportItem item = new ReportItem(
                        saleDate,
                        billIdVal,
                        outletName,
                        productName,
                        quantity,
                        unitPrice,
                        cgst,
                        sgst,
                        itemTotalAmount
                    );
                    item.setHsn(hsn);
                    billItems.add(item);
                }
            }
        }
        return billItems;
    }

    public List<BillViewItem> getBillsReport(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<BillViewItem> billsReport = new ArrayList<>();
        String query = "SELECT " +
                "b.bill_id, " +
                "b.bill_date, " +
                "o.name AS outlet_name, " +
                "o.address AS outlet_address, " +
                "b.total_amount, " +
                "b.total_CGST, " +
                "b.total_SGST " +
                "FROM bills b " +
                "JOIN outlets o ON b.outlet_id = o.outlet_id " +
                "WHERE b.bill_date BETWEEN ? AND ? " +
                "ORDER BY b.bill_date DESC, b.bill_id ASC;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, startDate.toString());
            stmt.setString(2, endDate.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    billsReport.add(new BillViewItem(
                            rs.getInt("bill_id"),
                            LocalDate.parse(rs.getString("bill_date")),
                            rs.getString("outlet_name"),
                            rs.getString("outlet_address"),
                            rs.getDouble("total_amount"),
                            rs.getDouble("total_CGST"),
                            rs.getDouble("total_SGST")
                    ));
                }
            }
        }
        return billsReport;
    }

    public List<BillViewItem> getBillsReportByOutletAndDate(String outletName, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<BillViewItem> billsReport = new ArrayList<>();
        String query = "SELECT " +
                "b.bill_id, " +
                "b.bill_date, " +
                "o.name AS outlet_name, " +
                "o.address AS outlet_address, " +
                "b.total_amount, " +
                "b.total_CGST, " +
                "b.total_SGST " +
                "FROM bills b " +
                "JOIN outlets o ON b.outlet_id = o.outlet_id " +
                "WHERE o.name = ? AND b.bill_date BETWEEN ? AND ? " +
                "ORDER BY b.bill_date DESC, b.bill_id ASC;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, outletName);
            stmt.setString(2, startDate.toString());
            stmt.setString(3, endDate.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    billsReport.add(new BillViewItem(
                            rs.getInt("bill_id"),
                            LocalDate.parse(rs.getString("bill_date")),
                            rs.getString("outlet_name"),
                            rs.getString("outlet_address"),
                            rs.getDouble("total_amount"),
                            rs.getDouble("total_CGST"),
                            rs.getDouble("total_SGST")
                    ));
                }
            }
        }
        return billsReport;
    }

    public List<BillViewItem> getBillsReportByOutletAndYear(String outletName, int year) throws SQLException {
        List<BillViewItem> billsReport = new ArrayList<>();
        String query = "SELECT " +
                "b.bill_id, " +
                "b.bill_date, " +
                "o.name AS outlet_name, " +
                "o.address AS outlet_address, " +
                "b.total_amount, " +
                "b.total_CGST, " +
                "b.total_SGST " +
                "FROM bills b " +
                "JOIN outlets o ON b.outlet_id = o.outlet_id " +
                "WHERE o.name = ? AND STRFTIME('%Y', b.bill_date) = ? " +
                "ORDER BY b.bill_date DESC, b.bill_id ASC;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, outletName);
            stmt.setString(2, String.valueOf(year));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    billsReport.add(new BillViewItem(
                            rs.getInt("bill_id"),
                            LocalDate.parse(rs.getString("bill_date")),
                            rs.getString("outlet_name"),
                            rs.getString("outlet_address"),
                            rs.getDouble("total_amount"),
                            rs.getDouble("total_CGST"),
                            rs.getDouble("total_SGST")
                    ));
                }
            }
        }
        return billsReport;
    }

    public List<BillViewItem> getBillsReportByOutletAndYearPaginated(String outletName, int year, int limit, int offset) throws SQLException {
        List<BillViewItem> billsReport = new ArrayList<>();
        String query = "SELECT " +
                "b.bill_id, " +
                "b.bill_date, " +
                "o.name AS outlet_name, " +
                "o.address AS outlet_address, " +
                "b.total_amount, " +
                "b.total_CGST, " +
                "b.total_SGST " +
                "FROM bills b " +
                "JOIN outlets o ON b.outlet_id = o.outlet_id " +
                "WHERE o.name = ? AND STRFTIME('%Y', b.bill_date) = ? " +
                "ORDER BY b.bill_date DESC, b.bill_id ASC " +
                "LIMIT ? OFFSET ?;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, outletName);
            stmt.setString(2, String.valueOf(year));
            stmt.setInt(3, limit);
            stmt.setInt(4, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    billsReport.add(new BillViewItem(
                            rs.getInt("bill_id"),
                            LocalDate.parse(rs.getString("bill_date")),
                            rs.getString("outlet_name"),
                            rs.getString("outlet_address"),
                            rs.getDouble("total_amount"),
                            rs.getDouble("total_CGST"),
                            rs.getDouble("total_SGST")
                    ));
                }
            }
        }
        return billsReport;
    }

    public List<BillViewItem> getBillsReportPaginated(LocalDate startDate, LocalDate endDate, int limit, int offset) throws SQLException {
        List<BillViewItem> billsReport = new ArrayList<>();
        String query = "SELECT " +
                "b.bill_id, " +
                "b.bill_date, " +
                "o.name AS outlet_name, " +
                "o.address AS outlet_address, " +
                "b.total_amount, " +
                "b.total_CGST, " +
                "b.total_SGST " +
                "FROM bills b " +
                "JOIN outlets o ON b.outlet_id = o.outlet_id " +
                "WHERE b.bill_date BETWEEN ? AND ? " +
                "ORDER BY b.bill_date DESC, b.bill_id ASC " +
                "LIMIT ? OFFSET ?;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, startDate.toString());
            stmt.setString(2, endDate.toString());
            stmt.setInt(3, limit);
            stmt.setInt(4, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    billsReport.add(new BillViewItem(
                            rs.getInt("bill_id"),
                            LocalDate.parse(rs.getString("bill_date")),
                            rs.getString("outlet_name"),
                            rs.getString("outlet_address"),
                            rs.getDouble("total_amount"),
                            rs.getDouble("total_CGST"),
                            rs.getDouble("total_SGST")
                    ));
                }
            }
        }
        return billsReport;
    }

    public BillViewItem getBillViewItemById(int billId) {
        try {
            return getBillDetails(billId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
} 