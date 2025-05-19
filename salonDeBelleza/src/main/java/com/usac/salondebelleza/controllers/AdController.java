package com.usac.salondebelleza.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import dao.AdDAO;
import dao.UserInterestDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Ad;
import models.Role;
import models.User;
import utils.AuthUtils;
import utils.LocalDateAdapter;
import utils.LocalDateTimeAdapter;

@WebServlet(name = "AdController", urlPatterns = {"/ads", "/ads/*"})
public class AdController extends HttpServlet {
private final Gson gson = new GsonBuilder()
    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
    .create();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();

        try {
            if (!AuthUtils.checkRole(request, Role.MARKETING)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }

            JsonObject jsonObject = gson.fromJson(request.getReader(), JsonObject.class);

            Ad ad = new Ad();
            ad.setType(jsonObject.get("type").getAsString());
            ad.setContentUrl(jsonObject.get("contentUrl").getAsString());
            ad.setStartDate(LocalDate.parse(jsonObject.get("startDate").getAsString()));
            ad.setEndDate(LocalDate.parse(jsonObject.get("endDate").getAsString()));
            ad.setCategory(jsonObject.get("category").getAsString());

            if (AdDAO.createAd(ad)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                ad.setPaymentStatus("pendiente");
                responseData.put("data", ad);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseData.put("error", "Error al crear el anuncio");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", e.getMessage());
        }
        
        try {
            String res = gson.toJson(responseData);
            response.getWriter().write(res);
        } catch (JsonIOException | IOException e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                response.resetBuffer();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                try {
                    response.getWriter().write("{\"error\":\"Failed to generate JSON response: " + 
                        e.getMessage().replace("\"", "'") + "\"}");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();

        try {
            if (!AuthUtils.checkRole(request, Role.CLIENTE) && !AuthUtils.checkRole(request, Role.MARKETING)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }
            List<Ad> ads = new ArrayList<>();
            if(AuthUtils.checkRole(request, Role.MARKETING)){
                ads = AdDAO.getActiveAds();
            }else{
                HttpSession session = request.getSession();
                User user = (User) session.getAttribute("user");
                int userId = user.getId();

                List<String> interests = UserInterestDAO.getUserInterests(userId);

                for (String interest : interests) {
                    ads.addAll(AdDAO.getActiveAdsByCategory(interest));
                }
            }



            responseData.put("data", ads);
            response.getWriter().write(gson.toJson(responseData));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("error", "Error al obtener anuncios");
            response.getWriter().write(gson.toJson(responseData));
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();

        try {
            if (!AuthUtils.checkRole(request, Role.MARKETING)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }

            // Obtener ID del anuncio de la URL
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de anuncio requerido");
                return;
            }

            int adId = Integer.parseInt(pathInfo.split("/")[1]);
            JsonObject jsonObject = gson.fromJson(request.getReader(), JsonObject.class);

            // Obtener el anuncio existente
            Ad existingAd = AdDAO.getAdById(adId);
            if (existingAd == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Anuncio no encontrado");
                return;
            }

            // Actualizar campos
            if (jsonObject.has("type")) {
                existingAd.setType(jsonObject.get("type").getAsString());
            }
            if (jsonObject.has("contentUrl")) {
                existingAd.setContentUrl(jsonObject.get("contentUrl").getAsString());
            }
            if (jsonObject.has("startDate")) {
                existingAd.setStartDate(LocalDate.parse(jsonObject.get("startDate").getAsString()));
            }
            if (jsonObject.has("endDate")) {
                existingAd.setEndDate(LocalDate.parse(jsonObject.get("endDate").getAsString()));
            }
            if (jsonObject.has("category")) {
                existingAd.setCategory(jsonObject.get("category").getAsString());
            }
            if (jsonObject.has("isActive")) {
                existingAd.setIsActive(jsonObject.get("isActive").getAsBoolean());
            }

            if (AdDAO.updateAd(existingAd)) {
                responseData.put("data", existingAd);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseData.put("error", "Error al actualizar el anuncio");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", "ID de anuncio inválido");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", e.getMessage());
        }

        response.getWriter().write(gson.toJson(responseData));
    }
    
        @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();

        try {
            if (!AuthUtils.checkRole(request, Role.MARKETING)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
                return;
            }

            // Obtener ID del anuncio de la URL
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de anuncio requerido");
                return;
            }

            int adId = Integer.parseInt(pathInfo.split("/")[1]);

            // Verificar si el anuncio existe
            if (!AdDAO.adExists(adId)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Anuncio no encontrado");
                return;
            }

            if (AdDAO.deleteAd(adId)) {
                responseData.put("message", "Anuncio eliminado correctamente");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseData.put("error", "Error al eliminar el anuncio");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", "ID de anuncio inválido");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("error", e.getMessage());
        }

        response.getWriter().write(gson.toJson(responseData));
    }
}