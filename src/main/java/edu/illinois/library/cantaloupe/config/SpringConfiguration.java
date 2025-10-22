package edu.illinois.library.cantaloupe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Spring configuration class that provides the Cantaloupe Configuration
 * as a Spring-managed bean, enabling dependency injection instead of
 * using the singleton pattern with Configuration.getInstance().
 */
@Component
@org.springframework.context.annotation.Configuration
public class SpringConfiguration {

    /**
     * Provides the global Configuration instance as a Spring bean.
     * This allows other Spring components to inject the Configuration
     * instead of calling Configuration.getInstance().
     *
     * @return The global Configuration instance
     */
    @Bean
    @Primary
    public Configuration configuration() {
        // Use the existing factory to get the singleton instance
        // This maintains compatibility with existing non-Spring code
        return ConfigurationFactory.getInstance();
    }
}
