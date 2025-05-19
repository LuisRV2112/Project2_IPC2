package dao;

import utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdPricingDAO {

    // 1. Crear nuevo precio
    public static boolean insertPrice(double pricePerDay) {
        String sql = "INSERT INTO ad_pricing (price_per_day, effective_date) VALUES (?, CURDATE())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, pricePerDay);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Obtener el precio más reciente (precio actual)
    public static Double getCurrentPrice() {
        String sql = "SELECT price_per_day FROM ad_pricing ORDER BY effective_date DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("price_per_day");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // o lanzar excepción si prefieres
    }

    // 3. Obtener historial completo de precios
    public static List<PricingEntry> getPriceHistory() {
        List<PricingEntry> history = new ArrayList<>();
        String sql = "SELECT * FROM ad_pricing ORDER BY effective_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PricingEntry entry = new PricingEntry();
                entry.setId(rs.getInt("id"));
                entry.setPricePerDay(rs.getDouble("price_per_day"));
                entry.setEffectiveDate(rs.getDate("effective_date").toLocalDate());
                history.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    // Clase interna para representar los registros del historial
    public static class PricingEntry {
        private int id;
        private double pricePerDay;
        private LocalDate effectiveDate;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public double getPricePerDay() { return pricePerDay; }
        public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }

        public LocalDate getEffectiveDate() { return effectiveDate; }
        public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
    }
}
