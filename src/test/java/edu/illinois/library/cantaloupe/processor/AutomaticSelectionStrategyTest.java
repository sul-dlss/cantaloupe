package edu.illinois.library.cantaloupe.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.illinois.library.cantaloupe.test.BaseTest;

public class AutomaticSelectionStrategyTest extends BaseTest {

    private AutomaticSelectionStrategy instance;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        instance = new AutomaticSelectionStrategy();
    }

    @Test
    void getPreferredProcessorsWithJP2() {
        List<?> expected = List.of(
                KakaduNativeProcessor.class,
                OpenJpegProcessor.class,
                GrokProcessor.class);
        assertEquals(expected, instance.getPreferredProcessors(formatRegistry.formatWithKey("jp2")));
    }

    @Test
    void getPreferredProcessorsWithJPG() {
        List<?> expected = List.of(
                TurboJpegProcessor.class,
                Java2dProcessor.class);
        assertEquals(expected, instance.getPreferredProcessors(formatRegistry.formatWithKey("jpg")));
    }

    @Test
    void getPreferredProcessorsWithPDF() {
        List<?> expected = List.of(PdfBoxProcessor.class);
        assertEquals(expected, instance.getPreferredProcessors(formatRegistry.formatWithKey("pdf")));
    }

    @Test
    void getPreferredProcessorsWithVideo() {
        List<?> expected = List.of(FfmpegProcessor.class);
        assertEquals(expected, instance.getPreferredProcessors(formatRegistry.formatWithKey("mpg")));
    }

    @Test
    void getPreferredProcessorsWithOther() {
        List<?> expected = List.of(Java2dProcessor.class);
        assertEquals(expected, instance.getPreferredProcessors(formatRegistry.formatWithKey("bmp")));
        assertEquals(expected, instance.getPreferredProcessors(formatRegistry.formatWithKey("gif")));
        assertEquals(expected, instance.getPreferredProcessors(formatRegistry.formatWithKey("png")));
        assertEquals(expected, instance.getPreferredProcessors(formatRegistry.formatWithKey("tif")));
    }

    @Test
    void testToString() {
        assertEquals("AutomaticSelectionStrategy", instance.toString());
    }

}
