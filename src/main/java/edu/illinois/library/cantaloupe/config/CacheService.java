package edu.illinois.library.cantaloupe.config;

import edu.illinois.library.cantaloupe.cache.DerivativeCache;
import edu.illinois.library.cantaloupe.cache.SourceCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Spring service that manages cache instances, replacing the static methods
 * in CacheFactory with dependency-injected alternatives.
 */
@Service
public class CacheService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);

    private final Configuration configuration;
    private DerivativeCache derivativeCache;
    private SourceCache sourceCache;

    @Autowired
    public CacheService(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * @return The shared derivative cache instance, or empty if a derivative
     *         cache is not available.
     */
    public Optional<DerivativeCache> getDerivativeCache() {
        DerivativeCache cache = null;

        if (isDerivativeCacheEnabled()) {
            final String unqualifiedName = configuration.getString(Key.DERIVATIVE_CACHE, "");

            if (!unqualifiedName.isEmpty()) {
                final String qualifiedName = getQualifiedName(unqualifiedName);
                cache = derivativeCache;
                if (cache == null ||
                        !cache.getClass().getName().equals(qualifiedName)) {
                    synchronized (this) {
                        if (cache == null ||
                                !cache.getClass().getName().equals(qualifiedName)) {
                            LOGGER.trace("getDerivativeCache(): " +
                                    "implementation changed; creating a new " +
                                    "instance");
                            try {
                                @SuppressWarnings("unchecked")
                                Class<DerivativeCache> implClass =
                                        (Class<DerivativeCache>) Class.forName(qualifiedName);
                                cache = implClass.getDeclaredConstructor().newInstance();
                                derivativeCache = cache;
                                LOGGER.info("Using {} for the derivative cache",
                                        cache.getClass().getSimpleName());
                            } catch (Exception e) {
                                LOGGER.error("getDerivativeCache(): {}", e.getMessage(), e);
                            }
                        } else {
                            cache = derivativeCache;
                        }
                    }
                }
            }
        }
        return Optional.ofNullable(cache);
    }

    /**
     * @return The shared source cache instance, or empty if a source cache
     *         is not available.
     */
    public Optional<SourceCache> getSourceCache() {
        SourceCache cache = null;

        final String unqualifiedName = configuration.getString(Key.SOURCE_CACHE, "");

        if (!unqualifiedName.isEmpty()) {
            final String qualifiedName = getQualifiedName(unqualifiedName);
            cache = sourceCache;
            if (cache == null ||
                    !cache.getClass().getName().equals(qualifiedName)) {
                synchronized (this) {
                    if (cache == null ||
                            !cache.getClass().getName().equals(qualifiedName)) {
                        LOGGER.trace("getSourceCache(): " +
                                "implementation changed; creating a new " +
                                "instance");
                        try {
                            @SuppressWarnings("unchecked")
                            Class<SourceCache> implClass =
                                    (Class<SourceCache>) Class.forName(qualifiedName);
                            cache = implClass.getDeclaredConstructor().newInstance();
                            sourceCache = cache;
                            LOGGER.info("Using {} for the source cache",
                                    cache.getClass().getSimpleName());
                        } catch (Exception e) {
                            LOGGER.error("getSourceCache(): {}", e.getMessage(), e);
                        }
                    } else {
                        cache = sourceCache;
                    }
                }
            }
        }
        return Optional.ofNullable(cache);
    }

    private boolean isDerivativeCacheEnabled() {
        return configuration.getBoolean(Key.DERIVATIVE_CACHE_ENABLED, false);
    }

    /**
     * @param unqualifiedName Unqualified class name.
     * @return Qualified class name.
     */
    private String getQualifiedName(String unqualifiedName) {
        return CacheService.class.getPackage().getName().replace(".config", ".cache") +
               "." + unqualifiedName;
    }

    /**
     * Shuts down any initialized caches.
     */
    public synchronized void shutdown() {
        if (derivativeCache != null) {
            derivativeCache.shutdown();
        }
        if (sourceCache != null) {
            sourceCache.shutdown();
        }
    }
}
