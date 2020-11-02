package util;

import java.util.List;
import java.util.Map;

public class Json {
    private static String formatPropertyIdentifier(String property) {
        return "\"" + property + "\":";
    }

    public static String formatValue(String value) {
        return "\"" + value.replaceAll("\"","\\\\\\\"") + "\"";
    }

    public static String formatProperty(String property, List<String> values) {
        return formatPropertyIdentifier(property) + formatList(values);
    }

    public static String formatList(List<String> values) {
        String ret = "[";

        boolean isFirst = true;
        for(String value : values) {
            if(!isFirst) ret += ",";
            isFirst = false;

            ret += Json.formatValue(value);
        }

        ret += "]";

        return ret;
    }

    public static String formatMap                (Map<String, List<String>> map) { return formatMapCore(map, false); }
    public static String formatMapRemoveEmptyLists(Map<String, List<String>> map) { return formatMapCore(map, true ); }

    private static String formatMapCore(Map<String, List<String>> map, boolean removeEmptyLists) {
        String ret = "{";

        boolean isFirst = true;
        for(String key : map.keySet()) {
            List<String> values = map.get(key);
            if(removeEmptyLists && values.isEmpty()) continue;

            if(!isFirst) ret += ",";
            isFirst = false;

            ret += Json.formatProperty(key, values);
        }

        ret += "}";

        return ret;
    }
}