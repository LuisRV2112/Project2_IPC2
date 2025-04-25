package com.usac.salondebelleza.controllers;

import com.google.gson.Gson;
import models.User;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import utils.AuthUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import utils.GlobalUtils;

@WebServlet(name = "AuthController", urlPatterns = {"/auth/*"})
public class AuthController extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getRequestURI().substring(request.getContextPath().length());
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, Object> responseData = new HashMap<>();
        
        try {
            if (path.equals("/auth/login")) {
                handleLogin(request, response, responseData);
            } else if (path.equals("/auth/register")) {
                handleRegister(request, response, responseData);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("error", "Internal server error");
            response.getWriter().write(gson.toJson(responseData));
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response, 
            Map<String, Object> responseData) throws IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        if (email == null || password == null) {
            Map<String, String> jsonParams = GlobalUtils.parseJsonRequest(request);
            email = jsonParams.get("email");
            password = jsonParams.get("password");
            if (email == null || password == null){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseData.put("error", "Email and password are required");
                response.getWriter().write(gson.toJson(responseData));
                return;
            }

        }
        
        User user = UserDAO.findByEmail(email);
        if (user != null && AuthUtils.checkPassword(password, user.getPassword())) {
            String token = AuthUtils.generateToken(user); 
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("email", user.getEmail());
            userData.put("roleId", user.getRoleId());
            
            responseData.put("message", "Login successful");
            responseData.put("token", token);
            responseData.put("user", userData);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            responseData.put("error", "Invalid credentials");
        }
        response.getWriter().write(gson.toJson(responseData));
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response, 
            Map<String, Object> responseData) throws IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String dpi = request.getParameter("dpi");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        
        if (email == null || password == null || dpi == null || phone == null || address == null) {
            Map<String, String> jsonParams = GlobalUtils.parseJsonRequest(request);
            email = jsonParams.get("email");
            password = jsonParams.get("password");
            dpi = jsonParams.get("dpi");
             phone = jsonParams.get("phone");
            address = jsonParams.get("address");
            if(email == null || password == null || dpi == null || phone == null || address == null){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseData.put("error", "All fields are required");
                response.getWriter().write(gson.toJson(responseData));
                return;
            }
        }
        
        if (UserDAO.findByEmail(email) != null) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            responseData.put("error", "Email already registered");
            response.getWriter().write(gson.toJson(responseData));
            return;
        }
        
        User newUser = new User(
            0, // ID se genera automáticamente
            email,
            AuthUtils.hashPassword(password),
            dpi,
            phone,
            address,
            5,
            true// Asume que 5 es el ID del rol 'cliente'
        );
        
        if (UserDAO.createUser(newUser)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            responseData.put("message", "User registered successfully");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("error", "Failed to register user");
        }
        response.getWriter().write(gson.toJson(responseData));
    }
}