package edu.illinois.library.cantaloupe.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.AppenderBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.illinois.library.cantaloupe.Application;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>Reports {@literal ERROR}-level log events to
 * <a href="https://www.honeybadger.io/">Honeybadger</a> via its
 * <a href="https://docs.honeybadger.io/api/reporting-exceptions/">notice
 * API</a>.</p>
 *
 * <p>Notices are sent asynchronously from a single background thread. If the
 * queue fills up (e.g. during an error storm or a Honeybadger outage), new
 * notices are dropped rather than blocking the logging thread.</p>
 *
 * <p>Problems with the appender itself are reported via the Logback status
 * system rather than via a logger, to avoid feedback loops.</p>
 */
public class HoneybadgerAppender extends AppenderBase<ILoggingEvent> {

    static final String DEFAULT_ENDPOINT = "https://api.honeybadger.io/v1/notices";

    private static final String NOTIFIER_NAME = "Cantaloupe Honeybadger Appender";
    private static final String NOTIFIER_URL  = "https://github.com/cantaloupe-project/cantaloupe";
    private static final int QUEUE_SIZE       = 100;
    private static final Duration TIMEOUT     = Duration.ofSeconds(10);

    /**
     * {@link org.slf4j.MDC} key whose value, if present, is reported as the
     * notice's request URL.
     */
    static final String MDC_REQUEST_URL = "request_url";

    /**
     * {@link org.slf4j.MDC} key whose value, if present, is reported as the
     * {@literal identifier} parameter of the notice's request.
     */
    static final String MDC_IDENTIFIER = "identifier";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String apiKey;
    private String endpoint = DEFAULT_ENDPOINT;
    private String environment;
    private String hostname;
    private HttpClient httpClient;
    private ThreadPoolExecutor executor;

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    @Override
    public void start() {
        if (apiKey == null || apiKey.isBlank()) {
            addError("No API key set for appender named \"" + name + "\"");
            return;
        }
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "unknown";
        }
        httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
        executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(QUEUE_SIZE), r -> {
                    Thread thread = new Thread(r, "honeybadger-appender");
                    thread.setDaemon(true);
                    return thread;
                }, new ThreadPoolExecutor.AbortPolicy());
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        if (executor != null) {
            executor.shutdown();
            try {
                executor.awaitTermination(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!event.getLevel().isGreaterOrEqual(Level.ERROR)) {
            return;
        }
        final String body;
        try {
            body = MAPPER.writeValueAsString(toNotice(event));
        } catch (Exception e) {
            addError("Failed to serialize Honeybadger notice", e);
            return;
        }
        try {
            executor.execute(() -> send(body));
        } catch (RejectedExecutionException e) {
            addWarn("Honeybadger queue is full; dropping notice");
        }
    }

    private void send(String body) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                .timeout(TIMEOUT)
                .header("X-API-Key", apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                addWarn("Honeybadger responded with HTTP " +
                        response.statusCode() + ": " + response.body());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            addWarn("Failed to send notice to Honeybadger", e);
        }
    }

    Map<String, Object> toNotice(ILoggingEvent event) {
        Map<String, Object> notice = new LinkedHashMap<>();

        Map<String, Object> notifier = new LinkedHashMap<>();
        notifier.put("name", NOTIFIER_NAME);
        notifier.put("url", NOTIFIER_URL);
        notifier.put("version", Application.getVersion());
        notice.put("notifier", notifier);

        Map<String, Object> error = new LinkedHashMap<>();
        IThrowableProxy throwable = event.getThrowableProxy();
        if (throwable != null) {
            error.put("class", throwable.getClassName());
            error.put("message", (throwable.getMessage() != null) ?
                    throwable.getMessage() : event.getFormattedMessage());
            error.put("backtrace", toBacktrace(throwable));
            List<Map<String, Object>> causes = new ArrayList<>();
            IThrowableProxy cause = throwable.getCause();
            while (cause != null && causes.size() < 10) {
                Map<String, Object> causeMap = new LinkedHashMap<>();
                causeMap.put("class", cause.getClassName());
                causeMap.put("message", cause.getMessage());
                causeMap.put("backtrace", toBacktrace(cause));
                causes.add(causeMap);
                cause = cause.getCause();
            }
            error.put("causes", causes);
        } else {
            error.put("class", event.getLoggerName());
            error.put("message", event.getFormattedMessage());
            List<Map<String, Object>> backtrace = new ArrayList<>();
            StackTraceElement[] callerData = event.getCallerData();
            if (callerData != null) {
                for (StackTraceElement element : callerData) {
                    backtrace.add(toBacktraceLine(element));
                }
            }
            error.put("backtrace", backtrace);
        }
        error.put("tags", List.of("logger:" + event.getLoggerName()));
        notice.put("error", error);

        Map<String, Object> context = new LinkedHashMap<>();
        context.put("log_message", event.getFormattedMessage());
        context.put("logger", event.getLoggerName());
        context.put("thread", event.getThreadName());
        context.putAll(event.getMDCPropertyMap());
        Map<String, Object> request = new LinkedHashMap<>();
        String url = event.getMDCPropertyMap().get(MDC_REQUEST_URL);
        if (url != null) {
            request.put("url", url);
        }
        String identifier = event.getMDCPropertyMap().get(MDC_IDENTIFIER);
        if (identifier != null) {
            request.put("params", Map.of("identifier", identifier));
        }
        request.put("context", context);
        notice.put("request", request);

        Map<String, Object> server = new LinkedHashMap<>();
        if (environment != null && !environment.isBlank()) {
            server.put("environment_name", environment);
        }
        server.put("hostname", hostname);
        server.put("project_root", System.getProperty("user.dir"));
        notice.put("server", server);

        return notice;
    }

    private static List<Map<String, Object>> toBacktrace(IThrowableProxy throwable) {
        List<Map<String, Object>> backtrace = new ArrayList<>();
        StackTraceElementProxy[] elements = throwable.getStackTraceElementProxyArray();
        if (elements != null) {
            for (StackTraceElementProxy element : elements) {
                backtrace.add(toBacktraceLine(element.getStackTraceElement()));
            }
        }
        return backtrace;
    }

    private static Map<String, Object> toBacktraceLine(StackTraceElement element) {
        Map<String, Object> line = new LinkedHashMap<>();
        line.put("number", String.valueOf(element.getLineNumber()));
        line.put("file", element.getFileName());
        line.put("method", element.getClassName() + "." + element.getMethodName());
        return line;
    }

}
