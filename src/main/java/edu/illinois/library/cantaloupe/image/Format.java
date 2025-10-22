package edu.illinois.library.cantaloupe.image;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>Image/file format.</p>
 *
 * <p>Instances are immutable.</p>
 *
 * @see FormatRegistry
 */
public final class Format implements Comparable<Format> {

    /**
     * Represents an unknown format.
     */
    public static final Format UNKNOWN = new Format(
            "unknown",
            "Unknown",
            List.of("unknown/unknown"),
            List.of("unknown"),
            true,
            false,
            false);

    @JsonProperty
    private List<String> extensions;
    @JsonProperty
    private List<String> mediaTypes;
    @JsonProperty
    private String key;
    @JsonProperty
    private String name;
    @JsonProperty("raster")
    private boolean isRaster;
    @JsonProperty("video")
    private boolean isVideo;
    @JsonProperty
    private boolean supportsTransparency;

    /**
     * @return All known formats.
     */
    // public static Set<Format> all() {
    //     return null;//FormatRegistry.allFormats();
    // }

    /**
     * @param key One of the keys in {@literal formats.yml}.
     * @return    Instance corresponding to the given argument, or {@code null}
     *            if no such format exists.
     */
    // public static Format get(String key) {
    //     return null; //FormatRegistry.formatWithKey(key);
    // }

    /**
     * No-op constructor needed by Jackson.
     */
    Format() {}

    Format(String key,
           String name,
           List<String> mediaTypes,
           List<String> extensions,
           boolean isRaster,
           boolean isVideo,
           boolean supportsTransparency) {
        this();
        this.key                  = key;
        this.name                 = name;
        this.mediaTypes           = mediaTypes;
        this.extensions           = extensions;
        this.isRaster             = isRaster;
        this.isVideo              = isVideo;
        this.supportsTransparency = supportsTransparency;
    }

    /**
     * Compares by case-insensitive {@link #getName() name}.
     */
    @Override
    public int compareTo(Format o) {
        return getName().compareToIgnoreCase(o.getName());
    }

    /**
     * @param obj Object to compare.
     * @return    Whether the given object's {@link #getKey() key} is equal to
     *            that of the instance.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Format) {
            Format other = (Format) obj;
            return other.key.equals(key);
        }
        return super.equals(obj);
    }

    /**
     * @return All extensions associated with this format.
     * @see #getPreferredExtension()
     */
    public List<String> getExtensions() {
        return extensions;
    }

    /**
     * @return Unique format key, used internally to identify formats but not
     *         relevant outside of the application.
     * @see #getName()
     */
    public String getKey() {
        return key;
    }

    /**
     * @return All media types associated with this format.
     * @see #getPreferredMediaType()
     */
    public List<MediaType> getMediaTypes() {
        return mediaTypes.stream().map(MediaType::new).
                collect(Collectors.toUnmodifiableList());
    }

    /**
     * @return Human-readable name.
     * @see #getKey()
     */
    public String getName() {
        return name;
    }

    /**
     * @return The most appropriate extension for the format.
     */
    public String getPreferredExtension() {
        return getExtensions().get(0);
    }

    /**
     * @return The most appropriate media type for the format.
     */
    public MediaType getPreferredMediaType() {
        return getMediaTypes().get(0);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public boolean isRaster() {
        return isRaster;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public boolean supportsTransparency() {
        return supportsTransparency;
    }

    /**
     * @return Unmodifiable map with {@code extension} and {@code media_type}
     *         keys corresponding to string values.
     */
    public Map<String,Object> toMap() {
        return Map.of(
                "extension", getPreferredExtension(),
                "media_type", getPreferredMediaType().toString());
    }

    /**
     * @return Preferred extension.
     */
    @Override
    public String toString() {
        return getPreferredExtension();
    }

}
