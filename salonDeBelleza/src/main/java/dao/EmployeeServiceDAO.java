package dao;

import utils.DatabaseConnection;
import java.sql.*;
import java.util.List;

public class EmployeeServiceDAO {

    public static boolean assignEmployees(int serviceId, List<Integer> employeeIds) {
        String sql = "INSERT INTO employee_services (service_id, employee_id) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (Integer employeeId : employeeIds) {
                stmt.setInt(1, serviceId);
                stmt.setInt(2, employeeId);
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            for (int result : results) {
                if (result <= 0) return false;
            }
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean unassignEmployee(int serviceId, int employeeId) {
        String sql = "DELETE FROM employee_services WHERE service_id = ? AND employee_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, serviceId);
            stmt.setInt(2, employeeId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isEmployeeAssigned(int serviceId, int employeeId) {
        String sql = "SELECT 1 FROM employee_services WHERE service_id = ? AND employee_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, serviceId);
            stmt.setInt(2, employeeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Integer> getEmployeesByService(int serviceId) {
        List<Integer> employees = new java.util.ArrayList<>();
        String sql = "SELECT employee_id FROM employee_services WHERE service_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, serviceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(rs.getInt("employee_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }
}