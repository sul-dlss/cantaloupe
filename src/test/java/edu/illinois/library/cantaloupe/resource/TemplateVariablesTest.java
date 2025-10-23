package edu.illinois.library.cantaloupe.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import edu.illinois.library.cantaloupe.config.Configuration;

public class TemplateVariablesTest {
    
    @Test
    void testGetCommonTemplateVars() {
        Request request = new Request(new MockHttpServletRequest(), java.util.Collections.emptyList(), Configuration.getInstance());
        TemplateVariables vars = TemplateVariables.getDefault(request);
        assertEquals(((String) vars.get("basePath")), "/");
        assertNotNull(vars.get("version"));
    }
}
