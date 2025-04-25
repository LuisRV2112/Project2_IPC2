package dao;

import models.Ad;
import utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdDAO {
    public static boolean createAd(Ad ad) {
        String sql = "INSERT INTO advertisements (type, content_url, start_date, end_date, category, price_per_day, total_cost) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ad.getType());
            stmt.setString(2, ad.getContentUrl());
            stmt.setDate(3, Date.valueOf(ad.getStartDate()));
            stmt.setDate(4, Date.valueOf(ad.getEndDate()));
            stmt.setString(5, ad.getCategory());
            stmt.setDouble(6, ad.getPricePerDay());
            stmt.setDouble(7, ad.getTotalCost());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<Ad> getActiveAdsByCategory(String category) {
        List<Ad> ads = new ArrayList<>();
        String sql = "SELECT * FROM advertisements WHERE category = ? AND is_active = TRUE AND start_date <= CURDATE() AND end_date >= CURDATE()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Ad ad = new Ad();
                ad.setId(rs.getInt("id"));
                ad.setType(rs.getString("type"));
                ad.setContentUrl(rs.getString("content_url"));
                ad.setStartDate(rs.getDate("start_date").toLocalDate());
                ad.setEndDate(rs.getDate("end_date").toLocalDate());
                ads.add(ad);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ads;
    }
}