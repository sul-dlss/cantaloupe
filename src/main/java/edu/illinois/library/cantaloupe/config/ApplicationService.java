package edu.illinois.library.cantaloupe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

/**
 * Example service showing how to inject Configuration instead of using
 * Configuration.getInstance() singleton pattern.
 *
 * This replaces static methods in Application.java that used Configuration.getInstance().
 */
@Service
public class ApplicationService {

    private final Configuration configuration;

    @Autowired
    public ApplicationService(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * @return Temp path from configuration, replacing Application.getTempPath()
     */
    public Path getTempPath() {
        final String pathStr = configuration.getString(Key.TEMP_PATHNAME, "");
        if (!pathStr.isEmpty()) {
            return Path.of(pathStr);
        }
        return Path.of(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Check if cache worker is enabled
     */
    public boolean isCacheWorkerEnabled() {
        return configuration.getBoolean(Key.CACHE_WORKER_ENABLED, false);
    }

    /**
     * Get cache worker interval
     */
    public int getCacheWorkerInterval() {
        return configuration.getInt(Key.CACHE_WORKER_INTERVAL, -1);
    }

    /**
     * Example of how other configuration values can be accessed through DI
     */
    public long getMaxPixels() {
        return configuration.getLong(Key.MAX_PIXELS, 0);
    }

    /**
     * Get HTTP port configuration
     */
    public int getHttpPort() {
        return configuration.getInt(Key.HTTP_PORT, 8182);
    }

    /**
     * Check if HTTP is enabled
     */
    public boolean isHttpEnabled() {
        return configuration.getBoolean(Key.HTTP_ENABLED, true);
    }
}
