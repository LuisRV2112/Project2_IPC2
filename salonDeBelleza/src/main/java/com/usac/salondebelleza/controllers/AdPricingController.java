package com.usac.salondebelleza.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import dao.AdPricingDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Role;
import utils.AuthUtils;
import utils.LocalDateAdapter;
import utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdPricingController", urlPatterns = {"/ad-pricing", "/ad-pricing/*"})
public class AdPricingController extends HttpServlet {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(java.time.LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();

        try {
            // Solo administradores de marketing pueden cambiar precio
            if (!AuthUtils.checkRole(request, Role.MARKETING) && !AuthUtils.checkRole(request, Role.ADMINISTRADOR)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }

            JsonObject json = gson.fromJson(request.getReader(), JsonObject.class);
            if (!json.has("pricePerDay")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseData.put("error", "El campo pricePerDay es obligatorio");
            } else {
                double price = json.get("pricePerDay").getAsDouble();
                boolean created = AdPricingDAO.insertPrice(price);
                if (created) {
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    responseData.put("message", "Precio agregado correctamente");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    responseData.put("error", "Error al insertar el precio");
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", e.getMessage());
        }
        // Escribir JSON de respuesta
        try {
            response.getWriter().write(gson.toJson(responseData));
        } catch (JsonIOException | IOException e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                response.resetBuffer();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Error al generar respuesta JSON\"}");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();

        try {
            // Clientes y marketing pueden ver precio
            if (!AuthUtils.checkRole(request, Role.CLIENTE) && !AuthUtils.checkRole(request, Role.MARKETING) && !AuthUtils.checkRole(request, Role.ADMINISTRADOR) ) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }

            String pathInfo = request.getPathInfo();
            if (pathInfo != null && pathInfo.equalsIgnoreCase("/history")) {
                // Historial de precios
                List<AdPricingDAO.PricingEntry> history = AdPricingDAO.getPriceHistory();
                responseData.put("history", history);

            } else {
                // Precio actual
                Double currentPrice = AdPricingDAO.getCurrentPrice();
                if (currentPrice != null) {
                    responseData.put("currentPrice", currentPrice);
                } else {
                    responseData.put("message", "No hay precios registrados");
                }
            }
            response.getWriter().write(gson.toJson(responseData));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("error", "Error al obtener precios");
            response.getWriter().write(gson.toJson(responseData));
        }
    }
}
