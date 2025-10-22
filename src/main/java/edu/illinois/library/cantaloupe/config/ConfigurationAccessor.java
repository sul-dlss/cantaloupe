package edu.illinois.library.cantaloupe.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Utility class to help with gradual migration from Configuration.getInstance()
 * singleton pattern to Spring dependency injection.
 *
 * This class provides static access to the Spring-managed Configuration bean
 * for classes that cannot yet be converted to full dependency injection.
 *
 * Usage during migration:
 * Instead of: Configuration.getInstance()
 * Use:        ConfigurationAccessor.getConfiguration()
 *
 * This provides a stepping stone during the migration process while maintaining
 * compatibility with existing code that expects static access.
 */
@Component
public class ConfigurationAccessor implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private static Configuration configuration;

    /**
     * Set by Spring during application startup
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        ConfigurationAccessor.applicationContext = context;
        // Cache the configuration bean for performance
        ConfigurationAccessor.configuration = context.getBean(Configuration.class);
    }

    /**
     * Gets the Spring-managed Configuration instance.
     * This method provides static access to the Configuration bean during
     * the migration period from singleton pattern to dependency injection.
     *
     * @return The Spring-managed Configuration instance
     * @throws IllegalStateException if Spring context is not yet initialized
     */
    public static Configuration getConfiguration() {
        if (configuration != null) {
            return configuration;
        }

        if (applicationContext == null) {
            throw new IllegalStateException(
                "Spring ApplicationContext not initialized. " +
                "ConfigurationAccessor can only be used after Spring Boot startup."
            );
        }

        // Fallback to context lookup if cached instance is null
        configuration = applicationContext.getBean(Configuration.class);
        return configuration;
    }

    /**
     * Checks if the Spring context and Configuration are available.
     * Useful for conditional logic during migration.
     *
     * @return true if Configuration can be accessed via Spring DI
     */
    public static boolean isAvailable() {
        return applicationContext != null && configuration != null;
    }

    /**
     * Gets Configuration instance with fallback to singleton.
     * This method provides maximum compatibility during migration by
     * attempting Spring DI first, then falling back to the traditional
     * singleton pattern if Spring is not available.
     *
     * @return Configuration instance from Spring DI or singleton fallback
     */
    public static Configuration getConfigurationWithFallback() {
        if (isAvailable()) {
            return getConfiguration();
        } else {
            // Fall back to the original singleton pattern
            // This ensures compatibility during partial migration
            return ConfigurationFactory.getInstance();
        }
    }

    /**
     * Helper method for migration: replaces Configuration.getInstance() calls
     * with Spring DI when available, singleton otherwise.
     *
     * This method should be used temporarily during migration and removed
     * once all classes are converted to proper dependency injection.
     *
     * @return Configuration instance
     * @deprecated Use proper dependency injection instead
     */
    @Deprecated
    public static Configuration getInstance() {
        return getConfigurationWithFallback();
    }
}
