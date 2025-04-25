package controllers;

import dao.ClientProfileDAO;
import dao.UserInterestDAO;
import models.ClientProfile;
import com.google.gson.Gson;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.*;
import models.User;

@WebServlet("/profile/*")
@MultipartConfig
public class ProfileController extends HttpServlet {
    private final Gson gson = new Gson();

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
        profile.setUserId(user.getId());
        profile.setPhotoUrl(request.getParameter("photoUrl"));
        profile.setDescription(request.getParameter("description"));
        profile.setHobbies(request.getParameter("hobbies"));
        
        if (ClientProfileDAO.createOrUpdateProfile(profile)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}