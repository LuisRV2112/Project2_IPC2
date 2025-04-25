package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import utils.DatabaseConnection;

public class ReportDAO {

    public static List<Map<String, Object>> getTopServices(String reportType, Date startDate, Date endDate) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = buildReportQuery(reportType);
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set parameters for date range
            stmt.setDate(1, startDate);
            stmt.setDate(2, startDate);
            stmt.setDate(3, endDate);
            stmt.setDate(4, endDate);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("service_name", rs.getString("name"));
                    row.put("total", rs.getObject("total"));
                    results.add(row);
                }
            }
            return results;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static String buildReportQuery(String reportType) {
        switch (reportType.toLowerCase()) {
            case "most_reserved":
                return "SELECT s.name, COUNT(a.id) as total " +
                       "FROM appointments a " +
                       "JOIN services s ON a.service_id = s.id " +
                       "WHERE (? IS NULL OR a.start_time >= ?) " +
                       "AND (? IS NULL OR a.start_time <= ?) " +
                       "GROUP BY s.id ORDER BY total DESC LIMIT 5";
            
            case "least_reserved":
                return "SELECT s.name, COUNT(a.id) as total " +
                       "FROM appointments a " +
                       "JOIN services s ON a.service_id = s.id " +
                       "WHERE (? IS NULL OR a.start_time >= ?) " +
                       "AND (? IS NULL OR a.start_time <= ?) " +
                       "GROUP BY s.id ORDER BY total ASC LIMIT 5";
            
            case "most_revenue":
                return "SELECT s.name, SUM(s.price) as total " +
                       "FROM appointments a " +
                       "JOIN services s ON a.service_id = s.id " +
                       "WHERE (? IS NULL OR a.start_time >= ?) " +
                       "AND (? IS NULL OR a.start_time <= ?) " +
                       "GROUP BY s.id ORDER BY total DESC LIMIT 5";
            
            default:
                throw new IllegalArgumentException("Tipo de reporte no válido");
        }
    }
    
    // Top 5 anuncios más/menos mostrados
    public static List<Map<String, Object>> getAdPerformanceReport(String type, Date startDate, Date endDate) {
        List<Map<String, Object>> results = new ArrayList<>();
        String order = type.equals("most_shown") ? "DESC" : "ASC";
        
        String sql = "SELECT a.id, a.content_url, COUNT(i.id) AS total_shown " +
                     "FROM advertisements a " +
                     "LEFT JOIN ad_impressions i ON a.id = i.ad_id " +
                     "WHERE (? IS NULL OR i.shown_at >= ?) " +
                     "AND (? IS NULL OR i.shown_at <= ?) " +
                     "GROUP BY a.id " +
                     "ORDER BY total_shown " + order + " LIMIT 5";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, startDate);
            stmt.setDate(2, startDate);
            stmt.setDate(3, endDate);
            stmt.setDate(4, endDate);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("ad_id", rs.getInt("id"));
                row.put("content_url", rs.getString("content_url"));
                row.put("total_shown", rs.getInt("total_shown"));
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Historial de anuncios más comprados
    public static List<Map<String, Object>> getMostPurchasedAdsReport() {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT a.type, a.content_url, a.purchase_count, " +
                     "SUM(p.amount) AS total_revenue " +
                     "FROM advertisements a " +
                     "LEFT JOIN ad_payments p ON a.id = p.ad_id " +
                     "GROUP BY a.id " +
                     "ORDER BY a.purchase_count DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("type", rs.getString("type"));
                row.put("content_url", rs.getString("content_url"));
                row.put("purchase_count", rs.getInt("purchase_count"));
                row.put("total_revenue", rs.getDouble("total_revenue"));
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}