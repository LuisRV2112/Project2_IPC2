package com.usac.salondebelleza.controllers;

import com.google.gson.Gson;
import dao.UserInterestDAO;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;
import models.User;

@WebServlet("/interests/*")
public class InterestController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        String interest = request.getParameter("interest");
        
        if (UserInterestDAO.addUserInterest(user.getId(), interest)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        String interest = request.getParameter("interest");
        
        if (UserInterestDAO.removeUserInterest(user.getId(), interest)) {
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");

        // Get the list of interests for the logged-in user
        List<String> interests = UserInterestDAO.getUserInterests(user.getId());

        // Set response type to JSON
        response.setContentType("application/json");

        // Write interests list as JSON array to response
        response.getWriter().write(new Gson().toJson(interests));
    }
}