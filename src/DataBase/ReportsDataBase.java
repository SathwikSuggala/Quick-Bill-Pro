package DataBase;

import models.ReportItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportsDataBase {

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
        }
        return salesReport;
    }
} 