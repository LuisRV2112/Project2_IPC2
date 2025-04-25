package controllers;

import dao.ServiceDAO;
import models.Service;
import utils.AuthUtils;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import models.Role;

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

            Service service = new Service();
            service.setName(request.getParameter("name"));
            service.setDescription(request.getParameter("description"));
            service.setImageUrl(request.getParameter("imageUrl"));
            service.setDurationMin(Integer.parseInt(request.getParameter("durationMin")));
            service.setPrice(Double.parseDouble(request.getParameter("price")));
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

            service.setName(request.getParameter("name"));
            service.setDescription(request.getParameter("description"));
            service.setImageUrl(request.getParameter("imageUrl"));
            service.setDurationMin(Integer.parseInt(request.getParameter("durationMin")));
            service.setPrice(Double.parseDouble(request.getParameter("price")));

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
                // Listar todos los servicios
                responseData.put("data", ServiceDAO.getAllServices());
            } else {
                // Obtener servicio por ID
                int serviceId = Integer.parseInt(pathInfo.substring(1));
                Service service = ServiceDAO.getServiceById(serviceId);
                
                if (service != null) {
                    responseData.put("data", service);
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

            int serviceId = Integer.parseInt(request.getPathInfo().substring(1));
            boolean newStatus = Boolean.parseBoolean(request.getParameter("active"));
            
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