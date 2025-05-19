package com.usac.salondebelleza.controllers;

import com.usac.salondebelleza.reports.InvoiceReportGenerator;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.Connection;
import utils.DatabaseConnection;

@WebServlet("/invoices/*")
public class InvoiceController extends HttpServlet {

    // Usamos la clase InvoiceReportGenerator para generar el reporte de factura
    private InvoiceReportGenerator reportGenerator = new InvoiceReportGenerator();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Extraer el ID de la factura desde la URL
            String pathInfo = request.getPathInfo();
            int invoiceId = Integer.parseInt(pathInfo.substring(1));
            
            // Generar PDF con JasperReports usando InvoiceReportGenerator
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=factura_" + invoiceId + ".pdf");
            
            // Obtener la conexión de la base de datos
            Connection conn = DatabaseConnection.getConnection();
            
            // Exportar el reporte de la factura a PDF
            try (OutputStream out = response.getOutputStream()) {
                reportGenerator.exportPdf(invoiceId, conn, out);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar el reporte.");
        }
    }
}
