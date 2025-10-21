package edu.illinois.library.cantaloupe.resource;

import java.io.IOException;
import java.io.PrintWriter;

import edu.illinois.library.cantaloupe.config.Configuration;
import edu.illinois.library.cantaloupe.config.Key;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ErrorWrapper implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            chain.doFilter(request, response); // Continue the filter chain
        } catch (Exception ex) {
            handleError(httpRequest, httpResponse, ex);
            return; // Stop further processing
        }
    }

    private void handleError(HttpServletRequest request,
                             HttpServletResponse response,
                             Throwable t) {
        // Try to use an ErrorResource, which will render an HTML template.
        ErrorResource resource = new ErrorResource(t, request, response);
        try {
            resource.doGET();
        } catch (IllegalClientArgumentException e) {
            handleError(response, e, 400);
        } catch (Throwable t2) {
            handleError(response, t2, 500);
        }
    }

    private void handleError(HttpServletResponse response,
                             Throwable t,
                             int status) {
        response.setStatus(status);
        response.setContentType("text/plain;charset=UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print("Unrecoverable error in " +
                    HandlerServlet.class.getSimpleName());
            if (isPrintingStackTraces()) {
                writer.println(":");
                writer.println("");
                t.printStackTrace(writer);
            }
        } catch (IllegalStateException e) {
            if ("STREAM".equals(e.getMessage())) {
                // This means that something was writing to the response
                // OutputStream but was interrupted, probably by the user
                // terminating the request, and trying to acquire a writer
                // above threw an exception because you aren't allowed to
                // use a writer after you've written to the output stream.
                // Anyway, no big deal, we'll just log it.
                LOGGER.debug("Failed to acquire an error writer after " +
                        "failing to fully write the response. Most " +
                        "likely this was caused by the client closing " +
                        "the connection and is not a problem.");
            }
        } catch (IOException e) {
            LOGGER.error("handleError(): {}", e.getMessage(), e);
        }
    }

    private boolean isPrintingStackTraces() {
        Configuration config = Configuration.getInstance();
        return config.getBoolean(Key.PRINT_STACK_TRACE_ON_ERROR_PAGES, false);
    }
}
