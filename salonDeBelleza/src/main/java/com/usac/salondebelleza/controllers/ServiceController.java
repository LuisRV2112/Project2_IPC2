package com.usac.salondebelleza.controllers;

import dao.ServiceDAO;
import models.Service;
import utils.AuthUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dao.ServiceCatalogDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Role;
import models.ServiceCatalog;
import utils.GlobalUtils;

@WebServlet(name = "ServiceController", urlPatterns = {"/services/*"})
@MultipartConfig
public class ServiceController extends HttpServlet {
    private final Gson gson = new Gson();
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (request.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();
        
        try {
            if (!AuthUtils.checkRole(request, Role.GESTION_SERVICIOS)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }
            
            Map<String, String> data = GlobalUtils.parseJsonRequest(request);

            Service service = new Service();
            service.setName(data.get("name"));
            service.setDescription(data.get("description"));
            service.setImageUrl(data.get("imageUrl"));
            service.setDurationMin(Integer.parseInt(data.get("durationMin")));
            service.setPrice(Double.parseDouble(data.get("price")));
            service.setIsActive(true);

            if (ServiceDAO.createService(service)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                responseData.put("data", service);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseData.put("error", "Error al crear el servicio");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", "Datos numéricos inválidos");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("error", "Error interno del servidor");
        }
        
        response.getWriter().write(gson.toJson(responseData));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();
        
        try {
            if (!AuthUtils.checkRole(request, Role.GESTION_SERVICIOS)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }

            int serviceId = Integer.parseInt(request.getPathInfo().substring(1));
            Service service = ServiceDAO.getServiceById(serviceId);
            
            if (service == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Servicio no encontrado");
                return;
            }

            Map<String, String> data = GlobalUtils.parseJsonRequest(request);

            service.setName(data.get("name"));
            service.setDescription(data.get("description"));
            service.setImageUrl(data.get("imageUrl"));
            service.setDurationMin(Integer.parseInt(data.get("durationMin")));
            service.setPrice(Double.parseDouble(data.get("price")));

            if (ServiceDAO.updateService(service)) {
                responseData.put("data", service);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseData.put("error", "Error al actualizar el servicio");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", "Datos numéricos inválidos");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("error", "Error interno del servidor");
        }
        
        response.getWriter().write(gson.toJson(responseData));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                List<Service> services = ServiceDAO.getAllServices(AuthUtils.checkRole(request, Role.CLIENTE));
                List<JsonObject> servicesWithCatalogs = new ArrayList<>();

                for (Service service : services) {
                    JsonObject serviceJson = gson.toJsonTree(service).getAsJsonObject();
                    ServiceCatalog catalog = ServiceCatalogDAO.getCatalogByServiceId(service.getId());
                    String pdf = "";

                    if (catalog != null) {
                        pdf = catalog.getPdfUrl();
                    }
                    serviceJson.addProperty("catalogoPdfUrl", pdf);
                        
                    servicesWithCatalogs.add(serviceJson);
                }

                responseData.put("data", servicesWithCatalogs);
            } else {
                // Obtener servicio por ID
                String[] parts = pathInfo.split("/");
                int serviceId = Integer.parseInt(parts[1]);
                Service service = ServiceDAO.getServiceById(serviceId);

                if (service != null) {
                    // Obtener el catálogo asociado al servicio
                    ServiceCatalog catalog = ServiceCatalogDAO.getCatalogByServiceId(serviceId);

                    // Convertir el servicio a JSON y agregar el catálogo
                    JsonObject serviceJson = gson.toJsonTree(service).getAsJsonObject();
                    if (catalog != null) {
                        serviceJson.addProperty("catalogoPdfUrl", catalog.getPdfUrl());
                    }

                    responseData.put("data", serviceJson);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Servicio no encontrado");
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", "ID de servicio inválido");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("error", "Error interno del servidor");
        }

        response.getWriter().write(gson.toJson(responseData));
    }

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();
        
        try {
            if (!AuthUtils.checkRole(request, Role.GESTION_SERVICIOS)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }
            
            Map<String, String> jsonParams = GlobalUtils.parseJsonRequest(request);

            int serviceId = Integer.parseInt(request.getPathInfo().substring(1));
            boolean newStatus = Boolean.parseBoolean(jsonParams.get("active"));
            
            if (ServiceDAO.toggleServiceStatus(serviceId, newStatus)) {
                responseData.put("message", "Estado actualizado correctamente");
                responseData.put("newStatus", newStatus);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseData.put("error", "Error al actualizar el estado");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", "ID de servicio inválido");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("error", "Error interno del servidor");
        }
        
        response.getWriter().write(gson.toJson(responseData));
    }
}