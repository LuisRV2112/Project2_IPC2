package dao;

import models.Appointment;
import utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
   public static boolean updateAppointmentStatus(int id, String status, boolean noShow) {
        String sql = "UPDATE appointments SET status = ?, no_show = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setBoolean(2, noShow);
            stmt.setInt(3, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static int createAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (client_id, employee_id, service_id, start_time, end_time, status) " +
                    "VALUES (?, ?, ?, ?, ?, 'pendiente')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, appointment.getClientId());
            stmt.setInt(2, appointment.getEmployeeId());
            stmt.setInt(3, appointment.getServiceId());
            stmt.setTimestamp(4, Timestamp.valueOf(appointment.getStartTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(appointment.getEndTime()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static List<Appointment> getClientAppointments(int clientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, s.price FROM appointments a " +
                    "JOIN services s ON a.service_id = s.id " +
                    "WHERE client_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appt = mapAppointmentFromResultSet(rs);
                    //appt.setPrice(rs.getDouble("price"));
                    appointments.add(appt);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public static List<Appointment> getEmployeeAppointments(int employeeId, LocalDateTime start, LocalDateTime end) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE employee_id = ? " +
                    "AND start_time BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, employeeId);
            stmt.setTimestamp(2, Timestamp.valueOf(start));
            stmt.setTimestamp(3, Timestamp.valueOf(end));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapAppointmentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public static boolean isEmployeeAvailable(int employeeId, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT COUNT(*) FROM appointments " +
                    "WHERE employee_id = ? " +
                    "AND ((start_time < ? AND end_time > ?) " +
                    "OR (start_time BETWEEN ? AND ?))";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, employeeId);
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            stmt.setTimestamp(3, Timestamp.valueOf(start));
            stmt.setTimestamp(4, Timestamp.valueOf(start));
            stmt.setTimestamp(5, Timestamp.valueOf(end));
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Appointment mapAppointmentFromResultSet(ResultSet rs) throws SQLException {
        Appointment appt = new Appointment();
        appt.setId(rs.getInt("id"));
        appt.setClientId(rs.getInt("client_id"));
        appt.setEmployeeId(rs.getInt("employee_id"));
        appt.setServiceId(rs.getInt("service_id"));
        appt.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        appt.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        appt.setStatus(rs.getString("status"));
        appt.setNoShow(rs.getBoolean("no_show"));
        return appt;
    }
    
    public static Appointment getAppointment(int id) {
        String sql = "SELECT * FROM appointments WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapAppointmentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    

}