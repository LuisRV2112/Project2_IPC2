package dao;

import utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DisponibilidadDAO {

    public static List<LocalTime> getAvailableSlots(int employeeId, LocalDate date) {
        List<LocalTime> slots = new ArrayList<>();
        
        // 1. Obtener horario del salón
        String sqlSchedule = "SELECT * FROM salon_hours WHERE day_of_week = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlSchedule)) {
            
            stmt.setInt(1, date.getDayOfWeek().getValue());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next() && rs.getBoolean("is_active")) {
                LocalTime opening = rs.getTime("opening_time").toLocalTime();
                LocalTime closing = rs.getTime("closing_time").toLocalTime();
                
                // 2. Obtener citas existentes
                List<LocalTime> bookedSlots = getBookedSlots(employeeId, date);
                
                // 3. Generar slots disponibles
                LocalTime current = opening;
                while (current.isBefore(closing)) {
                    if (!bookedSlots.contains(current)) {
                        slots.add(current);
                    }
                    current = current.plusMinutes(30);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return slots;
    }

    private static List<LocalTime> getBookedSlots(int employeeId, LocalDate date) {
        List<LocalTime> booked = new ArrayList<>();
        String sql = "SELECT start_time FROM appointments " +
                    "WHERE employee_id = ? AND DATE(start_time) = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, employeeId);
            stmt.setDate(2, Date.valueOf(date));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                booked.add(rs.getTimestamp("start_time").toLocalDateTime().toLocalTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return booked;
    }
}