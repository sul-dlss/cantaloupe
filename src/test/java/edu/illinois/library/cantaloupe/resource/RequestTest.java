package edu.illinois.library.cantaloupe.resource;

import edu.illinois.library.cantaloupe.http.Cookies;
import edu.illinois.library.cantaloupe.http.Headers;
import edu.illinois.library.cantaloupe.http.Method;
import edu.illinois.library.cantaloupe.http.Reference;
import edu.illinois.library.cantaloupe.test.BaseTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest extends BaseTest {

    private MockHttpServletRequest sr = new MockHttpServletRequest();
    private Request instance = new Request(sr, Collections.emptyList());

    @Test
    void testGetContextPath() {
        final String path = "/the-new-path";
        sr.setContextPath(path);
        assertEquals(path, instance.getContextPath());
    }

    @Test
    void testGetCookies() {
        sr.getHeaders().put("Cookie", List.of("fruit=apples; animal=cats",
                "shape=cube; car=ford"));

        Cookies cookies = instance.getCookies();
        assertEquals(4, cookies.size());
        assertEquals("apples", cookies.getFirstValue("fruit"));
        assertEquals("cats", cookies.getFirstValue("animal"));
        assertEquals("cube", cookies.getFirstValue("shape"));
        assertEquals("ford", cookies.getFirstValue("car"));
    }

    @Test
    void testGetHeaders() {
        sr.getHeaders().put("Cookie", List.of("cats=yes"));
        sr.getHeaders().put("Accept", List.of("text/plain"));

        Headers headers = instance.getHeaders();
        assertEquals(2, headers.size());
        assertEquals("cats=yes", headers.getFirstValue("Cookie"));
        assertEquals("text/plain", headers.getFirstValue("Accept"));
    }

    @Disabled // TODO: write this
    @Test
    void testGetInputStream() {
    }

    @Test
    void testGetMethod() {
        sr.setMethod("PUT");

        assertEquals(Method.PUT, instance.getMethod());
    }

    @Test
    void testGetReference() {
        String url = "http://example.org/cats?query=yes";
        sr.setRequestURL(url);

        assertEquals(new Reference(url), instance.getReference());
    }

    @Test
    void testGetRemoteAddr() {
        String addr = "10.2.5.3";
        sr.setRemoteAddr(addr);

        assertEquals(addr, instance.getRemoteAddr());
    }

    @Test
    void testGetServletRequest() {
        assertSame(sr, instance.getServletRequest());
    }

}
