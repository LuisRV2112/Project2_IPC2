package com.usac.salondebelleza.controllers;

import dao.AvailabilityDAO;
import com.google.gson.Gson;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/availability")
public class AvailabilityController extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();

        try {
            // Obtener los parámetros como query parameters
            int employeeId = Integer.parseInt(request.getParameter("employeeId"));
            int serviceId = Integer.parseInt(request.getParameter("serviceId"));
            LocalDate date = LocalDate.parse(request.getParameter("date"));

            List<String> availableSlots = AvailabilityDAO.getAvailableSlots(employeeId, serviceId, date);

            responseData.put("slots", availableSlots);
            response.getWriter().write(gson.toJson(responseData));
        } catch (IOException | NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", "Parámetros inválidos");
            response.getWriter().write(gson.toJson(responseData));
        }
    }
}
