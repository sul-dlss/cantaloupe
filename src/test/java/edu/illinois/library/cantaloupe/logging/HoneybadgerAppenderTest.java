package edu.illinois.library.cantaloupe.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.util.LogbackMDCAdapter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import edu.illinois.library.cantaloupe.test.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class HoneybadgerAppenderTest extends BaseTest {

    private record Received(String apiKey, String body) {}

    private final BlockingQueue<Received> received = new LinkedBlockingQueue<>();
    private HttpServer server;
    private HoneybadgerAppender instance;
    private Logger logger;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/v1/notices", exchange -> {
            String body = new String(exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8);
            received.add(new Received(
                    exchange.getRequestHeaders().getFirst("X-API-Key"), body));
            exchange.sendResponseHeaders(201, -1);
            exchange.close();
        });
        server.start();

        LoggerContext context = new LoggerContext();
        context.setMDCAdapter(new LogbackMDCAdapter());
        logger = context.getLogger(HoneybadgerAppenderTest.class);
        logger.setAdditive(false);

        instance = new HoneybadgerAppender();
        instance.setContext(context);
        instance.setName("test");
        instance.setApiKey("secret");
        instance.setEnvironment("test");
        instance.setEndpoint("http://127.0.0.1:" +
                server.getAddress().getPort() + "/v1/notices");
        instance.start();
        logger.addAppender(instance);
    }

    @AfterEach
    public void tearDown() throws Exception {
        super.tearDown();
        instance.stop();
        server.stop(0);
    }

    @Test
    void startWithoutApiKeyDoesNotStart() {
        HoneybadgerAppender appender = new HoneybadgerAppender();
        appender.setContext(new LoggerContext());
        appender.start();
        assertFalse(appender.isStarted());
    }

    @Test
    void appendSendsErrorWithThrowable() throws Exception {
        Exception cause = new IllegalStateException("root cause");
        logger.error("Something broke: {}", "details",
                new RuntimeException("outer", cause));

        Received request = received.poll(5, TimeUnit.SECONDS);
        assertNotNull(request);
        assertEquals("secret", request.apiKey());

        JsonNode notice = new ObjectMapper().readTree(request.body());
        JsonNode error = notice.get("error");
        assertEquals("java.lang.RuntimeException", error.get("class").asText());
        assertEquals("outer", error.get("message").asText());
        assertFalse(error.get("backtrace").isEmpty());
        assertEquals("java.lang.IllegalStateException",
                error.get("causes").get(0).get("class").asText());
        assertEquals("Something broke: details",
                notice.get("request").get("context").get("log_message").asText());
        assertEquals("test",
                notice.get("server").get("environment_name").asText());
    }

    @Test
    void appendSendsErrorWithoutThrowable() throws Exception {
        logger.error("Plain error");

        Received request = received.poll(5, TimeUnit.SECONDS);
        assertNotNull(request);
        JsonNode error = new ObjectMapper().readTree(request.body()).get("error");
        assertEquals(HoneybadgerAppenderTest.class.getName(),
                error.get("class").asText());
        assertEquals("Plain error", error.get("message").asText());
    }

    @Test
    void appendIgnoresEventsBelowError() throws Exception {
        logger.warn("Just a warning");
        assertNull(received.poll(500, TimeUnit.MILLISECONDS));
    }

    @Test
    void toNoticeIncludesMdc() {
        LoggingEvent event = new LoggingEvent(Logger.FQCN, logger,
                Level.ERROR, "message", null, null);
        event.setMDCPropertyMap(Map.of("request_id", "abc123"));
        @SuppressWarnings("unchecked")
        Map<String, Object> context = (Map<String, Object>)
                ((Map<String, Object>) instance.toNotice(event).get("request")).get("context");
        assertEquals("abc123", context.get("request_id"));
    }

    @Test
    void toNoticeUsesMdcRequestUrl() {
        LoggingEvent event = new LoggingEvent(Logger.FQCN, logger,
                Level.ERROR, "message", null, null);
        event.setMDCPropertyMap(Map.of(HoneybadgerAppender.MDC_REQUEST_URL,
                "http://example.org/iiif/3/cats/full/max/0/default.jpg"));
        @SuppressWarnings("unchecked")
        Map<String, Object> request =
                (Map<String, Object>) instance.toNotice(event).get("request");
        assertEquals("http://example.org/iiif/3/cats/full/max/0/default.jpg",
                request.get("url"));
    }

    @Test
    void toNoticeUsesMdcIdentifier() {
        LoggingEvent event = new LoggingEvent(Logger.FQCN, logger,
                Level.ERROR, "message", null, null);
        event.setMDCPropertyMap(Map.of(HoneybadgerAppender.MDC_IDENTIFIER,
                "cats"));
        @SuppressWarnings("unchecked")
        Map<String, Object> request =
                (Map<String, Object>) instance.toNotice(event).get("request");
        assertEquals(Map.of("identifier", "cats"), request.get("params"));
    }

}
