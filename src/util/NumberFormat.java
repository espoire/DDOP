package util;

import java.util.LinkedHashMap;
import java.util.Map;

public class NumberFormat {
    private static Map<String, Long> SI_PREFIXES = new LinkedHashMap<String, Long>();
    static {
        SI_PREFIXES.put(" E", 1000000000000000000L);
        SI_PREFIXES.put(" P", 1000000000000000L);
        SI_PREFIXES.put(" T", 1000000000000L);
        SI_PREFIXES.put(" G", 1000000000L);
        SI_PREFIXES.put(" M", 1000000L);
        SI_PREFIXES.put("k", 1000L);
    }

    public static String readableLargeNumber(long number) {
        return readableLargeNumber((double) number);
    }

    public static String readableLargeNumber(double number) {
        for(String prefix : SI_PREFIXES.keySet()) {
            long unitValue = SI_PREFIXES.get(prefix);
            double ratio = number / unitValue;

            if(ratio >= 1) return niceFloat(ratio) + prefix;
        }

        return niceFloat(number);
    }

    private static Map<String, Long> TIME_UNITS = new LinkedHashMap<String, Long>();
    static {
        TIME_UNITS.put("year", 220903200000L);
        TIME_UNITS.put("month", 18408600000L);
        TIME_UNITS.put("week",    604800000L);
        TIME_UNITS.put("day",      86400000L);
        TIME_UNITS.put("hour",      3600000L);
        TIME_UNITS.put("minute",      60000L);
        TIME_UNITS.put("second",       1000L);
        TIME_UNITS.put("milli",           1L);
    }

    public static String readableLongTime(long millis) {
        for(String unit : TIME_UNITS.keySet()) {
            long unitValue = TIME_UNITS.get(unit);
            double ratio = (double) millis / unitValue;

            if(ratio >= 1) return niceFloat(ratio) + " " + unit + (ratio >= 1.05 ? "s" : "");
        }

        return niceFloat(millis);
    }
    
    protected static String niceFloat(double value) {
        String ret = ("" + value);

        int decimalPlace = ret.indexOf(".");

        int cutoff = decimalPlace + 2;
        if(cutoff > ret.length()) cutoff = ret.length();

        return ret.substring(0, cutoff);
    }
    
    public static String percent(double number) {
        return niceFloat(number * 100) + "%";
    }
}
