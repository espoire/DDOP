package ddop.stat.conversions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ddop.Settings;
import ddop.stat.Stat;
import ddop.stat.StatTransformTemplate;
import util.Pair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NamedStat {
    private static final Map<String, NamedStat> all = NamedStat.loadJson();
    private final String name;
    protected final List<StatTransformTemplate> conversion;

    private NamedStat(String name, List<StatTransformTemplate> conversion) {
        this.name = name;
        this.conversion = conversion;
    }

    private static Map<String, NamedStat> loadJson() {
        String json = file.Reader.getEntireFile(Settings.NAMED_STAT_DEFINITIONS_JSON);
        Type type = new TypeToken<Map<String, List<StatTransformTemplate>>>() {}.getType();
        Map<String, List<StatTransformTemplate>> fromJson = new Gson().fromJson(json, type);

        Map<String, NamedStat> ret = new HashMap<>();
        for(Map.Entry<String, List<StatTransformTemplate>> entry : fromJson.entrySet())
            ret.put(entry.getKey(), new NamedStat(entry.getKey(), entry.getValue()));

        return NamedStatUnpacker.unpack(ret);
    }

    public static List<Stat> convertAll(List<Stat> toConvert) {
        List<Stat> converted = new ArrayList<>();

        for(Stat s : toConvert) converted.addAll(convert(s));

        return converted;
    }

    /** Returns a List of elementary stats into which the provided compound stat decomposes.
     * If the provided stat does not decompose, it will instead be returned as the sole element.
     *
     * @param toConvert The stat to convert.
     * @return List&lt;Stat>
     */
    public static List<Stat> convert(Stat toConvert) {
        NamedStat ns = NamedStat.all.get(toConvert.category);
        if(ns == null || ns.conversion.size() == 0) {
            List<Stat> ret = new ArrayList<>();
            ret.add(toConvert);
            return ret;
        }

        List<Stat> ret = new ArrayList<>(ns.conversion.size());
        for(StatTransformTemplate template : ns.conversion) {
            Stat conversion = template.applyTo(toConvert);

            List<Stat> fullyConverted = convert(conversion);
            ret.addAll(fullyConverted);
        }

        return ret;
    }

    public static boolean isNamed(Stat s) { return NamedStat.isNamed(s.category); }
    public static boolean isNamed(String s) {
        return NamedStat.all.containsKey(s);
    }

    /** Checks if the provided enchantment string contains a named stat category token. Return null if not.
     *
     * @return {the named stat category, the rest of the string that wasn't part of the named stat}*/
    public static Pair<String, String> containsNamed(String enchantment) {
        for(String s : NamedStat.all.keySet()) {
            int beforeToken = enchantment.indexOf(s);
            int afterToken = beforeToken + s.length();
            int end = enchantment.length();

            if(beforeToken != -1)
                return new Pair<>(s, enchantment.substring(0, beforeToken) + enchantment.substring(afterToken, end));
        }

        return null;
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();

        ret.append(this.name).append(" -> [");

        boolean isFirst = true;
        for(StatTransformTemplate stt : this.conversion) {
            if(!isFirst) ret.append(", ");
            isFirst = false;

            ret.append(stt);
        }

        ret.append("]");

        return ret.toString();
    }
}
