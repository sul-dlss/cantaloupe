package edu.illinois.library.cantaloupe.processor.codec;

import java.util.Set;

import edu.illinois.library.cantaloupe.image.Format;
import edu.illinois.library.cantaloupe.image.FormatRegistry;
import edu.illinois.library.cantaloupe.operation.Encode;
import edu.illinois.library.cantaloupe.processor.OutputFormatException;
import edu.illinois.library.cantaloupe.processor.codec.gif.GIFImageWriter;
import edu.illinois.library.cantaloupe.processor.codec.jpeg.JPEGImageWriter;
import edu.illinois.library.cantaloupe.processor.codec.png.PNGImageWriter;
import edu.illinois.library.cantaloupe.processor.codec.tiff.TIFFImageWriter;

public final class ImageWriterFactory {
    /**
     * @return Set of supported output formats.
     */
    public static Set<Format> supportedFormats(FormatRegistry formatRegistry) {
        return Set.of(
            formatRegistry.formatWithKey("gif"),
            formatRegistry.formatWithKey("jpg"),
            formatRegistry.formatWithKey("png"),
            formatRegistry.formatWithKey("tif"));
    }

    private FormatRegistry formatRegistry;
    public ImageWriterFactory(FormatRegistry formatRegistry) {
        this.formatRegistry = formatRegistry;
    }

    public ImageWriter newImageWriter(Encode encode)
            throws OutputFormatException {
        ImageWriter writer;
        if (formatRegistry.formatWithKey("gif").equals(encode.getFormat())) {
            writer = new GIFImageWriter();
        } else if (formatRegistry.formatWithKey("jpg").equals(encode.getFormat())) {
            writer = new JPEGImageWriter();
        } else if (formatRegistry.formatWithKey("png").equals(encode.getFormat())) {
            writer = new PNGImageWriter();
        } else if (formatRegistry.formatWithKey("tif").equals(encode.getFormat())) {
            writer = new TIFFImageWriter();
        } else {
            throw new OutputFormatException(encode.getFormat());
        }
        writer.setEncode(encode);
        return writer;
    }

}
