package dev.mruniverse.nohackclient.listeners.security;

import java.util.Map;

import static java.util.stream.Collectors.*;

@SuppressWarnings("unused")
public class HttpResponse {

    private final int code;
    private final String message;
    private final String statusLine;
    private final Map<String, String> headers;
    private final String body;

    public HttpResponse(int code, String message, String statusLine, Map<String, String> headers,
                        String body) {
        this.code = code;
        this.message = message;
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getStatusLine() {
        return statusLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return statusLine + "\n" +
                headers.entrySet().stream()
                        .map(e -> e.getKey() + ": " + e.getValue())
                        .collect(joining("\n", "", "\n\n"))
                + body;
    }
}