package com.usac.salondebelleza.controllers;

import dao.AdImpressionDAO;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/record-impression")
public class AdTrackerServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        int adId = Integer.parseInt(request.getParameter("adId"));
        String pageUrl = request.getParameter("pageUrl");
        AdImpressionDAO.recordImpression(adId, pageUrl);
    }
}