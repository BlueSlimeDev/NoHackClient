package dev.mruniverse.nohackclient.listeners.security;

import dev.mruniverse.nohackclient.NoHackClient;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import static java.util.stream.Collectors.toMap;

@SuppressWarnings("unused")
public class HttpRequest {

    private HttpResponse lastResponse;

    private static final Pattern HEADER = Pattern.compile("(.*?):(.+)");
    private static final String[] EMPTY_STRING_ARRAY = new String[0];


    private static final ExecutorService threadPool =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final String method;
    private final String url;
    private final List<String> headers;
    private final String body;

    private final NoHackClient plugin;

    private boolean hasHeaders = false;
    private boolean hasBody = false;

    private final Player player;

    private String ip = "";

    private SecurityController.Keys key = SecurityController.Keys.PROXYCHECK;

    public HttpRequest(NoHackClient plugin, String method, String url, List<String> headers, String body, boolean hasHeaders, boolean hasBody) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.body = body;
        this.hasHeaders = hasHeaders;
        this.hasBody = hasBody;
        this.plugin = plugin;
        this.player = null;
        if(url.contains("ipqualityscore")) {
            key = SecurityController.Keys.IPQUALITYSCORE;
        }
        execute();
    }

    public HttpRequest(NoHackClient plugin, String method, String url, List<String> headers, String body, boolean hasHeaders, boolean hasBody,Player player,String ip) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.body = body;
        this.hasHeaders = hasHeaders;
        this.hasBody = hasBody;
        this.plugin = plugin;
        this.player = player;
        this.ip = ip;
        if(url.contains("ipqualityscore")) {
            key = SecurityController.Keys.IPQUALITYSCORE;
        }
        execute();
    }

    public HttpRequest(NoHackClient plugin, String method, String url) {
        this.method = method;
        this.url = url;
        this.headers = new ArrayList<>();
        this.body = "";
        this.plugin = plugin;
        this.player = null;
        if(url.contains("ipqualityscore")) {
            key = SecurityController.Keys.IPQUALITYSCORE;
        }
        execute();
    }

    public HttpRequest(NoHackClient plugin, String method, String url, Player player,String ip) {
        this.method = method;
        this.url = url;
        this.headers = new ArrayList<>();
        this.body = "";
        this.plugin = plugin;
        this.player = player;
        this.ip = ip;
        if(url.contains("ipqualityscore")) {
            key = SecurityController.Keys.IPQUALITYSCORE;
        }
        execute();
    }

    public HttpResponse getResponse() {
        return lastResponse;
    }

    public void execute() {
        CompletableFuture.supplyAsync(this::sendRequest, threadPool)
                .whenComplete((resp, err) -> {
                    if (err != null) {
                        err.printStackTrace();
                        lastResponse = null;
                    }
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        lastResponse = resp;
                        HttpUtils utils = new HttpUtils(plugin.getLogs(),resp.getBody());
                        if(player != null) {
                            plugin.getSecurity().result(ip, key, player, utils.getMap());
                        }
                    });

                });
    }

    public HttpResponse sendRequest() {
        String method = null;
        if (this.method != null) {
            method = this.method.toUpperCase();
        }

        String url = this.url;
        if (url == null) {
            return null;
        }
        url = url.replace('§', '&');

        String[] headers = EMPTY_STRING_ARRAY;
        if (this.headers != null && hasHeaders) {
            headers = this.headers.toArray(new String[0]);
        }
        String body = "";
        if (this.body != null && hasBody) {
            body = String.join("\n", this.body);
        }

        HttpURLConnection conn = null;

        try {
            URL target = new URL(url);
            conn = (HttpURLConnection) target.openConnection();

            conn.setRequestProperty("User-Agent", String.format("LatinPlay/%s (AntiCheatDetector)",
                    plugin.getDescription().getVersion()));

            for (String header : headers) {
                Matcher headerMatcher = HEADER.matcher(header);
                if (headerMatcher.matches()) {
                    conn.setRequestProperty(headerMatcher.group(1).trim(),
                            headerMatcher.group(2).trim());
                } else {
                    plugin.getLogs().warn(String.format("Malformed header during request to %s: %s", url, header));
                }
            }

            conn.setUseCaches(false);

            if (method != null && !method.equals("GET")) {
                conn.setRequestProperty("Content-Length", Integer.toString(body.getBytes().length));
                conn.setRequestMethod(method);
                conn.setDoOutput(true);
                try (OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
                    out.write(body);
                }
            } else if (!body.equals("")) {
                plugin.getLogs().warn("Get requests should not have a body");
            }

            String statusLine = conn.getHeaderField(0);
            Map<String, String> responseHeaders = conn.getHeaderFields().entrySet().stream()
                    .filter(h -> h.getKey() != null)
                    .collect(toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().get(0)
                    ));

            InputStream response = conn.getErrorStream();
            if (response == null) {
                response = conn.getInputStream();
            }

            String encoding = conn.getContentEncoding();
            if (encoding != null)
                if (encoding.equalsIgnoreCase("gzip")) {
                    response = new GZIPInputStream(response);
                } else if (encoding.equalsIgnoreCase("deflate")) {
                    response = new InflaterInputStream(response, new Inflater(true));
                }

            StringBuilder responseBody = new StringBuilder();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    responseBody.append(line);
                    responseBody.append("\n");
                }
            }

            return new HttpResponse(conn.getResponseCode(), conn.getResponseMessage(), statusLine,
                    responseHeaders, responseBody.toString());
        } catch (MalformedURLException err) {
            plugin.getLogs().warn("Tried to send a request to a malformed URL: " + url);
        } catch (IOException err) {
            err.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return null;
    }
}
