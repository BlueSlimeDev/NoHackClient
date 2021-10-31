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

@SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
public class HttpDebugRequest {

    private HttpResponse lastResponse;

    private static final Pattern HEADER = Pattern.compile("(.*?):(.+)");
    private static final String[] EMPTY_STRING_ARRAY = new String[0];


    private static final ExecutorService threadPool =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final String method;
    private final String url;

    private final NoHackClient plugin;

    private final Player staff;

    private final String ip;

    private final String player;

    private boolean debugAll;

    private SecurityController.Keys key = SecurityController.Keys.PROXYCHECK;

    public HttpDebugRequest(NoHackClient plugin, String method, String url, String player,String ip,Player staff,boolean debugAll) {
        this.method = method;
        this.url = url;
        List<String> headers = new ArrayList<>();
        String body = "";
        this.plugin = plugin;
        this.player = player;
        this.debugAll = debugAll;
        this.staff = staff;
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
                        if(staff != null) {
                            plugin.getSecurity().checkResult(ip, key,debugAll, player,staff, utils.getMap());
                        }
                    });

                });
    }

    @SuppressWarnings("RedundantOperationOnEmptyContainer")
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

        boolean hasHeaders = false;
        String body = "";
        boolean hasBody = false;

        HttpURLConnection conn = null;

        try {
            URL target = new URL(url);
            conn = (HttpURLConnection) target.openConnection();

            conn.setRequestProperty("User-Agent", String.format("LatinPlay/%s (AntiCheatDetector)",
                    plugin.getDescription().getVersion()));

            for (String header : EMPTY_STRING_ARRAY) {
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
