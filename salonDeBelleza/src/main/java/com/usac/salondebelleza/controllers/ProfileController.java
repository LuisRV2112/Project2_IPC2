package com.usac.salondebelleza.controllers;

import dao.ClientProfileDAO;
import dao.UserInterestDAO;
import models.ClientProfile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import models.User;
import utils.GlobalUtils;
import utils.LocalDateTimeAdapter;

@WebServlet("/profile/*")
@MultipartConfig
public class ProfileController extends HttpServlet {
    private final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .create();
    

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        Map<String, Object> data = new HashMap<>();
        
        data.put("profile", ClientProfileDAO.getProfile(user.getId()));
        data.put("interests", UserInterestDAO.getUserInterests(user.getId()));
        data.put("suggestedInterests", UserInterestDAO.getCommonInterests());
        
        response.getWriter().write(gson.toJson(data));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        ClientProfile profile = new ClientProfile();
        Map<String, String> data = GlobalUtils.parseJsonRequest(request);
        profile.setUserId(user.getId());
        profile.setName(data.get("name"));
        profile.setPhotoUrl(data.get("photoUrl"));
        profile.setDescription(data.get("description"));
        profile.setHobbies(data.get("hobbies"));
        
        if (ClientProfileDAO.createOrUpdateProfile(profile)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}