package controllers;

import dao.AppointmentDAO;
import dao.BlacklistDAO;
import dao.InvoiceDAO;
import models.Appointment;
import utils.AuthUtils;
import com.google.gson.Gson;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import models.Role;
import models.User;

@WebServlet("/client/appointments/*")
public class ClientAppointmentController extends HttpServlet {
    private final Gson gson = new Gson();

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

            Map<String, String> data = gson.fromJson(request.getReader(), HashMap.class);
            Appointment appointment = new Appointment();
            appointment.setClientId(user.getId());
            appointment.setEmployeeId(Integer.parseInt(data.get("employeeId")));
            appointment.setServiceId(Integer.parseInt(data.get("serviceId")));
            appointment.setStartTime(LocalDateTime.parse(data.get("startTime")));
            
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
}