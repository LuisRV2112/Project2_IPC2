package controllers;

import dao.SalonScheduleDAO;
import models.SalonSchedule;
import utils.AuthUtils;
import com.google.gson.Gson;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import models.Role;

@WebServlet("/admin/schedule/*")
public class ScheduleController extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        
        try {
            if (!AuthUtils.checkRole(request, Role.ADMINISTRADOR)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                List<SalonSchedule> fullSchedule = SalonScheduleDAO.getFullSchedule();
                response.getWriter().write(gson.toJson(fullSchedule));
            } else {
                int dayOfWeek = Integer.parseInt(pathInfo.substring(1));
                SalonSchedule daySchedule = SalonScheduleDAO.getScheduleForDay(dayOfWeek);
                if (daySchedule != null) {
                    response.getWriter().write(gson.toJson(daySchedule));
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Día inválido");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();
        
        try {
            if (!AuthUtils.checkRole(request, Role.ADMINISTRADOR)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            SalonSchedule schedule = gson.fromJson(request.getReader(), SalonSchedule.class);
            
            if (schedule.getClosingTime().before(schedule.getOpeningTime())) {
                responseData.put("error", "La hora de cierre debe ser posterior a la de apertura");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else if (SalonScheduleDAO.updateSchedule(schedule)) {
                responseData.put("message", "Horario actualizado");
                response.setStatus(HttpServletResponse.SC_OK);
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