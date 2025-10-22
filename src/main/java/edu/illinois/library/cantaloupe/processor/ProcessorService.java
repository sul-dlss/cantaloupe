package edu.illinois.library.cantaloupe.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.illinois.library.cantaloupe.config.Configuration;
import edu.illinois.library.cantaloupe.config.Key;
import edu.illinois.library.cantaloupe.image.Format;
import edu.illinois.library.cantaloupe.util.CommandLocator;

/**
 * Spring service that provides processor-related functionality with
 * dependency injection instead of using ConfigurationAccessor.getConfiguration().
 *
 * This service replaces static methods in various processor classes
 * that previously accessed configuration directly via singleton pattern.
 */
@Service
public class ProcessorService {

    private final Configuration configuration;

    @Autowired
    public ProcessorService(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Gets the path to FFmpeg binaries.
     * Replaces FfmpegProcessor.getPath(String binaryName).
     *
     * @param binaryName Name of one of the ffmpeg binaries
     * @return Path to the binary
     */
    public String getFfmpegPath(String binaryName) {
        String searchPath = configuration.getString(Key.FFMPEGPROCESSOR_PATH_TO_BINARIES);
        return CommandLocator.locate(binaryName, searchPath);
    }

    /**
     * Gets the path to Grok binaries.
     * Replaces GrokProcessor.getPath().
     *
     * @param binaryName Name of the grok binary
     * @return Path to the binary
     */
    public String getGrokPath(String binaryName) {
        String searchPath = configuration.getString(Key.GROKPROCESSOR_PATH_TO_BINARIES);
        return CommandLocator.locate(binaryName, searchPath);
    }

    /**
     * Gets the path to OpenJPEG binaries.
     * Replaces OpenJpegProcessor.getPath().
     *
     * @param binaryName Name of the OpenJPEG binary
     * @return Path to the binary
     */
    public String getOpenJpegPath(String binaryName) {
        String searchPath = configuration.getString(Key.OPENJPEGPROCESSOR_PATH_TO_BINARIES);
        return CommandLocator.locate(binaryName, searchPath);
    }

    /**
     * Gets the DPI setting for rasterization.
     * Replaces RasterizationHelper constructor logic.
     *
     * @return DPI value from configuration
     */
    public int getRasterizationDpi() {
        return configuration.getInt(Key.PROCESSOR_DPI, 150); // 150 is common fallback
    }

    /**
     * Gets the fallback processor name from manual selection strategy.
     * Replaces ManualSelectionStrategy.getFallbackProcessorName().
     *
     * @return Fallback processor name
     */
    public String getFallbackProcessorName() {
        return configuration.getString(Key.PROCESSOR_FALLBACK);
    }

    /**
     * Gets the assigned processor name for a specific format.
     * Replaces ManualSelectionStrategy.getAssignedProcessorName(Format).
     *
     * @param format Image format
     * @return Processor name assigned to the format
     */
    public String getAssignedProcessorName(Format format) {
        return configuration.getString(
                "processor.ManualSelectionStrategy." + format.getPreferredExtension());
    }

    /**
     * Checks if PDF scratch file is enabled.
     * Replaces PdfBoxProcessor configuration access.
     *
     * @return true if PDF scratch file is enabled
     */
    public boolean isPdfScratchFileEnabled() {
        return configuration.getBoolean(Key.PROCESSOR_PDF_SCRATCH_FILE_ENABLED, false);
    }

    /**
     * Gets PDF scratch file max memory setting.
     *
     * @return Max memory in bytes for PDF processing
     */
    // public long getPdfMaxMainMemoryBytes() {
    //     return configuration.getLongBytes(Key.PROCESSOR_PDF_MAX_MAIN_MEMORY_BYTES, 1024 * 1024 * 100); // 100MB default
    // }

    /**
     * Gets the processor selection strategy.
     *
     * @return Processor selection strategy name
     */
    public String getProcessorSelectionStrategy() {
        return configuration.getString(Key.PROCESSOR_SELECTION_STRATEGY, "AutomaticSelectionStrategy");
    }

    /**
     * Gets retrieval strategy for a specific key.
     * Replaces RetrievalStrategy.from(Key).
     *
     * @param key Configuration key
     * @return RetrievalStrategy enum value
     */
    public RetrievalStrategy getRetrievalStrategy(Key key) {
        final String configValue = configuration.getString(key, "");

        switch (configValue) {
            case "StreamStrategy":
                return RetrievalStrategy.STREAM;
            case "CacheStrategy":
                return RetrievalStrategy.CACHE;
            case "DownloadStrategy":
                return RetrievalStrategy.DOWNLOAD;
            default:
                return RetrievalStrategy.STREAM; // Default fallback
        }
    }

    /**
     * Checks if a processor should use large images support.
     *
     * @return true if large images support is enabled
     */
    // public boolean isLargeImageSupportEnabled() {
    //     return configuration.getBoolean(Key.PROCESSOR_LARGE_IMAGE_SUPPORT_ENABLED, false);
    // }

    /**
     * Gets the maximum allowed pixel area for processing.
     * Replaces OperationList access to MAX_PIXELS.
     *
     * @return Maximum pixels allowed, or 0 for unlimited
     */
    public long getMaxPixels() {
        return configuration.getLong(Key.MAX_PIXELS, 0);
    }

    /**
     * Gets processor-specific configuration value.
     *
     * @param processorName Name of the processor
     * @param configKey Configuration key suffix
     * @param defaultValue Default value if not found
     * @return Configuration value
     */
    public String getProcessorConfig(String processorName, String configKey, String defaultValue) {
        String fullKey = "processor." + processorName + "." + configKey;
        return configuration.getString(fullKey, defaultValue);
    }

    /**
     * Gets processor-specific boolean configuration value.
     *
     * @param processorName Name of the processor
     * @param configKey Configuration key suffix
     * @param defaultValue Default value if not found
     * @return Configuration value
     */
    public boolean getProcessorConfigBoolean(String processorName, String configKey, boolean defaultValue) {
        String fullKey = "processor." + processorName + "." + configKey;
        return configuration.getBoolean(fullKey, defaultValue);
    }

    /**
     * Gets processor-specific integer configuration value.
     *
     * @param processorName Name of the processor
     * @param configKey Configuration key suffix
     * @param defaultValue Default value if not found
     * @return Configuration value
     */
    public int getProcessorConfigInt(String processorName, String configKey, int defaultValue) {
        String fullKey = "processor." + processorName + "." + configKey;
        return configuration.getInt(fullKey, defaultValue);
    }
}
