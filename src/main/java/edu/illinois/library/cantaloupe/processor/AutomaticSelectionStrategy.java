package edu.illinois.library.cantaloupe.processor;

import java.util.List;

import edu.illinois.library.cantaloupe.image.Format;
import edu.illinois.library.cantaloupe.image.FormatRegistry;

/**
 * Selects a {@link Processor} based on which ones are available for use, and
 * their assumed relative efficiency at handling a given source format.
 */
class AutomaticSelectionStrategy implements SelectionStrategy {

    private static final List<Class<? extends Processor>> JP2_CANDIDATES = List.of(
            KakaduNativeProcessor.class,
            OpenJpegProcessor.class,
            GrokProcessor.class);
    private static final List<Class<? extends Processor>> JPG_CANDIDATES = List.of(
            TurboJpegProcessor.class,
            Java2dProcessor.class);
    private static final List<Class<? extends Processor>> PDF_CANDIDATES = List.of(
            PdfBoxProcessor.class);
    private static final List<Class<? extends Processor>> VIDEO_CANDIDATES = List.of(
            FfmpegProcessor.class);
    private static final List<Class<? extends Processor>> FALLBACK_CANDIDATES = List.of(
            Java2dProcessor.class);

    private final FormatRegistry formatRegistry;

    AutomaticSelectionStrategy(FormatRegistry formatRegistry) {
        this.formatRegistry = formatRegistry;
    }
    
    @Override
    public List<Class<? extends Processor>> getPreferredProcessors(Format sourceFormat) {
        if (formatRegistry.formatWithKey("jp2").equals(sourceFormat)) {
            return JP2_CANDIDATES;
        } else if (formatRegistry.formatWithKey("jpg").equals(sourceFormat)) {
            return JPG_CANDIDATES;
        } else if (formatRegistry.formatWithKey("pdf").equals(sourceFormat)) {
            return PDF_CANDIDATES;
        } else if (sourceFormat.isVideo()) {
            return VIDEO_CANDIDATES;
        }
        return FALLBACK_CANDIDATES;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
