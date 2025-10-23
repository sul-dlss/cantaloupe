package edu.illinois.library.cantaloupe.resource;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.illinois.library.cantaloupe.Application;
import edu.illinois.library.cantaloupe.config.Configuration;
import edu.illinois.library.cantaloupe.config.Key;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class PoweredByHeader implements Filter {
    @Autowired
    private Configuration config;
    PoweredByHeader(Configuration config) {
        this.config = config;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Only show the x-powered-by header if configured to do so.
        if (config.getBoolean(Key.HEADERS_POWERED_BY_DISPLAY, true)) {
          ((HttpServletResponse) response).setHeader("X-Powered-By",
                  Application.getName() + "/" + Application.getVersion());
        }
        chain.doFilter(request, response);
    }
}

