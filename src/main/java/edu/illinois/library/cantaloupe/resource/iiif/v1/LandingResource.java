package edu.illinois.library.cantaloupe.resource.iiif.v1;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import edu.illinois.library.cantaloupe.resource.Controller;
import edu.illinois.library.cantaloupe.resource.Request;
import edu.illinois.library.cantaloupe.resource.TemplateVariables;

/**
 * Handles the IIIF Image API 1.x landing page.
 */
public class LandingResource extends Controller {
    public LandingResource(HttpServletRequest request, HttpServletResponse response) {
                super(request, response);
        }

    @Override
    public void doGet(Request request) throws Exception {
        renderHtml("/iiif_1_landing.html", TemplateVariables.getDefault(request));
    }

}
