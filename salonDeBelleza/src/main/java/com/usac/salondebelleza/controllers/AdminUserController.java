package com.usac.salondebelleza.controllers;

import com.google.gson.Gson;
import dao.UserDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Role;
import models.User;
import utils.AuthUtils;

@WebServlet("/admin/users/*")
public class AdminUserController extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        
        try {
            if (!AuthUtils.checkRole(request, Role.ADMINISTRADOR)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            List<User> users = UserDAO.getAllUsers();
            response.getWriter().write(gson.toJson(users));
            
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();
        
        try {
            if (!AuthUtils.checkRole(request, Role.ADMINISTRADOR)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            User user = gson.fromJson(request.getReader(), User.class);
            user.setPassword(AuthUtils.hashPassword(user.getPassword()));
            user.setActive(true);

            if (UserDAO.createUser(user)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                responseData.put("message", "Usuario creado exitosamente");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseData.put("error", "Error al crear usuario");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", "Datos inválidos");
        }
        
        response.getWriter().write(gson.toJson(responseData));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();
        
        try {
            if (!AuthUtils.checkRole(request, Role.ADMINISTRADOR)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            int userId = Integer.parseInt(request.getPathInfo().substring(1));
            if (UserDAO.deactivateUser(userId)) {
                responseData.put("message", "Usuario desactivado");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                responseData.put("error", "Usuario no encontrado");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", "ID inválido");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("error", "Error interno");
        }
        
        response.getWriter().write(gson.toJson(responseData));
    }
}