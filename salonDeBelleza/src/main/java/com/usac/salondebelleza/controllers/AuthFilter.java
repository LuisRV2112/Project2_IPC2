package com.usac.salondebelleza.controllers;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import models.User;
import utils.AuthUtils;

@WebFilter("/*")
public class AuthFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        
        if (!path.startsWith("/auth") && !isPublicResource(path)) {
            String authHeader = httpRequest.getHeader("Authorization");
            
            // Verificar si el token JWT está presente
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            
            String token = authHeader.substring(7); // Extraer el token
            User user = AuthUtils.parseToken(token); // Validar y obtener usuario
            
            if (user == null) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            
            // Establecer usuario en la sesión para compatibilidad
            HttpSession session = httpRequest.getSession();
            session.setAttribute("user", user);
        }
        
        chain.doFilter(request, response);
    }
    
    private boolean isPublicResource(String path) {
        return path.startsWith("/public/") || path.equals("/");
    }
}