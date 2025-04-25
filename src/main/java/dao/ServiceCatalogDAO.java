package dao;

import models.ServiceCatalog;
import utils.DatabaseConnection;
import java.sql.*;

public class ServiceCatalogDAO {

    public static boolean uploadCatalog(int serviceId, String pdfUrl) {
        String sql = "INSERT INTO service_catalogs (service_id, pdf_url) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE pdf_url = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, serviceId);
            stmt.setString(2, pdfUrl);
            stmt.setString(3, pdfUrl);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ServiceCatalog getCatalogByServiceId(int serviceId) {
        String sql = "SELECT * FROM service_catalogs WHERE service_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, serviceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ServiceCatalog(
                        rs.getInt("service_id"),
                        rs.getString("pdf_url")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean deleteCatalog(int serviceId) {
        String sql = "DELETE FROM service_catalogs WHERE service_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, serviceId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}