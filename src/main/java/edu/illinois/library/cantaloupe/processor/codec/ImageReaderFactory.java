package edu.illinois.library.cantaloupe.processor.codec;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Set;

import javax.imageio.stream.ImageInputStream;

import edu.illinois.library.cantaloupe.image.Format;
import edu.illinois.library.cantaloupe.image.FormatRegistry;
import edu.illinois.library.cantaloupe.processor.codec.bmp.BMPImageReader;
import edu.illinois.library.cantaloupe.processor.codec.gif.GIFImageReader;
import edu.illinois.library.cantaloupe.processor.codec.jpeg.JPEGImageReader;
import edu.illinois.library.cantaloupe.processor.codec.png.PNGImageReader;
import edu.illinois.library.cantaloupe.processor.codec.tiff.TIFFImageReader;
import edu.illinois.library.cantaloupe.processor.codec.xpm.XPMImageReader;
import edu.illinois.library.cantaloupe.source.StreamFactory;
import edu.illinois.library.cantaloupe.source.stream.ClosingMemoryCacheImageInputStream;

/**
 * Used for obtaining {@link ImageReader} instances.
 */
public final class ImageReaderFactory {
    private FormatRegistry formatRegistry;
    public ImageReaderFactory(FormatRegistry formatRegistry) {
        this.formatRegistry = formatRegistry;
    }

    /**
     * @return Map of available output formats for all known source formats,
     *         based on information reported by ImageIO.
     */
    public static Set<Format> supportedFormats(FormatRegistry formatRegistry) {
        return Set.of(
            formatRegistry.formatWithKey("bmp"),
            formatRegistry.formatWithKey("gif"),
            formatRegistry.formatWithKey("jpg"),
            formatRegistry.formatWithKey("png"),
            formatRegistry.formatWithKey("tif"),
            formatRegistry.formatWithKey("xpm"));
    }

    public ImageReader newImageReader(Format format) {
        if (formatRegistry.formatWithKey("bmp").equals(format)) {
            return new BMPImageReader(formatRegistry);
        } else if (formatRegistry.formatWithKey("gif").equals(format)) {
            return new GIFImageReader(formatRegistry);
        } else if (formatRegistry.formatWithKey("jpg").equals(format)) {
            return new JPEGImageReader(formatRegistry);
        } else if (formatRegistry.formatWithKey("png").equals(format)) {
            return new PNGImageReader(formatRegistry);
        } else if (formatRegistry.formatWithKey("tif").equals(format)) {
            return new TIFFImageReader(formatRegistry);
        } else if (formatRegistry.formatWithKey("xpm").equals(format)) {
            return new XPMImageReader(formatRegistry);
        }
        throw new IllegalArgumentException("Unsupported format: " + format);
    }

    /**
     * Creates a reusable instance for reading from files.
     *
     * @param format     Format of the source image.
     * @param sourceFile File to read from.
     * @throws IllegalArgumentException if the format is unsupported.
     */
    public ImageReader newImageReader(Format format,
                                      Path sourceFile) throws IOException {
        ImageReader reader = newImageReader(format);
        reader.setSource(sourceFile);
        return reader;
    }

    /**
     * <p>Creates a non-reusable instance.</p>
     *
     * <p>{@link #newImageReader(Format, ImageInputStream)} should be preferred
     * when a first-class {@link ImageInputStream} can be provided.</p>
     *
     * @param format      Format of the source image.
     * @param inputStream Stream to read from.
     * @throws IllegalArgumentException if the format is unsupported.
     */
    public ImageReader newImageReader(Format format,
                                      InputStream inputStream) throws IOException {
        return newImageReader(format, new ClosingMemoryCacheImageInputStream(inputStream));
    }

    /**
     * Creates a non-reusable instance.
     *
     * @param format      Format of the source image.
     * @param inputStream Stream to read from.
     * @throws IllegalArgumentException if the format is unsupported.
     */
    public ImageReader newImageReader(Format format,
                                      ImageInputStream inputStream) throws IOException {
        ImageReader reader = newImageReader(format);
        reader.setSource(inputStream);
        return reader;
    }

    /**
     * Creates a reusable instance for reading from streams.
     *
     * @param format        Format of the source image.
     * @param streamFactory Source of stream to read from.
     * @throws IllegalArgumentException if the format is unsupported.
     */
    public ImageReader newImageReader(Format format,
                                      StreamFactory streamFactory) throws IOException {
        ImageReader reader = newImageReader(format);
        reader.setSource(streamFactory);
        return reader;
    }

}
