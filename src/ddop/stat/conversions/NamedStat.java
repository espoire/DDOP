package ddop.stat.conversions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ddop.Settings;
import ddop.stat.Stat;
import ddop.stat.StatTransformTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NamedStat {
    private static final Map<String, NamedStat> all = NamedStat.loadJson();
    private final List<StatTransformTemplate> conversion;

    private NamedStat(List<StatTransformTemplate> conversion) {
        this.conversion = conversion;
    }

    private static Map<String, NamedStat> loadJson() {
        String json = file.Reader.getEntireFile(Settings.NAMED_STAT_DEFINITIONS_JSON);
        Type type = new TypeToken<Map<String, List<StatTransformTemplate>>>() {}.getType();
        Map<String, List<StatTransformTemplate>> fromJson = new Gson().fromJson(json, type);

        Map<String, NamedStat> ret = new HashMap<>();
        for(Map.Entry<String, List<StatTransformTemplate>> entry : fromJson.entrySet())
            ret.put(entry.getKey(), new NamedStat(entry.getValue()));

        return ret;
    }

    public static List<Stat> convertAll(List<Stat> toConvert) {
        List<Stat> converted = new ArrayList<>();

        for(Stat s : toConvert) converted.addAll(convert(s));

        return converted;
    }

    /** Returns a List of elementary stats into which the provided compound stat decomposes.
     * If the provided stat does not decompose, it will instead be returned as the sole element.
     *
     * @param toConvert - The stat to convert.
     * @return Stat[].
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
            Stat conversion = template.apply(toConvert);

            List<Stat> fullyConverted = convert(conversion);
            ret.addAll(fullyConverted);
        }

        return ret;
    }

    public static boolean isNamed(Stat s) { return NamedStat.isNamed(s.category); }
    public static boolean isNamed(String s) {
        return NamedStat.all.containsKey(s);
    }
}
