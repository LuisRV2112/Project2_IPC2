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

@WebServlet("/reports/ads")
public class ReportsAdsController extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();
        
        try {
            if (!AuthUtils.checkRole(request, Role.MARKETING)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            String reportType = request.getParameter("type");
            Date startDate = parseDate(request.getParameter("startDate"));
            Date endDate = parseDate(request.getParameter("endDate"));

            switch (reportType) {
                case "most_shown":
                case "least_shown":
                    responseData.put("data", ReportDAO.getAdPerformanceReport(reportType, startDate, endDate));
                    break;
                    
                case "most_purchased":
                    responseData.put("data", ReportDAO.getMostPurchasedAdsReport());
                    break;
                    
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tipo de reporte inválido");
                    return;
            }
            
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