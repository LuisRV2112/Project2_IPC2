package com.usac.salondebelleza.controllers;

import dao.AppointmentDAO;
import dao.BlacklistDAO;
import dao.InvoiceDAO;
import models.Appointment;
import utils.AuthUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import models.Role;
import models.User;
import utils.GlobalUtils;
import utils.LocalDateTimeAdapter;

@WebServlet("/employee/appointments/*")
public class EmployeeAppointmentController extends HttpServlet {
    private final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .create();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        
        try {
            User user = AuthUtils.getCurrentUser(request);
            if (!AuthUtils.checkRole(request, Role.EMPLEADO)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            LocalDate date = LocalDate.parse(request.getParameter("date"));
            List<Appointment> appointments = AppointmentDAO.getEmployeeAppointments(
                user.getId(), 
                date.atStartOfDay(), 
                date.plusDays(1).atStartOfDay()
            );
            
            response.getWriter().write(gson.toJson(appointments));
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();
        
        try {
            User user = AuthUtils.getCurrentUser(request);
            if (!AuthUtils.checkRole(request, Role.EMPLEADO)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            String pathInfo = request.getPathInfo();
            int appointmentId = Integer.parseInt(pathInfo.substring(1));
            
            Map<String, String> data = GlobalUtils.parseJsonRequest(request);
            String status = data.get("status");
            boolean noShow = Boolean.parseBoolean(data.get("noShow"));
            
            if (AppointmentDAO.updateAppointmentStatus(appointmentId, status, noShow)) {
                if (noShow) {
                    BlacklistDAO.addToBlacklist(
                        AppointmentDAO.getAppointment(appointmentId).getClientId(),
                        "No se presentó a la cita"
                    );
                }
                responseData.put("message", "Estado actualizado");
            } else {
                responseData.put("error", "Error al actualizar");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            responseData.put("error", "Datos inválidos");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        response.getWriter().write(gson.toJson(responseData));
    }
}