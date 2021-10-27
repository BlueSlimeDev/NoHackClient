package dev.mruniverse.nohackclient.listeners.security;

import dev.mruniverse.nohackclient.utils.interfaces.GLogger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;

public class HttpUtils {

    private final String body;

    private Object result;

    private final HashMap<String,String> map = new HashMap<>();

    private final GLogger logs;

    public HttpUtils(GLogger logs, String body) {
        this.body = body;
        this.logs = logs;
        execute();
    }

    private void execute() {
        try {
            result = new JSONParser().parse(body);
            map();
        }catch (Throwable throwable) {
            logs.error(throwable);
        }
    }

    private void map() {
        if(result instanceof JSONObject) {
            handleObject();
        } else if(result instanceof JSONArray) {
            handleArray();
        } else {
            map.put("unique",(String)result);
        }
    }

    private void handleObject() {
        JSONObject object = (JSONObject)result;
        for(Object obj : object.keySet()) {
            map.put(obj.toString(),object.get(obj).toString());
        }
    }

    private void handleArray() {
        JSONArray obj = (JSONArray)result;
        for (int i = 0; i < obj.size(); i++) {
            map.put("" + (i + 1),(String)obj.get(i));
        }
    }

    public HashMap<String,String> getMap() {
        return map;
    }
}
