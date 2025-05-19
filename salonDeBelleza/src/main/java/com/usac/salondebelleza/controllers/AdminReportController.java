package com.usac.salondebelleza.controllers;

import com.usac.salondebelleza.reports.AdminReportGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import utils.DatabaseConnection;

@WebServlet("/admin/reports/*")
public class AdminReportController extends HttpServlet {

    private final AdminReportGenerator reportGenerator = new AdminReportGenerator();

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
            Integer filterId = request.getParameter("filterId") != null ? Integer.valueOf(request.getParameter("filterId")) : null;

            switch (action) {
                case "service-earnings":
                    setupPdfResponse(response, "ganancias_servicio.pdf");
                    reportGenerator.exportServiceEarningsReport(start, end, filterId, conn, out);
                    break;
                case "top-ads":
                    setupPdfResponse(response, "anuncios_mas_mostrados.pdf");
                    reportGenerator.exportTopAdsReport(start, end, conn, out);
                    break;
                case "top-clients":
                    setupPdfResponse(response, "clientes_mas_citas.pdf");
                    reportGenerator.exportTopClientsByAppointmentsReport(start, end, conn, out);
                    break;
                case "least-clients":
                    setupPdfResponse(response, "clientes_menos_citas.pdf");
                    reportGenerator.exportLeastClientsByAppointmentsReport(start, end, conn, out);
                    break;
                case "blacklisted-clients":
                    setupPdfResponse(response, "clientes_lista_negra.pdf");
                    reportGenerator.exportBlacklistedClientsReport(start, end, conn, out);
                    break;
                case "top-spenders":
                    setupPdfResponse(response, "clientes_mas_gastaron.pdf");
                    reportGenerator.exportTopSpendersReport(start, end, filterId, conn, out);
                    break;
                case "least-spenders":
                    setupPdfResponse(response, "clientes_menos_gastaron.pdf");
                    reportGenerator.exportLeastSpendersReport(start, end, filterId, conn, out);
                    break;
                case "employee-earnings":
                    setupPdfResponse(response, "ganancias_empleado.pdf");
                    reportGenerator.exportEmployeeEarningsReport(start, end, filterId, conn, out);
                    break;
                case "top-attended-clients":
                    setupPdfResponse(response, "clientes_mas_atendidos.pdf");
                    reportGenerator.exportTopAttendedClientsReport(start, end, filterId, conn, out);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Reporte no disponible");
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
