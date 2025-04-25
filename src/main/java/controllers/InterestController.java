package controllers;

import dao.UserInterestDAO;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
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
}