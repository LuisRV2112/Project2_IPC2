package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.User;
import utils.DatabaseConnection;

public class EmployeeDAO {

    public static List<User> getEmployeesByService(int serviceId) {
       String sql = "SELECT e.id, e.email FROM employee_services es " +
                 "JOIN users e ON es.employee_id = e.id " +
                 "WHERE es.service_id = ?";
       List<User> employees = new ArrayList<>();

       try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

           stmt.setInt(1, serviceId);
           ResultSet rs = stmt.executeQuery();

           while (rs.next()) {
               User user = new User();
               user.setId(rs.getInt("id"));
               user.setEmail(rs.getString("email"));
               employees.add(user);
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return employees;
   }
}