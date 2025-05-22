package dao;

import utils.DatabaseConnection;
import java.sql.*;

public class AdImpressionDAO {
    public static boolean recordImpression(int adId, String pageUrl) {
        String sql = "INSERT INTO ad_impressions (ad_id, page_url) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, adId);
            stmt.setString(2, pageUrl);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean recordImpression(int adId, String pageUrl, int userId) {
        String sql = "INSERT INTO ad_impressions (ad_id, page_url, user_id) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, adId);
            stmt.setString(2, pageUrl);
            stmt.setInt(3, userId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}