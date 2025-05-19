package com.usac.salondebelleza.controllers;

import dao.ReportDAO;
import utils.AuthUtils;
import com.google.gson.Gson;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.Date;
import java.util.Map;
import java.util.HashMap;
import models.Role;

@WebServlet("/reports/services")
public class ReportController extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();
        
        try {
            if (!AuthUtils.checkRole(request, Role.ADMINISTRADOR)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }

            String reportType = request.getParameter("type");
            Date startDate = parseDate(request.getParameter("startDate"));
            Date endDate = parseDate(request.getParameter("endDate"));

            if (reportType == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseData.put("error", "Tipo de reporte requerido");
                return;
            }

            responseData.put("data", ReportDAO.getTopServices(reportType, startDate, endDate));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("error", "Error generando el reporte");
        }
        
        response.getWriter().write(gson.toJson(responseData));
    }

    private Date parseDate(String dateString) {
        return dateString != null ? Date.valueOf(dateString) : null;
    }
}