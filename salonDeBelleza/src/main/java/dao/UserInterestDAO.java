package dao;

import utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserInterestDAO {

    public static boolean addUserInterest(int userId, String interest) {
        String sql = "INSERT INTO user_interests (user_id, interest) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, interest.toLowerCase());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeUserInterest(int userId, String interest) {
        String sql = "DELETE FROM user_interests WHERE user_id = ? AND interest = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, interest.toLowerCase());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> getUserInterests(int userId) {
        List<String> interests = new ArrayList<>();
        String sql = "SELECT interest FROM user_interests WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    interests.add(rs.getString("interest"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return interests;
    }

    public static boolean hasInterest(int userId, String interest) {
        String sql = "SELECT 1 FROM user_interests WHERE user_id = ? AND interest = ? LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, interest.toLowerCase());
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<String> getCommonInterests() {
        List<String> interests = new ArrayList<>();
        String sql = "SELECT interest, COUNT(*) as count FROM user_interests GROUP BY interest ORDER BY count DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                interests.add(rs.getString("interest"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return interests;
    }
}