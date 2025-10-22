package edu.illinois.library.cantaloupe.resource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteSet {

    public static final String ADMIN_PATH         = "/admin";
    public static final String ADMIN_CONFIG_PATH  = "/admin/configuration";
    public static final String ADMIN_STATUS_PATH  = "/admin/status";
    public static final String CONFIGURATION_PATH = "/configuration";
    public static final String HEALTH_PATH        = "/health";
    public static final String IIIF_1_PATH        = "/iiif/1";
    public static final String IIIF_2_PATH        = "/iiif/2";
    public static final String IIIF_3_PATH        = "/iiif/3";
    public static final String STATUS_PATH        = "/status";
    public static final String TASKS_PATH         = "/tasks";

    private static class RouteEntry {
        Class<? extends AbstractResource> resource;
        Class<? extends Request> request;

        RouteEntry(Class<? extends AbstractResource> resource, Class<? extends Request> request) {
            this.request = request;
            this.resource = resource;
        }

        public Class<? extends AbstractResource> getResourceClass() {
            return resource;
        }

        public Class<? extends Request> getRequestClass() {
            return request;
        }       
    }

    /**
     * N.B.: the {@link LinkedHashMap} preserves order as each mapping will be
     * checked sequentially and the first match used.
     */
    private Map<Pattern,RouteEntry> mappings;

    public void build() {
        mappings = new LinkedHashMap<>();
        // N.B.: Regex groups are used to extract the URI path arguments.
        mappings.put(Pattern.compile("\\A\\z"),
                new RouteEntry(LandingResource.class, Request.class));
        mappings.put(Pattern.compile("^/$"),
                new RouteEntry(LandingResource.class, Request.class));
        mappings.put(Pattern.compile("/$"),
                new RouteEntry(TrailingSlashRemovingResource.class, Request.class));

        // IIIF Image API v3 routes
        mappings.put(Pattern.compile("^" + IIIF_3_PATH + "$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.iiif.v3.LandingResource.class, IIIFRequest.class));
        mappings.put(Pattern.compile("^" + IIIF_3_PATH + "/([^/]+)/info\\.json$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.iiif.v3.InformationResource.class, IIIFRequest.class));
        mappings.put(Pattern.compile("^" + IIIF_3_PATH + "/([^/]+)$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.iiif.v3.IdentifierResource.class, IIIFRequest.class));
        mappings.put(Pattern.compile("^" + IIIF_3_PATH + "/([^/]+)/([^/]+)/([^/]+)/([^/]+)/([^/]+)\\.([^/]+)$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.iiif.v3.ImageResource.class, IIIFRequest.class));

        // IIIF Image API v2 routes
        mappings.put(Pattern.compile("^" + IIIF_2_PATH + "$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.iiif.v2.LandingResource.class, IIIFRequest.class));
        mappings.put(Pattern.compile("^" + IIIF_2_PATH + "/([^/]+)/info\\.json$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.iiif.v2.InformationResource.class, IIIFRequest.class));
        mappings.put(Pattern.compile("^" + IIIF_2_PATH + "/([^/]+)$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.iiif.v2.IdentifierResource.class, IIIFRequest.class));
        mappings.put(Pattern.compile("^" + IIIF_2_PATH + "/([^/]+)/([^/]+)/([^/]+)/([^/]+)/([^/]+)\\.([^/]+)$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.iiif.v2.ImageResource.class, IIIFRequest.class));

        // IIIF Image API v1 routes
        mappings.put(Pattern.compile("^" + IIIF_1_PATH + "$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.iiif.v1.LandingResource.class, IIIFRequest.class));
        mappings.put(Pattern.compile("^" + IIIF_1_PATH + "/([^/]+)/info\\.json$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.iiif.v1.InformationResource.class, IIIFRequest.class));
        mappings.put(Pattern.compile("^" + IIIF_1_PATH + "/([^/]+)$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.iiif.v1.IdentifierResource.class, IIIFRequest.class));
        mappings.put(Pattern.compile("^" + IIIF_1_PATH + "/([^/]+)/([^/]+)/([^/]+)/([^/]+)/([^/.]+)$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.iiif.v1.ImageResource.class, IIIFRequest.class));
        mappings.put(Pattern.compile("^" + IIIF_1_PATH + "/([^/]+)/([^/]+)/([^/]+)/([^/]+)/([^/.]+)\\.([^/]+)$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.iiif.v1.ImageResource.class, IIIFRequest.class));

        // Control Panel routes
        mappings.put(Pattern.compile("^" + ADMIN_CONFIG_PATH + "$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.admin.ConfigurationResource.class, Request.class));
        mappings.put(Pattern.compile("^" + ADMIN_PATH + "$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.admin.AdminResource.class, Request.class));
        mappings.put(Pattern.compile("^" + ADMIN_STATUS_PATH + "$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.admin.StatusResource.class, Request.class));

        // API routes
        mappings.put(Pattern.compile("^" + CONFIGURATION_PATH + "$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.api.ConfigurationResource.class, Request.class));
        mappings.put(Pattern.compile("^" + HEALTH_PATH + "$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.health.HealthResource.class, Request.class));
        mappings.put(Pattern.compile("^" + STATUS_PATH + "$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.api.StatusResource.class, Request.class));
        mappings.put(Pattern.compile("^" + TASKS_PATH + "$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.api.TasksResource.class, Request.class));
        mappings.put(Pattern.compile("^" + TASKS_PATH + "/([^/]+)$"),
                new RouteEntry(edu.illinois.library.cantaloupe.resource.api.TaskResource.class, Request.class));
    }



    /**
     * @param path URI path relative to the context path.
     * @return     Route corresponding to the given path, or {@code null} if
     *             there is no match.
     */
    public Route forPath(String path) {
        for (var entry : mappings.entrySet()) {
            final Pattern pattern = entry.getKey();
            final Matcher matcher = pattern.matcher(path);
            if (matcher.find()) {
                RouteEntry routeEntry = entry.getValue();
                final Route route = new Route(routeEntry.getResourceClass(), routeEntry.getRequestClass());
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    route.getPathArguments().add(matcher.group(i));
                }
                return route;
            }
        }
        return null;
    }
    
}
