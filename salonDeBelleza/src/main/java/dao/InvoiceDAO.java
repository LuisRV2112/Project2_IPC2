package dao;

import models.Invoice;
import utils.DatabaseConnection;
import java.sql.*;

public class InvoiceDAO {

    public static boolean generateInvoice(int appointmentId) {
        String sql = "INSERT INTO invoices (appointment_id, total) " +
                    "SELECT ?, s.price FROM appointments a " +
                    "JOIN services s ON a.service_id = s.id " +
                    "WHERE a.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, appointmentId);
            stmt.setInt(2, appointmentId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Invoice getInvoiceForAppointment(int appointmentId) {
        String sql = "SELECT * FROM invoices WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, appointmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Invoice invoice = new Invoice();
                    invoice.setId(rs.getInt("id"));
                    invoice.setAppointmentId(appointmentId);
                    invoice.setTotal(rs.getDouble("total"));
                    invoice.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
                    invoice.setPaymentStatus(rs.getString("payment_status"));
                    return invoice;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}