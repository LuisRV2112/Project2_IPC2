package dao;

import models.Service;
import utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    public static boolean createService(Service service) {
        String sql = "INSERT INTO services (name, description, image_url, duration_min, price, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, service.getName());
            stmt.setString(2, service.getDescription());
            stmt.setString(3, service.getImageUrl());
            stmt.setInt(4, service.getDurationMin());
            stmt.setDouble(5, service.getPrice());
            stmt.setBoolean(6, service.isIsActive());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateService(Service service) {
        String sql = "UPDATE services SET name = ?, description = ?, image_url = ?, duration_min = ?, price = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, service.getName());
            stmt.setString(2, service.getDescription());
            stmt.setString(3, service.getImageUrl());
            stmt.setInt(4, service.getDurationMin());
            stmt.setDouble(5, service.getPrice());
            stmt.setInt(6, service.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean toggleServiceStatus(int serviceId, boolean isActive) {
        String sql = "UPDATE services SET is_active = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, isActive);
            stmt.setInt(2, serviceId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Service> getAllServices(boolean isActive) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM services ";
        
        if(isActive) {
            sql += "WHERE is_active = true";
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Service service = new Service();
                service.setId(rs.getInt("id"));
                service.setName(rs.getString("name"));
                service.setDescription(rs.getString("description"));
                service.setImageUrl(rs.getString("image_url"));
                service.setDurationMin(rs.getInt("duration_min"));
                service.setPrice(rs.getDouble("price"));
                service.setIsActive(rs.getBoolean("is_active"));
                
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    public static Service getServiceById(int serviceId) {
        String sql = "SELECT * FROM services WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, serviceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getInt("id"));
                    service.setName(rs.getString("name"));
                    service.setDescription(rs.getString("description"));
                    service.setImageUrl(rs.getString("image_url"));
                    service.setDurationMin(rs.getInt("duration_min"));
                    service.setPrice(rs.getDouble("price"));
                    service.setIsActive(rs.getBoolean("is_active"));
                    return service;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}