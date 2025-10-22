package edu.illinois.library.cantaloupe.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.illinois.library.cantaloupe.image.Info;
import edu.illinois.library.cantaloupe.operation.Encode;
import edu.illinois.library.cantaloupe.operation.OperationList;
import edu.illinois.library.cantaloupe.processor.codec.ImageReader;
import edu.illinois.library.cantaloupe.processor.codec.ImageReaderFactory;
import edu.illinois.library.cantaloupe.test.TestUtil;

public class Java2dProcessorTest extends AbstractImageIOProcessorTest {

    private Java2dProcessor instance;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        instance = newInstance();
    }

    @Override
    protected Java2dProcessor newInstance() {
        return new Java2dProcessor();
    }

    @Test
    void testIsSeekingWithNonSeekableSource() throws Exception {
        instance.setSourceFormat(formatRegistry.formatWithKey("bmp"));
        instance.setSourceFile(TestUtil.getImage("bmp"));
        assertFalse(instance.isSeeking());
    }

    @Test
    void testIsSeekingWithSeekableSource() throws Exception {
        instance.setSourceFormat(formatRegistry.formatWithKey("tif"));
        instance.setSourceFile(TestUtil.getImage("tif-rgb-1res-64x56x8-tiled-jpeg.tif"));
        assertTrue(instance.isSeeking());
    }

    @Test
    void testProcessWithAnimatedGIF() throws Exception {
        Path image = TestUtil.getImage("gif-animated-looping.gif");
        OperationList ops = OperationList.builder()
                .withOperations(new Encode(formatRegistry.formatWithKey("gif")))
                .build();
        Info info = Info.builder()
                .withSize(136, 200)
                .withFormat(formatRegistry.formatWithKey("gif"))
                .build();

        instance.setSourceFile(image);
        instance.setSourceFormat(formatRegistry.formatWithKey("gif"));

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            instance.process(ops, info, os);

            try (ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray())) {
                ImageReader reader = null;
                try {
                    reader = new ImageReaderFactory().newImageReader(formatRegistry.formatWithKey("gif"), is);
                    assertEquals(2, reader.getNumImages());
                } finally {
                    if (reader != null) {
                        reader.dispose();
                    }
                }
            }
        }
    }

    @Test
    @Override
    public void testProcessWithTurboJPEGAvailable() {
        // This processor doesn't use TurboJPEG ever.
    }

    @Test
    @Override
    public void testProcessWithTurboJPEGNotAvailable() {
        // This processor doesn't use TurboJPEG ever.
    }

    @Test
    void testSupportsSourceFormatWithSupportedFormat() {
        try (Processor instance = newInstance()) {
            assertTrue(instance.supportsSourceFormat(formatRegistry.formatWithKey("jpg")));
        }
    }

    @Test
    void testSupportsSourceFormatWithUnsupportedFormat() {
        try (Processor instance = newInstance()) {
            assertFalse(instance.supportsSourceFormat(formatRegistry.formatWithKey("mp4")));
        }
    }

}
