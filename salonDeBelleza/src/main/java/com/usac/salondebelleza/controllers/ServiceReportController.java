package com.usac.salondebelleza.controllers;

import com.usac.salondebelleza.reports.ServiceReportGenerator;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.Connection;
import java.sql.Date;
import utils.DatabaseConnection;

@WebServlet("/services/reports/*")
public class ServiceReportController extends HttpServlet {

    private final ServiceReportGenerator reportGenerator = new ServiceReportGenerator();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] parts = request.getPathInfo().split("/");
        if (parts.length < 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ruta inválida");
            return;
        }

        String action = parts[1];

        try (Connection conn = DatabaseConnection.getConnection();
             OutputStream out = response.getOutputStream()) {

            Date start = parseDate(request.getParameter("start"));
            Date end = parseDate(request.getParameter("end"));

            switch (action) {
                case "top-most": {
                    setupPdfResponse(response, "servicios_mas_reservados.pdf");
                    reportGenerator.exportMostBookedServicesReport(start, end, conn, out);
                    break;
                }

                case "top-least": {
                    setupPdfResponse(response, "servicios_menos_reservados.pdf");
                    reportGenerator.exportLeastBookedServicesReport(start, end, conn, out);
                    break;
                }

                case "top-income": {
                    setupPdfResponse(response, "servicio_mas_ingresos.pdf");
                    reportGenerator.exportTopIncomeServiceReport(start, end, conn, out);
                    break;
                }

                default: response.sendError(HttpServletResponse.SC_NOT_FOUND, "Reporte no disponible");
            }

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar el reporte");
        }
    }

    private void setupPdfResponse(HttpServletResponse response, String filename) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
    }

    private Date parseDate(String param) {
        try {
            return (param != null && !param.isEmpty()) ? Date.valueOf(param) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
