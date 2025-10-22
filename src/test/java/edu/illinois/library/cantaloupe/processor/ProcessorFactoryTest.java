package edu.illinois.library.cantaloupe.processor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.illinois.library.cantaloupe.config.Configuration;
import edu.illinois.library.cantaloupe.config.Key;
import edu.illinois.library.cantaloupe.image.Format;
import edu.illinois.library.cantaloupe.test.BaseTest;

public class ProcessorFactoryTest extends BaseTest {

    private ProcessorFactory instance;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        instance = new ProcessorFactory();
    }

    @Test
    void testGetAllProcessors() {
        assertTrue(ProcessorFactory.getAllProcessors().size() > 5);
    }

    /* newProcessor(String) */

    @Test
    void testNewProcessorWithStringWithUnqualifiedName() throws Exception {
        assertTrue(instance.newProcessor(Java2dProcessor.class.getSimpleName()) instanceof Java2dProcessor);
    }

    @Test
    void testNewProcessorWithStringWithNonExistingUnqualifiedName() {
        assertThrows(ClassNotFoundException.class, () ->
                instance.newProcessor("Bogus"));
    }

    @Test
    void testNewProcessorWithStringWithQualifiedName() throws Exception {
        assertTrue(instance.newProcessor(Java2dProcessor.class.getName()) instanceof Java2dProcessor);
    }

    @Test
    void testNewProcessorWithStringWithNonExistingQualifiedName() {
        assertThrows(ClassNotFoundException.class, () ->
                instance.newProcessor(ProcessorFactory.class.getPackage().getName() + ".Bogus"));
    }

    /* newProcessor(Format) */

    @Test
    void testNewProcessorWithFormatWithWorkingFirstPreferenceMatch() throws Exception {
        instance.setSelectionStrategy(f ->
                List.of(PdfBoxProcessor.class, Java2dProcessor.class));
        assertTrue(instance.newProcessor(formatRegistry.formatWithKey("pdf")) instanceof PdfBoxProcessor);
    }

    @Test
    void testNewProcessorWithFormatWithBrokenFirstPreferenceMatchAndWorkingSecondPreferenceMatch()
            throws Exception {
        instance.setSelectionStrategy(f ->
                List.of(MockBrokenProcessor.class, Java2dProcessor.class));
        assertTrue(instance.newProcessor(formatRegistry.formatWithKey("jpg")) instanceof Java2dProcessor);
    }

    @Test
    void testNewProcessorWithFormatWithWorkingSecondPreferenceMatch() throws Exception {
        instance.setSelectionStrategy(f ->
                List.of(PdfBoxProcessor.class, Java2dProcessor.class));
        assertTrue(instance.newProcessor(formatRegistry.formatWithKey("jpg")) instanceof Java2dProcessor);
    }

    @Test
    void testNewProcessorWithFormatWithBrokenSecondPreferenceMatch() {
        instance.setSelectionStrategy(f ->
                List.of(PdfBoxProcessor.class, MockBrokenProcessor.class));
        assertThrows(InitializationException.class,
                () -> instance.newProcessor(formatRegistry.formatWithKey("jpg")));
    }

    @Test
    void testNewProcessorWithFormatWithNoMatch() {
        instance.setSelectionStrategy(f ->
                List.of(MockPDFOnlyProcessor.class, MockPNGOnlyProcessor.class));
        assertThrows(SourceFormatException.class,
                () -> instance.newProcessor(formatRegistry.formatWithKey("jpg")));
    }

    @Test
    void testNewProcessorWithFormatWithUnknownFormat() {
        Configuration.getInstance().setProperty(Key.PROCESSOR_FALLBACK,
                Java2dProcessor.class.getSimpleName());
        assertThrows(SourceFormatException.class,
                () -> instance.newProcessor(Format.UNKNOWN));
    }

}
