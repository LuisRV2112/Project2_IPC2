package dao;

import models.Ad;
import utils.DatabaseConnection;
import java.sql.*;
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
                ad.setCategory(rs.getString("category"));
                ads.add(ad);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ads;
    }
    
        public static List<Ad> getActiveAds() {
        List<Ad> ads = new ArrayList<>();
        String sql = "SELECT * FROM advertisements WHERE is_active = TRUE ";//AND start_date <= CURDATE() AND end_date >= CURDATE()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Ad ad = new Ad();
                ad.setId(rs.getInt("id"));
                ad.setType(rs.getString("type"));
                ad.setContentUrl(rs.getString("content_url"));
                ad.setStartDate(rs.getDate("start_date").toLocalDate());
                ad.setEndDate(rs.getDate("end_date").toLocalDate());
                ad.setCategory(rs.getString("category"));
                ads.add(ad);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ads;
    }
        
    public static Ad getAdById(int id) {
        String sql = "SELECT * FROM advertisements WHERE id = ?";
        Ad ad = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                ad = new Ad();
                ad.setId(rs.getInt("id"));
                ad.setType(rs.getString("type"));
                ad.setContentUrl(rs.getString("content_url"));
                ad.setStartDate(rs.getDate("start_date").toLocalDate());
                ad.setEndDate(rs.getDate("end_date").toLocalDate());
                ad.setCategory(rs.getString("category"));
                ad.setIsActive(rs.getBoolean("is_active"));
                ad.setPricePerDay(rs.getDouble("price_per_day"));
                ad.setTotalCost(rs.getDouble("total_cost"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ad;
    }
    
    public static boolean updateAd(Ad ad) {
        String sql = "UPDATE advertisements SET type = ?, content_url = ?, start_date = ?, end_date = ?, " +
                     "category = ?, is_active = ?, price_per_day = ?, total_cost = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ad.getType());
            stmt.setString(2, ad.getContentUrl());
            stmt.setDate(3, Date.valueOf(ad.getStartDate()));
            stmt.setDate(4, Date.valueOf(ad.getEndDate()));
            stmt.setString(5, ad.getCategory());
            stmt.setBoolean(6, ad.isIsActive());
            stmt.setDouble(7, ad.getPricePerDay());
            stmt.setDouble(8, ad.getTotalCost());
            stmt.setInt(9, ad.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean adExists(int id) {
        String sql = "SELECT COUNT(*) FROM advertisements WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean deleteAd(int id) {
        String sql = "DELETE FROM advertisements WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deactivateAd(int id) {
        String sql = "UPDATE advertisements SET is_active = FALSE WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}