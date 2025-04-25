package controllers;

import net.sf.jasperreports.engine.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import utils.DatabaseConnection;

@WebServlet("/invoices/*")
public class InvoiceController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();
            int invoiceId = Integer.parseInt(pathInfo.substring(1));
            
            // Generar PDF con JasperReports
            JasperReport report = JasperCompileManager.compileReport(getServletContext().getRealPath("/reports/invoice.jrxml"));
            
            Map<String, Object> params = new HashMap<>();
            params.put("invoice_id", invoiceId);
            
            JasperPrint print = JasperFillManager.fillReport(
                report, 
                params, 
                DatabaseConnection.getConnection()
            );

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=factura.pdf");
            
            JasperExportManager.exportReportToPdfStream(print, response.getOutputStream());
            
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}