package edu.illinois.library.cantaloupe.processor.codec.xpm;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.illinois.library.cantaloupe.image.Compression;
import edu.illinois.library.cantaloupe.image.Format;
import edu.illinois.library.cantaloupe.image.FormatRegistry;
import edu.illinois.library.cantaloupe.image.Metadata;
import edu.illinois.library.cantaloupe.processor.codec.AbstractIIOImageReader;
import edu.illinois.library.cantaloupe.processor.codec.ImageReader;
import edu.illinois.library.cantaloupe.processor.codec.generic.GenericMetadata;

public final class XPMImageReader extends AbstractIIOImageReader
        implements ImageReader {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(XPMImageReader.class);

    private final FormatRegistry formatRegistry;
    public XPMImageReader(FormatRegistry formatRegistry) {
        this.formatRegistry = formatRegistry;
    }

    @Override
    public boolean canSeek() {
        return false;
    }

    @Override
    protected String[] getApplicationPreferredIIOImplementations() {
        return new String[0];
    }

    @Override
    public Compression getCompression(int imageIndex) throws IOException {
        // Throw any contract-required exceptions.
        getSize(0);
        return Compression.UNCOMPRESSED;
    }

    @Override
    protected Format getFormat() {
        return formatRegistry.formatWithKey("xpm");
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    public Metadata getMetadata(int imageIndex) throws IOException {
        // Throw any contract-required exceptions.
        getSize(0);
        return new GenericMetadata();
    }

    @Override
    protected String getUserPreferredIIOImplementation() {
        return null;
    }

}
