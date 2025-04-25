package com.usac.salondebelleza.controllers;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@WebServlet("/upload-photo")
@MultipartConfig
public class FileUploadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part filePart = request.getPart("photo");
        String uploadPath = getServletContext().getRealPath("/uploads");
        String fileName = UUID.randomUUID() + ".jpg";
        
        Files.createDirectories(Paths.get(uploadPath));
        filePart.write(uploadPath + File.separator + fileName);
        
        String photoUrl = "/uploads/" + fileName;
        // Guardar photoUrl en el perfil del usuario
    }
}