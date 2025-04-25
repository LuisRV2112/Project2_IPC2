package dao;

import models.SalonSchedule;
import utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SalonScheduleDAO {

    public static boolean updateSchedule(SalonSchedule schedule) {
        String sql = "UPDATE salon_hours SET opening_time = ?, closing_time = ?, is_active = ? WHERE day_of_week = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTime(1, schedule.getOpeningTime());
            stmt.setTime(2, schedule.getClosingTime());
            stmt.setBoolean(3, schedule.isIsActive());
            stmt.setInt(4, schedule.getDayOfWeek());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<SalonSchedule> getFullSchedule() {
        List<SalonSchedule> schedule = new ArrayList<>();
        String sql = "SELECT * FROM salon_hours ORDER BY day_of_week";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                SalonSchedule day = new SalonSchedule();
                day.setId(rs.getInt("id"));
                day.setDayOfWeek(rs.getInt("day_of_week"));
                day.setOpeningTime(rs.getTime("opening_time"));
                day.setClosingTime(rs.getTime("closing_time"));
                day.setIsActive(rs.getBoolean("is_active"));
                schedule.add(day);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedule;
    }

    public static SalonSchedule getScheduleForDay(int dayOfWeek) {
        String sql = "SELECT * FROM salon_hours WHERE day_of_week = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, dayOfWeek);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SalonSchedule day = new SalonSchedule();
                    day.setId(rs.getInt("id"));
                    day.setDayOfWeek(rs.getInt("day_of_week"));
                    day.setOpeningTime(rs.getTime("opening_time"));
                    day.setClosingTime(rs.getTime("closing_time"));
                    day.setIsActive(rs.getBoolean("is_active"));
                    return day;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static LocalDateTime getOpeningTime(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        SalonSchedule schedule = getScheduleForDay(dayOfWeek);
        
        if (schedule != null && schedule.isIsActive()) {
            return LocalDateTime.of(date, schedule.getOpeningTime().toLocalTime());
        }
        return null;
    }

    public static LocalDateTime getClosingTime(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        SalonSchedule schedule = getScheduleForDay(dayOfWeek);
        
        if (schedule != null && schedule.isIsActive()) {
            return LocalDateTime.of(date, schedule.getClosingTime().toLocalTime());
        }
        return null;
    }
}