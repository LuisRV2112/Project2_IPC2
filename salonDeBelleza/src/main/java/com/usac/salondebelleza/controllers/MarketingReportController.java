package com.usac.salondebelleza.controllers;

import com.usac.salondebelleza.reports.MarketingReportGenerator;
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

@WebServlet("/marketing/reports/*")
public class MarketingReportController extends HttpServlet {

    private final MarketingReportGenerator reportGenerator = new MarketingReportGenerator();

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
                case "top-ads":
                    setupPdfResponse(response, "top_anuncios_mostrados.pdf");
                    reportGenerator.exportTopAds(start, end, conn, out);
                    break;

                case "least-ads":
                    setupPdfResponse(response, "anuncios_menos_mostrados.pdf");
                    reportGenerator.exportLeastAds(start, end, conn, out);
                    break;

                case "ad-usage-history":
                    setupPdfResponse(response, "historial_uso_anuncios.pdf");
                    reportGenerator.exportAdUsageHistory(start, end, conn, out);
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
