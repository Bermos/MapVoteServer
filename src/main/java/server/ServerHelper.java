package server;

import java.util.HashMap;
import java.util.Map;

public class ServerHelper {
    public static Map<String, String> getUrlParams(String url) {
        url = (url.charAt(0) == '/') ?
                url.replaceFirst("/\\?", "") : url.replaceFirst("\\?", "");

        Map<String,String> valueMap = new HashMap<>();

        String[] map = url.split("&");
        for (String aMap : map) {
            String[] pair = aMap.split("=");
            valueMap.put(pair[0], pair[1]);
        }

        return valueMap;
    }
}
