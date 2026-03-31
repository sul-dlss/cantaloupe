package edu.illinois.library.cantaloupe.resource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class LandingResource extends Controller {
    public LandingResource(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    public void doGet(Request request) throws Exception {
        response.setHeader("Cache-Control", "public, max-age=" + Integer.MAX_VALUE);
        renderHtml("/landing.html", TemplateVariables.getDefault(request));
    }
}
