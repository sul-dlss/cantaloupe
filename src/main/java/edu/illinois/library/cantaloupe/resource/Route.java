package edu.illinois.library.cantaloupe.resource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Associates a URI path pattern with an {@link AbstractResource}
 * implementation.
 *
 * @since 4.1
 */
public final class Route {

    private Class<? extends AbstractResource> resource;
    private Class<? extends Request> request;

    private final List<String> pathArguments = new ArrayList<>();



    /**
     * <p>Returns a list of non-decoded URI path components that are considered
     * arguments, as extracted from the string argument to {@link
     * #forPath(String)}. For example, this URI path has six arguments:</p>
     *
     * <p>{@code /iiif/2/[identifier]/[region]/[size]/[rotation]/[quality].[format]}</p>
     */
    List<String> getPathArguments() {
        return pathArguments;
    }

    Route(Class<? extends AbstractResource> resource, Class<? extends Request> request) {
        this.resource = resource;
        this.request = request;
    }

    /**
     * @return Resource the instance "connects" to.
     */
    Class<? extends AbstractResource> getResource() {
        return resource;
    }

    /**
     * @return Request the users intent
     */
    Class<? extends Request> getRequest() {
        return request;
    }
}
