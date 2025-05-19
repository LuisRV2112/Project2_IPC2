package com.usac.salondebelleza.controllers;

import dao.EmployeeServiceDAO;
import utils.AuthUtils;
import com.google.gson.Gson;
import dao.EmployeeDAO;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import models.Role;
import models.User;

@WebServlet("/services/employees/*")
public class EmployeeAssignmentController extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();
        
        try {
            if (!AuthUtils.checkRole(request, Role.GESTION_SERVICIOS)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }

            int serviceId = Integer.parseInt(request.getPathInfo().split("/")[1]);
            String[] employeeIds = request.getParameterValues("employeeIds");
            
            if (employeeIds == null || employeeIds.length == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseData.put("error", "Se requieren IDs de empleados");
                return;
            }

            List<Integer> ids = Arrays.stream(employeeIds)
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toList());

            if (EmployeeServiceDAO.assignEmployees(serviceId, ids)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                responseData.put("message", "Empleados asignados correctamente");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseData.put("error", "Error al asignar empleados");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", "IDs inválidos");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("error", "Error interno del servidor");
        }
        
        response.getWriter().write(gson.toJson(responseData));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();
        
        try {
            if (!AuthUtils.checkRole(request, Role.GESTION_SERVICIOS)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }

            String[] pathParts = request.getPathInfo().split("/");
            int serviceId = Integer.parseInt(pathParts[1]);
            int employeeId = Integer.parseInt(pathParts[3]);

            if (EmployeeServiceDAO.unassignEmployee(serviceId, employeeId)) {
                responseData.put("message", "Empleado desasignado correctamente");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                responseData.put("error", "Asignación no encontrada");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", "IDs inválidos");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("error", "Error interno del servidor");
        }
        
        response.getWriter().write(gson.toJson(responseData));
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();
        
        try {
            int serviceId = Integer.parseInt(request.getPathInfo().split("/")[1]);
            List<User> employeeIds = EmployeeDAO.getEmployeesByService(serviceId);
            
            responseData.put("employees", employeeIds);
            response.getWriter().write(gson.toJson(responseData));
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", "ID de servicio inválido");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("error", "Error interno del servidor");
        }
    }
}