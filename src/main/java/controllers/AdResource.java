package controllers;

import dao.AdDAO;
import dao.UserInterestDAO;
import models.Ad;
import models.Role;
import models.User;
import utils.AuthUtils;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/ads")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdResource {

    @Context
    private HttpServletRequest request;

    // DTO para recibir datos del POST
    public static class AdRequest {
        private String type;
        private String contentUrl;
        private String startDate;
        private String endDate;
        private String category;

        // Getters y Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getContentUrl() { return contentUrl; }
        public void setContentUrl(String contentUrl) { this.contentUrl = contentUrl; }
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }

    @POST
    @RolesAllowed("MARKETING")
    public Response createAd(AdRequest adRequest) {
        // Verificar rol
        if (!AuthUtils.checkRole(request, Role.MARKETING)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("error", "Acceso denegado")).build();
        }

        try {
            Ad ad = new Ad();
            ad.setType(adRequest.getType());
            ad.setContentUrl(adRequest.getContentUrl());
            ad.setStartDate(LocalDate.parse(adRequest.getStartDate()));
            ad.setEndDate(LocalDate.parse(adRequest.getEndDate()));
            ad.setCategory(adRequest.getCategory());

            if (AdDAO.createAd(ad)) {
                return Response.status(Response.Status.CREATED).entity(ad).build();
            } else {
                return Response.serverError()
                        .entity(Map.of("error", "Error al crear el anuncio")).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage())).build();
        }
    }

    @GET
    //@RolesAllowed("CLIENTE")
    public Response getAds() {
        // Verificar rol
        if (!AuthUtils.checkRole(request, Role.CLIENTE)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("error", "Acceso denegado")).build();
        }

        try {
            User user = (User) request.getSession().getAttribute("user");
            int userId = user.getId();
            List<String> interests = UserInterestDAO.getUserInterests(userId);
            List<Ad> ads = new ArrayList<>();

            for (String interest : interests) {
                ads.addAll(AdDAO.getActiveAdsByCategory(interest));
            }

            return Response.ok(Map.of("data", ads)).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity(Map.of("error", "Error al obtener anuncios")).build();
        }
    }
}