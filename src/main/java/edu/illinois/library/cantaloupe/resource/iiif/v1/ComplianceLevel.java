package edu.illinois.library.cantaloupe.resource.iiif.v1;

import java.util.HashSet;
import java.util.Set;

import edu.illinois.library.cantaloupe.image.Format;
import edu.illinois.library.cantaloupe.image.FormatRegistry;

/**
 * @see <a href="http://iiif.io/api/image/1.1/compliance.html">Compliance
 * Levels</a>
 */
enum ComplianceLevel {

    LEVEL_0("http://library.stanford.edu/iiif/image-api/1.1/compliance.html#level0"),
    LEVEL_1("http://library.stanford.edu/iiif/image-api/1.1/compliance.html#level1"),
    LEVEL_2("http://library.stanford.edu/iiif/image-api/1.1/compliance.html#level2");

    private String uri;

    /**
     * @return Effective IIIF compliance level corresponding to the given
     * parameters.
     */
    public static ComplianceLevel getLevel(Set<Format> outputFormats, FormatRegistry formatRegistry) {
        ComplianceLevel level = LEVEL_0;
        Set<Format> l1outputFormats = new HashSet<>();
        l1outputFormats.add(formatRegistry.formatWithKey("jpg"));

        Set<Format> l2outputFormats = new HashSet<>();
        l2outputFormats.addAll(l1outputFormats);
        l2outputFormats.add(formatRegistry.formatWithKey("png"));

        if (outputFormats.containsAll(l1outputFormats)) {
            level = LEVEL_1;
            if (outputFormats.containsAll(l2outputFormats)) {
                level = LEVEL_2;
            }
        }
        return level;
    }

    ComplianceLevel(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return this.uri;
    }

}
