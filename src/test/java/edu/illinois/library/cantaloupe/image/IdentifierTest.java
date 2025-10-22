package edu.illinois.library.cantaloupe.image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.illinois.library.cantaloupe.config.ConfigurationAccessor;
import edu.illinois.library.cantaloupe.config.Key;
import edu.illinois.library.cantaloupe.test.BaseTest;

public class IdentifierTest extends BaseTest {

    private Identifier instance;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        instance = new Identifier("cats");
    }

    @Test
    void testJSONSerialization() throws Exception {
        Identifier identifier = new Identifier("cats");
        try (StringWriter writer = new StringWriter()) {
            new ObjectMapper().writeValue(writer, identifier);
            assertEquals("\"cats\"", writer.toString());
        }
    }

    @Test
    void testJSONDeserialization() throws Exception {
        Identifier identifier = new ObjectMapper().readValue("\"cats\"",
                Identifier.class);
        assertEquals("cats", identifier.toString());
    }

    @Test
    void testFromURIPathComponent() {
        ConfigurationAccessor.getConfiguration().setProperty(Key.SLASH_SUBSTITUTE, "BUG");

        String pathComponent = "catsBUG%3Adogs";
        Identifier actual = Identifier.fromURIPathComponent(pathComponent);
        Identifier expected = new Identifier("cats/:dogs");
        assertEquals(expected, actual);
    }

    @Test
    void testConstructor() {
        assertEquals("cats", instance.toString());
    }

    @Test
    void testConstructorWithNullArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> new Identifier(null));
    }

    @Test
    void testCompareTo() {
        Identifier id1 = new Identifier("cats");
        Identifier id2 = new Identifier("dogs");
        Identifier id3 = new Identifier("cats");
        assertTrue(id1.compareTo(id2) < 0);
        assertEquals(0, id1.compareTo(id3));
    }

    @Test
    void testEqualsWithEqualInstances() {
        assertEquals(new Identifier("cats"), new Identifier("cats"));
    }

    @Test
    void testEqualsWithUnequalInstances() {
        assertNotEquals(new Identifier("cats"), new Identifier("dogs"));
    }

    @Test
    void testHashCode() {
        Identifier id1 = new Identifier("cats");
        Identifier id2 = new Identifier("cats");
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("cats", instance.toString());
    }

}
