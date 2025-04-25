package controllers;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import models.User;

@WebFilter("/*")
public class AuthFilter implements Filter {
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        
        if (!path.startsWith("/auth") && !isPublicResource(path)) {
            User user = (User) httpRequest.getSession().getAttribute("user");
            if (user == null) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        chain.doFilter(request, response);
    }
    
    private boolean isPublicResource(String path) {
        return path.startsWith("/public/") || path.equals("/");
    }
}