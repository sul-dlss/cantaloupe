package edu.illinois.library.cantaloupe.resource.iiif.v3;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import edu.illinois.library.cantaloupe.test.BaseTest;

class OutputFormatTest extends BaseTest {    
    @Test
    void testToFormat() {
        assertEquals(formatRegistry.formatWithKey("jpg"), OutputFormat.JPG.toFormat());
    }

    @Test
    void testToString() {
        for (OutputFormat format : OutputFormat.values()) {
            assertEquals(format.toFormat().getPreferredExtension(),
                    format.toString());
        }
    }

}
