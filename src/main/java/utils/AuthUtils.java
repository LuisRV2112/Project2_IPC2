package utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import models.Role;
import models.User;
import org.mindrot.jbcrypt.BCrypt;

public class AuthUtils {
    
    public static User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }
    
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
    
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
    
    public static boolean checkRole(HttpServletRequest request, Role requiredRole) {
        User user = (User) request.getSession().getAttribute("user");
        return user != null && user.getRoleId() == requiredRole.getId();
    }
}