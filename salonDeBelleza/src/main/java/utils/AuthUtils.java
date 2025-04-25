package utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import models.Role;
import models.User;
import org.mindrot.jbcrypt.BCrypt;

public class AuthUtils {
    
    private static final long EXPIRATION_TIME = 86400000; // 24h
    private static final String SECRET_STRING = "dce2aa56f554b570a80c952f668be9a8eb64f19293912d3bcd785b6d674626e2"; // Must be ≥256 bits
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    public static String generateToken(User user) {
        return Jwts.builder()
            .setSubject(user.getEmail())
            .claim("id", user.getId())
            .claim("roleId", user.getRoleId())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(SECRET_KEY) // Key automatically infers HS256
            .compact();
    }
    
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
    
    public static User parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

            User user = new User();
            // Retrieve 'id' as Number and convert to int
            Number id = claims.get("id", Number.class);
            user.setId(id.intValue());
            // Retrieve 'roleId' as Number and convert to int
            Number roleId = claims.get("roleId", Number.class);
            user.setRoleId(roleId.intValue());
            user.setEmail(claims.getSubject());
            return user;
        } catch (JwtException e) {
            return null; // Token inválido o expirado
        }
    }
}