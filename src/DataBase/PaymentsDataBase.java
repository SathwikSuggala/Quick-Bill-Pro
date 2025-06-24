package DataBase;

import models.CreditPaymentView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentsDataBase {

    public List<CreditPaymentView> getCreditPayments(String outletName, LocalDate startDate, LocalDate endDate, int limit, int offset) throws SQLException {
        List<CreditPaymentView> payments = new ArrayList<>();
        
        StringBuilder queryBuilder = new StringBuilder(
            "SELECT cp.payment_id, o.name AS outlet_name, oc.bill_id, cp.payment_amount, cp.payment_mode, cp.payment_date " +
            "FROM credit_payments cp " +
            "JOIN outlet_credits oc ON cp.credit_id = oc.credit_id " +
            "JOIN outlets o ON oc.outlet_id = o.outlet_id "
        );

        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;

        if (outletName != null && !outletName.isEmpty()) {
            queryBuilder.append("WHERE o.name = ? ");
            params.add(outletName);
            hasWhere = true;
        }

        if (startDate != null && endDate != null) {
            queryBuilder.append(hasWhere ? "AND " : "WHERE ");
            queryBuilder.append("cp.payment_date BETWEEN ? AND ? ");
            params.add(startDate.toString());
            params.add(endDate.toString());
        }

        queryBuilder.append("ORDER BY cp.payment_date DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(new CreditPaymentView(
                        rs.getInt("payment_id"),
                        rs.getString("outlet_name"),
                        rs.getInt("bill_id"),
                        rs.getDouble("payment_amount"),
                        rs.getString("payment_mode"),
                        rs.getString("payment_date")
                    ));
                }
            }
        }
        return payments;
    }
} 