package edu.illinois.library.cantaloupe.resource;

import edu.illinois.library.cantaloupe.config.ConfigurationFactory;
import edu.illinois.library.cantaloupe.resource.admin.AdminResource;
import edu.illinois.library.cantaloupe.resource.api.TaskResource;
import edu.illinois.library.cantaloupe.resource.api.TasksResource;
import edu.illinois.library.cantaloupe.resource.health.HealthResource;
import edu.illinois.library.cantaloupe.test.BaseTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

class RouteTest extends BaseTest {
   RouteSet routeSet = new RouteSet();

   @BeforeEach
    public void before() throws Exception {
        routeSet.build();
    }

    @Test
    void testForPathWithRootRoutes() {
        Route route = routeSet.forPath("");
        assertEquals(LandingResource.class, route.getResource());

        route = routeSet.forPath("/");
        assertEquals(LandingResource.class, route.getResource());
    }

    @Test
    void testForPathWithAdminRoutes() {
        Route route = routeSet.forPath(RouteSet.ADMIN_CONFIG_PATH);
        assertEquals(edu.illinois.library.cantaloupe.resource.admin.ConfigurationResource.class,
                route.getResource());

        route = routeSet.forPath(RouteSet.ADMIN_PATH);
        assertEquals(AdminResource.class, route.getResource());

        route = routeSet.forPath(RouteSet.ADMIN_STATUS_PATH);
        assertEquals(edu.illinois.library.cantaloupe.resource.admin.StatusResource.class,
                route.getResource());
    }

    @Test
    void testForPathWithConfigurationRoute() {
        Route route = routeSet.forPath(RouteSet.CONFIGURATION_PATH);
        assertEquals(edu.illinois.library.cantaloupe.resource.api.ConfigurationResource.class,
                route.getResource());
    }

    @Test
    void testForPathWithHealthRoute() {
        Route route = routeSet.forPath(RouteSet.HEALTH_PATH);
        assertEquals(HealthResource.class, route.getResource());
    }

    @Test
    void testForPathWithStatusRoute() {
        Route route = routeSet.forPath(RouteSet.STATUS_PATH);
        assertEquals(edu.illinois.library.cantaloupe.resource.api.StatusResource.class,
                route.getResource());
    }

    @Test
    void testForPathWithTasksRoutes() {
        Route route = routeSet.forPath(RouteSet.TASKS_PATH);
        assertEquals(TasksResource.class, route.getResource());

        route = routeSet.forPath(RouteSet.TASKS_PATH + "/0bef-234a");
        assertEquals(TaskResource.class, route.getResource());
        assertEquals("0bef-234a", route.getPathArguments().get(0));
    }

    @Test
    void testForPathWithRootIIIFRoute() {
        Route route = routeSet.forPath("/iiif/");
        assertEquals(TrailingSlashRemovingResource.class, route.getResource());
    }

    @Test
    void testForPathWithIIIFv3Routes() {
        Route route = routeSet.forPath(RouteSet.IIIF_3_PATH + "/0bef-234a/info.json");
        assertEquals(edu.illinois.library.cantaloupe.resource.iiif.v3.InformationResource.class,
                route.getResource());
        assertEquals("0bef-234a", route.getPathArguments().get(0));

        route = routeSet.forPath(RouteSet.IIIF_3_PATH + "/0bef-234a");
        assertEquals(edu.illinois.library.cantaloupe.resource.iiif.v3.IdentifierResource.class,
                route.getResource());
        assertEquals("0bef-234a", route.getPathArguments().get(0));

        route = routeSet.forPath(RouteSet.IIIF_3_PATH + "/0bef-234a/0,0,100,100/max/0/default.jpg");
        assertEquals(edu.illinois.library.cantaloupe.resource.iiif.v3.ImageResource.class,
                route.getResource());
        assertEquals("0bef-234a", route.getPathArguments().get(0));
        assertEquals("0,0,100,100", route.getPathArguments().get(1));
        assertEquals("max", route.getPathArguments().get(2));
        assertEquals("0", route.getPathArguments().get(3));
        assertEquals("default", route.getPathArguments().get(4));
        assertEquals("jpg", route.getPathArguments().get(5));
    }

    @Test
    void testForPathWithIIIFv2Routes() {
        Route route = routeSet.forPath(RouteSet.IIIF_2_PATH + "/0bef-234a/info.json");
        assertEquals(edu.illinois.library.cantaloupe.resource.iiif.v2.InformationResource.class,
                route.getResource());
        assertEquals("0bef-234a", route.getPathArguments().get(0));

        route = routeSet.forPath(RouteSet.IIIF_2_PATH + "/0bef-234a");
        assertEquals(edu.illinois.library.cantaloupe.resource.iiif.v2.IdentifierResource.class,
                route.getResource());
        assertEquals("0bef-234a", route.getPathArguments().get(0));

        route = routeSet.forPath(RouteSet.IIIF_2_PATH + "/0bef-234a/0,0,100,100/max/0/default.jpg");
        assertEquals(edu.illinois.library.cantaloupe.resource.iiif.v2.ImageResource.class,
                route.getResource());
        assertEquals("0bef-234a", route.getPathArguments().get(0));
        assertEquals("0,0,100,100", route.getPathArguments().get(1));
        assertEquals("max", route.getPathArguments().get(2));
        assertEquals("0", route.getPathArguments().get(3));
        assertEquals("default", route.getPathArguments().get(4));
        assertEquals("jpg", route.getPathArguments().get(5));
    }

    @Test
    void testForPathWithIIIFv1Routes() {
        Route route = routeSet.forPath(RouteSet.IIIF_1_PATH + "/0bef-234a/info.json");
        assertEquals(edu.illinois.library.cantaloupe.resource.iiif.v1.InformationResource.class,
                route.getResource());
        assertEquals("0bef-234a", route.getPathArguments().get(0));

        route = routeSet.forPath(RouteSet.IIIF_1_PATH + "/0bef-234a");
        assertEquals(edu.illinois.library.cantaloupe.resource.iiif.v1.IdentifierResource.class,
                route.getResource());
        assertEquals("0bef-234a", route.getPathArguments().get(0));

        route = routeSet.forPath(RouteSet.IIIF_1_PATH + "/0bef-234a/0,0,100,100/max/0/native.jpg");
        assertEquals(edu.illinois.library.cantaloupe.resource.iiif.v1.ImageResource.class,
                route.getResource());
        assertEquals("0bef-234a", route.getPathArguments().get(0));
        assertEquals("0,0,100,100", route.getPathArguments().get(1));
        assertEquals("max", route.getPathArguments().get(2));
        assertEquals("0", route.getPathArguments().get(3));
        assertEquals("native", route.getPathArguments().get(4));
        assertEquals("jpg", route.getPathArguments().get(5));

        route = routeSet.forPath(RouteSet.IIIF_1_PATH + "/0bef-234a/0,0,100,100/max/0/native");
        assertEquals(edu.illinois.library.cantaloupe.resource.iiif.v1.ImageResource.class,
                route.getResource());
        assertEquals("0bef-234a", route.getPathArguments().get(0));
        assertEquals("0,0,100,100", route.getPathArguments().get(1));
        assertEquals("max", route.getPathArguments().get(2));
        assertEquals("0", route.getPathArguments().get(3));
        assertEquals("native", route.getPathArguments().get(4));
    }

    @Test
    void testForPathWithInvalidRoute() {
        Route route = routeSet.forPath("/notfound");
        assertNull(route);
    }

}