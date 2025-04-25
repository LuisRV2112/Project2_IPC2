package com.usac.salondebelleza.controllers;

import dao.AppointmentDAO;
import dao.BlacklistDAO;
import dao.InvoiceDAO;
import models.Appointment;
import utils.AuthUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import models.Role;
import models.User;
import utils.GlobalUtils;
import utils.LocalDateTimeAdapter;

@WebServlet("/client/appointments/*")
public class ClientAppointmentController extends HttpServlet {
    private final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .create();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();
        
        try {
            User user = AuthUtils.getCurrentUser(request);
            if (user == null || !AuthUtils.checkRole(request, Role.CLIENTE)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            if (BlacklistDAO.isClientBlacklisted(user.getId())) {
                responseData.put("error", "Requiere aprobación administrativa");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            Map<String, String> data = GlobalUtils.parseJsonRequest(request);
            Appointment appointment = new Appointment();
            appointment.setClientId(user.getId());
            appointment.setEmployeeId(Integer.parseInt(data.get("employeeId")));
            appointment.setServiceId(Integer.parseInt(data.get("serviceId")));
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(data.get("startTime"));
            LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
            appointment.setStartTime(localDateTime);
            zonedDateTime = ZonedDateTime.parse(data.get("endTime"));
            localDateTime = zonedDateTime.toLocalDateTime();
            appointment.setEndTime(localDateTime);
            
            
            if (AppointmentDAO.isEmployeeAvailable(appointment.getEmployeeId(), 
                appointment.getStartTime(), appointment.getEndTime())) {
                
                int appointmentId = AppointmentDAO.createAppointment(appointment);
                InvoiceDAO.generateInvoice(appointmentId);
                
                responseData.put("appointmentId", appointmentId);
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                responseData.put("error", "Horario no disponible");
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            }
        } catch (Exception e) {
            responseData.put("error", "Datos inválidos");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        response.getWriter().write(gson.toJson(responseData));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        
        
        try {
            User user = AuthUtils.getCurrentUser(request);
            if (!AuthUtils.checkRole(request, Role.CLIENTE)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            List<Appointment> appointments = AppointmentDAO.getClientAppointments(user.getId());
            response.getWriter().write(gson.toJson(appointments));
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();

        try {
            User user = AuthUtils.getCurrentUser(request);
            if (user == null || !AuthUtils.checkRole(request, Role.CLIENTE)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // Obtener ID de la cita de la URL
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de cita requerido");
                return;
            }

            int appointmentId = Integer.parseInt(pathInfo.split("/")[1]);
            
            // Verificar que la cita pertenece al cliente
            Appointment appointment = AppointmentDAO.getAppointment(appointmentId);
            if (appointment == null || appointment.getClientId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Cita no encontrada");
                return;
            }

            // Verificar que la cita no esté ya cancelada o completada
            if (appointment.getStatus().equals("cancelada") || appointment.getStatus().equals("completada")) {
                responseData.put("error", "No se puede cancelar una cita " + appointment.getStatus());
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                return;
            }

            // Verificar que no sea demasiado tarde para cancelar (ej. menos de 24 horas antes)
            LocalDateTime now = LocalDateTime.now();
            if (appointment.getStartTime().minusHours(24).isBefore(now)) {
                responseData.put("error", "Debe cancelar con al menos 24 horas de anticipación");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // Actualizar estado a "cancelada"
            if (AppointmentDAO.updateAppointmentStatus(appointmentId, "cancelada", false)) {
                responseData.put("message", "Cita cancelada exitosamente");
                responseData.put("appointmentId", appointmentId);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseData.put("error", "Error al cancelar la cita");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", "ID de cita inválido");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("error", e.getMessage());
        }

        response.getWriter().write(gson.toJson(responseData));
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }
}