package edu.illinois.library.cantaloupe.processor.codec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.illinois.library.cantaloupe.image.Format;
import edu.illinois.library.cantaloupe.operation.Encode;
import edu.illinois.library.cantaloupe.processor.OutputFormatException;
import edu.illinois.library.cantaloupe.test.BaseTest;

public class ImageWriterFactoryTest extends BaseTest {

    private ImageWriterFactory instance;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        instance = new ImageWriterFactory();
    }

    @Test
    void testSupportedFormats() {
        Set<Format> outputFormats = Set.of(
                formatRegistry.formatWithKey("gif"), formatRegistry.formatWithKey("jpg"), formatRegistry.formatWithKey("png"), formatRegistry.formatWithKey("tif"));
        assertEquals(outputFormats, ImageWriterFactory.supportedFormats());
    }

    @Test
    void testNewImageWriter() throws Exception {
        assertNotNull(instance.newImageWriter(new Encode(formatRegistry.formatWithKey("jpg"))));
    }

    @Test
    void testNewImageWriterWithUnsupportedFormat() {
        assertThrows(OutputFormatException.class,
                () -> instance.newImageWriter(new Encode(Format.UNKNOWN)));
    }

}
