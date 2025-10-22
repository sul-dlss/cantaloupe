package edu.illinois.library.cantaloupe.image;

import edu.illinois.library.cantaloupe.test.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FormatTest extends BaseTest {

    @Test
    void testAll() {
        Set<String> expected = Set.of("avi", "bmp", "flv", "gif", "jp2", "jpg",
                "mov", "mp4", "mpg", "pdf", "png", "tif", "webm", "webp",
                "xpm");
        Set<String> actual = Format.all()
                .stream()
                .map(Format::getKey)
                .collect(Collectors.toSet());
        assertEquals(expected, actual);
    }

    @Test
    void testGetWithValidKey() {
        assertEquals(FormatRegistry.formatWithKey("jpg"), formatRegistry.formatWithKey("jpg"));
    }

    @Test
    void testGetWithInvalidKey() {
        assertNull(formatRegistry.formatWithKey("bogus"));
    }

    @Test
    void testInferFormatWithIdentifier() {
        // AVI
        assertEquals(formatRegistry.formatWithKey("avi"),
                Format.inferFormat(new Identifier("bla.avi")));
        assertEquals(formatRegistry.formatWithKey("avi"),
                Format.inferFormat(new Identifier("bla.AVI")));
        // BMP
        assertEquals(formatRegistry.formatWithKey("bmp"),
                Format.inferFormat(new Identifier("bla.bmp")));
        // FLV
        assertEquals(formatRegistry.formatWithKey("flv"),
                Format.inferFormat(new Identifier("bla.flv")));
        // GIF
        assertEquals(formatRegistry.formatWithKey("gif"),
                Format.inferFormat(new Identifier("bla.gif")));
        // JP2
        assertEquals(formatRegistry.formatWithKey("jp2"),
                Format.inferFormat(new Identifier("bla.jp2")));
        assertEquals(formatRegistry.formatWithKey("jp2"),
                Format.inferFormat(new Identifier("bla.jpx")));
        // JPG
        assertEquals(formatRegistry.formatWithKey("jpg"),
                Format.inferFormat(new Identifier("bla.jpg")));
        // MOV
        assertEquals(formatRegistry.formatWithKey("mov"),
                Format.inferFormat(new Identifier("bla.mov")));
        // MP4
        assertEquals(formatRegistry.formatWithKey("mp4"),
                Format.inferFormat(new Identifier("bla.mp4")));
        // MPG
        assertEquals(formatRegistry.formatWithKey("mpg"),
                Format.inferFormat(new Identifier("bla.mpg")));
        // PDF
        assertEquals(formatRegistry.formatWithKey("pdf"),
                Format.inferFormat(new Identifier("bla.pdf")));
        // PNG
        assertEquals(formatRegistry.formatWithKey("png"),
                Format.inferFormat(new Identifier("bla.png")));
        // TIF
        assertEquals(formatRegistry.formatWithKey("tif"),
                Format.inferFormat(new Identifier("bla.tif")));
        // WEBM
        assertEquals(formatRegistry.formatWithKey("webm"),
                Format.inferFormat(new Identifier("bla.webm")));
        // WEBP
        assertEquals(formatRegistry.formatWithKey("webp"),
                Format.inferFormat(new Identifier("bla.webp")));
        // XPM
        assertEquals(formatRegistry.formatWithKey("xpm"),
                Format.inferFormat(new Identifier("bla.xpm")));
    }

    @Test
    void testInferFormatWithString() {
        // AVI
        assertEquals(formatRegistry.formatWithKey("avi"), Format.inferFormat("bla.avi"));
        assertEquals(formatRegistry.formatWithKey("avi"), Format.inferFormat("bla.AVI"));
        // BMP
        assertEquals(formatRegistry.formatWithKey("bmp"), Format.inferFormat("bla.bmp"));
        // FLV
        assertEquals(formatRegistry.formatWithKey("flv"), Format.inferFormat("bla.flv"));
        // GIF
        assertEquals(formatRegistry.formatWithKey("gif"), Format.inferFormat("bla.gif"));
        // JP2
        assertEquals(formatRegistry.formatWithKey("jp2"), Format.inferFormat("bla.jp2"));
        assertEquals(formatRegistry.formatWithKey("jp2"), Format.inferFormat("bla.jpx"));
        // JPG
        assertEquals(formatRegistry.formatWithKey("jpg"), Format.inferFormat("bla.jpg"));
        // MOV
        assertEquals(formatRegistry.formatWithKey("mov"), Format.inferFormat("bla.mov"));
        // MP4
        assertEquals(formatRegistry.formatWithKey("mp4"), Format.inferFormat("bla.mp4"));
        // MPG
        assertEquals(formatRegistry.formatWithKey("mpg"), Format.inferFormat("bla.mpg"));
        // PDF
        assertEquals(formatRegistry.formatWithKey("pdf"), Format.inferFormat("bla.pdf"));
        // PNG
        assertEquals(formatRegistry.formatWithKey("png"), Format.inferFormat("bla.png"));
        // TIF
        assertEquals(formatRegistry.formatWithKey("tif"), Format.inferFormat("bla.tif"));
        // UNKNOWN
        assertEquals(Format.UNKNOWN, Format.inferFormat("bla.bogus"));
        // WEBM
        assertEquals(formatRegistry.formatWithKey("webm"), Format.inferFormat("bla.webm"));
        // WEBP
        assertEquals(formatRegistry.formatWithKey("webp"), Format.inferFormat("bla.webp"));
        // XPM
        assertEquals(formatRegistry.formatWithKey("xpm"), Format.inferFormat("bla.xpm"));
    }

    @Test
    void testWithExtensionAndAMatch() {
        assertEquals(formatRegistry.formatWithKey("jpg"), Format.withExtension("jpg"));
        assertEquals(formatRegistry.formatWithKey("jpg"), Format.withExtension(".jpg"));
        assertEquals(formatRegistry.formatWithKey("jpg"), Format.withExtension("JPG"));
        assertEquals(formatRegistry.formatWithKey("jpg"), Format.withExtension(".JPG"));
    }

    @Test
    void testWithExtensionAndNoMatch() {
        assertNull(Format.withExtension("bogus"));
    }

    @Test
    void testCompareTo() {
        assertTrue(formatRegistry.formatWithKey("avi").compareTo(formatRegistry.formatWithKey("tif")) < 0);
        assertEquals(0, formatRegistry.formatWithKey("avi").compareTo(formatRegistry.formatWithKey("avi")));
        assertTrue(formatRegistry.formatWithKey("tif").compareTo(formatRegistry.formatWithKey("avi")) > 0);
    }

    @Test
    void testEqualsWithEqualInstances() {
        assertEquals(formatRegistry.formatWithKey("jpg"), formatRegistry.formatWithKey("jpg"));
    }

    @Test
    void testEqualsWithUnequalInstances() {
        assertNotEquals(formatRegistry.formatWithKey("jpg"), formatRegistry.formatWithKey("tif"));
    }

    @Test
    void testGetPreferredExtension() {
        assertEquals("avi", formatRegistry.formatWithKey("avi").getPreferredExtension());
        assertEquals("bmp", formatRegistry.formatWithKey("bmp").getPreferredExtension());
        assertEquals("flv", formatRegistry.formatWithKey("flv").getPreferredExtension());
        assertEquals("gif", formatRegistry.formatWithKey("gif").getPreferredExtension());
        assertEquals("jp2", formatRegistry.formatWithKey("jp2").getPreferredExtension());
        assertEquals("jpg", formatRegistry.formatWithKey("jpg").getPreferredExtension());
        assertEquals("mov", formatRegistry.formatWithKey("mov").getPreferredExtension());
        assertEquals("mp4", formatRegistry.formatWithKey("mp4").getPreferredExtension());
        assertEquals("mpg", formatRegistry.formatWithKey("mpg").getPreferredExtension());
        assertEquals("pdf", formatRegistry.formatWithKey("pdf").getPreferredExtension());
        assertEquals("png", formatRegistry.formatWithKey("png").getPreferredExtension());
        assertEquals("tif", formatRegistry.formatWithKey("tif").getPreferredExtension());
        assertEquals("unknown", Format.UNKNOWN.getPreferredExtension());
        assertEquals("webm", formatRegistry.formatWithKey("webm").getPreferredExtension());
        assertEquals("webp", formatRegistry.formatWithKey("webp").getPreferredExtension());
        assertEquals("xpm", formatRegistry.formatWithKey("xpm").getPreferredExtension());
    }

    @Test
    void testGetPreferredMediaType() {
        assertEquals("video/avi",
                formatRegistry.formatWithKey("avi").getPreferredMediaType().toString());
        assertEquals("image/bmp",
                formatRegistry.formatWithKey("bmp").getPreferredMediaType().toString());
        assertEquals("video/x-flv",
                formatRegistry.formatWithKey("flv").getPreferredMediaType().toString());
        assertEquals("image/gif",
                formatRegistry.formatWithKey("gif").getPreferredMediaType().toString());
        assertEquals("image/jp2",
                formatRegistry.formatWithKey("jp2").getPreferredMediaType().toString());
        assertEquals("image/jpeg",
                formatRegistry.formatWithKey("jpg").getPreferredMediaType().toString());
        assertEquals("video/quicktime",
                formatRegistry.formatWithKey("mov").getPreferredMediaType().toString());
        assertEquals("video/mp4",
                formatRegistry.formatWithKey("mp4").getPreferredMediaType().toString());
        assertEquals("video/mpeg",
                formatRegistry.formatWithKey("mpg").getPreferredMediaType().toString());
        assertEquals("application/pdf",
                formatRegistry.formatWithKey("pdf").getPreferredMediaType().toString());
        assertEquals("image/png",
                formatRegistry.formatWithKey("png").getPreferredMediaType().toString());
        assertEquals("image/tiff",
                formatRegistry.formatWithKey("tif").getPreferredMediaType().toString());
        assertEquals("unknown/unknown",
                Format.UNKNOWN.getPreferredMediaType().toString());
        assertEquals("video/webm",
                formatRegistry.formatWithKey("webm").getPreferredMediaType().toString());
        assertEquals("image/webp",
                formatRegistry.formatWithKey("webp").getPreferredMediaType().toString());
        assertEquals("image/x-xpixmap",
                formatRegistry.formatWithKey("xpm").getPreferredMediaType().toString());
    }

    @Test
    void testHashCodeWithEqualInstances() {
        assertEquals(formatRegistry.formatWithKey("jpg").hashCode(), formatRegistry.formatWithKey("jpg").hashCode());
    }

    @Test
    void testHashCodeWithUnequalInstances() {
        assertNotEquals(formatRegistry.formatWithKey("jpg").hashCode(), formatRegistry.formatWithKey("tif").hashCode());
    }

    @Test
    void testToMap() {
        Map<String, Object> map = formatRegistry.formatWithKey("jpg").toMap();
        assertEquals("jpg", map.get("extension"));
        assertEquals("image/jpeg", map.get("media_type"));

        //noinspection ConstantConditions
        assertThrows(UnsupportedOperationException.class,
                () -> map.put("cats", "cats"));
    }

    @Test
    void testToString() {
        for (Format format : Format.all()) {
            assertEquals(format.getPreferredExtension(),
                    format.toString());
        }
    }

}
