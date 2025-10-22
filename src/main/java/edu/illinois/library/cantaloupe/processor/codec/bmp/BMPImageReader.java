package edu.illinois.library.cantaloupe.processor.codec.bmp;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.illinois.library.cantaloupe.config.Configuration;
import edu.illinois.library.cantaloupe.image.Compression;
import edu.illinois.library.cantaloupe.image.Format;
import edu.illinois.library.cantaloupe.image.FormatRegistry;
import edu.illinois.library.cantaloupe.image.Metadata;
import edu.illinois.library.cantaloupe.processor.codec.AbstractIIOImageReader;
import edu.illinois.library.cantaloupe.processor.codec.ImageReader;

public final class BMPImageReader extends AbstractIIOImageReader
        implements ImageReader {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(BMPImageReader.class);

    static final String IMAGEIO_PLUGIN_CONFIG_KEY =
            "processor.imageio.bmp.reader";

    private final FormatRegistry formatRegistry;
    public BMPImageReader(FormatRegistry formatRegistry) {
        this.formatRegistry = formatRegistry;
    }

    @Override
    public boolean canSeek() {
        return false;
    }

    @Override
    protected String[] getApplicationPreferredIIOImplementations() {
        return new String[] { "com.sun.imageio.plugins.bmp.BMPImageReader" };
    }

    @Override
    public Compression getCompression(int imageIndex) throws IOException {
        // Throw any contract-required exceptions.
        getSize(0);
        return Compression.UNCOMPRESSED;
    }

    @Override
    protected Format getFormat() {
        return formatRegistry.formatWithKey("bmp");
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    public Metadata getMetadata(int imageIndex) throws IOException {
        // Throw any contract-required exceptions.
        getSize(0);
        return new Metadata();
    }

    @Override
    protected String getUserPreferredIIOImplementation() {
        Configuration config = Configuration.getInstance();
        return config.getString(IMAGEIO_PLUGIN_CONFIG_KEY);
    }

}
