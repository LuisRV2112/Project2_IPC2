package com.usac.salondebelleza.controllers;

import dao.ServiceCatalogDAO;
import utils.AuthUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import models.Role;

@WebServlet("/services/catalog/*")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 5,   // 5MB
    maxRequestSize = 1024 * 1024 * 5 // 5MB
)
public class ServiceCatalogController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        
        try {
            if (!AuthUtils.checkRole(request, Role.GESTION_SERVICIOS)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }

            int serviceId = Integer.parseInt(request.getPathInfo().split("/")[1]);
            Part filePart = request.getPart("catalog");
            
            if (filePart == null || filePart.getSize() == 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Archivo requerido");
                return;
            }

            String uploadPath = getServletContext().getRealPath("/catalogs");
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdir();

            String fileName = "catalogo_" + serviceId + ".pdf";
            String filePath = uploadPath + File.separator + fileName;
            
            filePart.write(filePath);

            if (ServiceCatalogDAO.uploadCatalog(serviceId, "/catalogs/" + fileName)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al guardar el catálogo");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de servicio inválido");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno del servidor");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int serviceId = Integer.parseInt(request.getPathInfo().split("/")[1]);
            String catalogPath = ServiceCatalogDAO.getCatalogByServiceId(serviceId).getPdfUrl();
            
            if (catalogPath == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Catálogo no encontrado");
                return;
            }

            File pdfFile = new File(getServletContext().getRealPath(catalogPath));
            
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=\"catalogo.pdf\"");
            
            try (InputStream in = new FileInputStream(pdfFile);
                 OutputStream out = response.getOutputStream()) {
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de servicio inválido");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al recuperar el catálogo");
        }
    }
}