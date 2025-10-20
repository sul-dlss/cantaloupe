package edu.illinois.library.cantaloupe.resource.iiif.v3;

import edu.illinois.library.cantaloupe.resource.Controller;
import edu.illinois.library.cantaloupe.resource.Request;
import edu.illinois.library.cantaloupe.resource.TemplateVariables;
import edu.illinois.library.cantaloupe.resource.ThymeleafRepresentation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>Handles the IIIF Image API 3.x landing page.</p>
 *
 * <p>This is a convenience feature that is out of the Image API's scope.</p>
 */
public class LandingResource extends Controller {

    public LandingResource(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        }

    @Override
    public void doGet(Request request) throws Exception {
        renderHtml("/iiif_3_landing.html", TemplateVariables.getDefault(request));
    }
}
