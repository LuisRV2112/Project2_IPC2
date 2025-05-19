package com.usac.salondebelleza.controllers;

import com.google.gson.Gson;
import dao.AdPaymentDAO;
import utils.AuthUtils;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import models.Role;
import utils.GlobalUtils;

@WebServlet("/ads/payments/*")
public class AdPaymentController extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> responseData = new HashMap<>();
        
        try {
            if (!AuthUtils.checkRole(request, Role.MARKETING)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            int adId = Integer.parseInt(request.getPathInfo().split("/")[1]);
            Map<String, String> jsonParams = GlobalUtils.parseJsonRequest(request);
            double amount = Double.parseDouble(jsonParams.get("amount"));
            
            if (AdPaymentDAO.recordPayment(adId, amount)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                responseData.put("message", "Payment recorded");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseData.put("error", "Error recording payment");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("error", "Invalid data");
        }
        
        response.getWriter().write(gson.toJson(responseData));
    }
}