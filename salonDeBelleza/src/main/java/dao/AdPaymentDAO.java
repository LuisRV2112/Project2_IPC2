package dao;

import utils.DatabaseConnection;
import java.sql.*;


public class AdPaymentDAO {
    public static boolean recordPayment(int adId, double amount) {
        String sql = "INSERT INTO ad_payments (ad_id, amount) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, adId);
            stmt.setDouble(2, amount);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}