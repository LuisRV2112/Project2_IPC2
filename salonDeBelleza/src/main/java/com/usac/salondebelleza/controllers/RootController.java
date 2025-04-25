package com.usac.salondebelleza.controllers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/")
public class RootController {

    @GET
    public Response getRoot() {
        return Response.ok("¡Bienvenido al Salón de Belleza! Accede a la documentación en <a href='/salonDeBelleza/webjars/swagger-ui/5.10.3/index.html?url=/resources/openapi.json'>Swagger UI</a>")
                .type("text/html")
                .build();
    }
}