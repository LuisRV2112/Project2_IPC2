package com.usac.salondebelleza.controllers;

import dao.SalonScheduleDAO;
import models.SalonSchedule;
import utils.AuthUtils;
import com.google.gson.Gson;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import models.Role;
import utils.GlobalUtils;

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

            Map<String, String> params = GlobalUtils.parseJsonRequest(request);

            SalonSchedule schedule = new SalonSchedule();
            schedule.setDayOfWeek(Integer.parseInt(params.get("dayOfWeek")));
            schedule.setOpeningTime(Time.valueOf(params.get("openingTime") + ":00"));
            schedule.setClosingTime(Time.valueOf(params.get("closingTime") + ":00"));
            schedule.setIsActive(Boolean.parseBoolean(params.get("isActive")));

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
        } catch (IllegalArgumentException | NullPointerException e) {
            responseData.put("error", "Datos inválidos");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        response.getWriter().write(gson.toJson(responseData));
    }
}