package dao;

import models.ClientProfile;
import utils.DatabaseConnection;
import java.sql.*;

public class ClientProfileDAO {

    public static boolean createOrUpdateProfile(ClientProfile profile) {
        String sql = "INSERT INTO client_profiles (user_id, photo_url, description, hobbies, name) " +
                     "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                     "photo_url = VALUES(photo_url), " +
                     "description = VALUES(description), " +
                     "hobbies = VALUES(hobbies), " +
                     "name = VALUES(name)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, profile.getUserId());
            stmt.setString(2, profile.getPhotoUrl());
            stmt.setString(3, profile.getDescription());
            stmt.setString(4, profile.getHobbies());
            stmt.setString(5, profile.getName());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ClientProfile getProfile(int userId) {
        String sql = "SELECT * FROM client_profiles WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ClientProfile profile = new ClientProfile();
                    profile.setUserId(userId);
                    profile.setName(rs.getString("name"));
                    profile.setPhotoUrl(rs.getString("photo_url"));
                    profile.setDescription(rs.getString("description"));
                    profile.setHobbies(rs.getString("hobbies"));
                    profile.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    profile.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return profile;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}