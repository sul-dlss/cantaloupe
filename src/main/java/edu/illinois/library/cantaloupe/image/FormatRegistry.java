package edu.illinois.library.cantaloupe.image;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import edu.illinois.library.cantaloupe.config.Configuration;
import edu.illinois.library.cantaloupe.source.Source;

/**
 * <p>Provides access to the master registry of {@link Format}s, which is
 * composed of the union of the sets of formats in:</p>
 *
 * <ol>
 *     <li>The bundled {@literal formats.yml} resource;</li>
 *     <li>Any {@literal formats.yml} that happens to be present in either the
 *     same directory as the configuration file, or, if no such file exists,
 *     the current working directory.</li>
 * </ol>
 *
 * @since 5.0
 */
public final class FormatRegistry {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(FormatRegistry.class);

    private static final String FILENAME = "formats.yml";
    private Map<String, Format> formats;
    private Path pathname;

    public FormatRegistry(Path pathname) {
        this.pathname = pathname;
    }

    /**
     * @return Unmodifiable union of all formats in every known {@literal
     *         formats.yml} file.
     */
    public Set<Format> allFormats() {
        if (formats == null) {
            readFormats();
        }
        return Set.copyOf(formats.values());
    }

    /**
     * @param key Format {@link Format#getKey() key}.
     * @return    Format with the given key, or {@code null} if no such format
     *            is {@link #allFormats() registered}.
     */
    public Format formatWithKey(String key) {
        if (formats == null) {
            readFormats();
        }
        return formats.get(key);
    }

    public Format formatForMime(MediaType target) {
        for (Format enumValue : allFormats()) {
            for (MediaType type : enumValue.getMediaTypes()) {
                if (type.equals(target)) {
                    return enumValue;
                }
            }
        }
        return Format.UNKNOWN;
    }

    /**
     * <p>Attempts to infer a format from the given pathname.</p>
     *
     * <p>It is usually more reliable (but also maybe more expensive) to
     * obtain this information from {@link Source#getFormatIterator()}.</p>
     *
     * @param pathname
     * @return The source format corresponding to the given identifier,
     *         assuming that its value will have a recognizable filename
     *         extension. If not, {@link #UNKNOWN} is returned.
     */
    public Format inferFormat(String pathname) {
        String extension = null;
        int i = pathname.lastIndexOf('.');
        if (i > 0) {
            extension = pathname.substring(i + 1);
        }
        if (extension != null) {
            extension = extension.toLowerCase();
            for (Format format : allFormats()) {
                if (format.getExtensions().contains(extension)) {
                    return format;
                }
            }
        }
        return Format.UNKNOWN;
    }

    /**
     * <p>Attempts to infer a format from the given identifier.</p>
     *
     * <p>It is usually more reliable (but also maybe more expensive) to
     * obtain this information from {@link Source#getFormatIterator()}.</p>
     *
     * @param identifier
     * @return The source format corresponding to the given identifier,
     *         assuming that its value will have a recognizable filename
     *         extension. If not, {@link #UNKNOWN} is returned.
     */
    public Format inferFormat(Identifier identifier) {
        return inferFormat(identifier.toString());
    }

    /**
     * @return Format in the {@link FormatRegistry registry} with the given
     *         extension.
     */
    public Format withExtension(String extension) {
        if (extension.startsWith(".")) {
            extension = extension.substring(1);
        }
        final String lcext = extension.toLowerCase();
        return allFormats()
                .stream()
                .filter(f -> f.getExtensions().contains(lcext))
                .findAny()
                .orElse(null);
    }

    /**
     * <p>Reads the available formats from various files according to the class
     * documentation.</p>
     */
    private void readFormats() {
        try {
            formats = readBundledFormats();
            formats.putAll(readUserFormats());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private static Map<String, Format> readBundledFormats() throws IOException {
        try (InputStream is = FormatRegistry.class.getClassLoader().getResourceAsStream(FILENAME)) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();

            TypeReference<Map<String, Format>> ref = new TypeReference<>() {};
            Map<String, Format> formats = mapper.readValue(is, ref);
            LOGGER.debug("Read {} bundled formats: {}",
                    formats.size(),
                    formats.values().stream().map(Format::getKey).collect(Collectors.joining(", ")));
            return formats;
        }
    }

    private Map<String, Format> readUserFormats() throws IOException {
        if (Files.exists(pathname)) {
            LOGGER.trace("Reading user formats from {}", pathname);
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();

            TypeReference<Map<String, Format>> ref = new TypeReference<>() {};
            Map<String, Format> formats = mapper.readValue(pathname.toFile(), ref);
            LOGGER.debug("Read {} user formats: {}",
                    formats.size(),
                    formats.values().stream().map(Format::getKey).collect(Collectors.joining(", ")));
            return formats;
        }
        return Collections.emptyMap();
    }

    public static FormatRegistry buildFromConfig(Configuration config) {
        Optional<Path> configFile = config.getFile();
        Path dir = configFile.isPresent() ?
                configFile.get().getParent() : Paths.get(".");
        return new FormatRegistry(dir.resolve(FILENAME));
    }
}
