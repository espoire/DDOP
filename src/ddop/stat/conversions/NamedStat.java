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
    private final String name;
    private final List<StatTransformTemplate> conversion;

    private NamedStat(String name, List<StatTransformTemplate> conversion) {
        this.name = name;
        this.conversion = conversion;
    }

    //region Initialization
    private static Map<String, NamedStat> loadJson() {
        String json = file.Reader.getEntireFile(Settings.NAMED_STAT_DEFINITIONS_JSON);
        Type type = new TypeToken<Map<String, List<StatTransformTemplate>>>() {}.getType();
        Map<String, List<StatTransformTemplate>> fromJson = new Gson().fromJson(json, type);

        Map<String, NamedStat> ret = new HashMap<>();
        for(Map.Entry<String, List<StatTransformTemplate>> entry : fromJson.entrySet())
            ret.put(entry.getKey(), new NamedStat(entry.getKey(), entry.getValue()));

        return unpack(ret);
    }

    //region Unpacking
    /** Recursively unpacks all NamedStat definitions until they convert directly into basic
     * stats without referencing another NamedStat along the way. */
    private static Map<String, NamedStat> unpack(Map<String, NamedStat> toUnpack) {
        for(NamedStat ns : toUnpack.values())
            unpack(ns, toUnpack);

        return toUnpack;
    }

    /** Unpack all the StatTransformTemplates in the provided NamedStat, and replace the
     * conversion list with the result. */
    private static void unpack(NamedStat toUnpack, Map<String, NamedStat> statMap) {
        List<StatTransformTemplate> unpacked = new ArrayList<>();
        for(StatTransformTemplate stt : toUnpack.conversion)
            if(stt != null)
                unpacked.addAll(unpack(stt, statMap));

        toUnpack.conversion.clear();
        toUnpack.conversion.addAll(unpacked);
    }

    /** Unpacks the provided StatTransformTemplate, by attempting to recursively apply
     * NamedStat conversion to the template. */
    private static List<StatTransformTemplate> unpack(StatTransformTemplate toUnpack, Map<String, NamedStat> statMap) {
        NamedStat ns = statMap.get(toUnpack.category);
        List<StatTransformTemplate> ret = new ArrayList<>();

        if(ns == null || ns.conversion.size() == 0) {
            ret.add(toUnpack);
        } else {
            for(StatTransformTemplate template : ns.conversion) {
                StatTransformTemplate unpacked = template.applyTo(toUnpack);

                List<StatTransformTemplate> fullyUnpacked = unpack(unpacked, statMap);
                ret.addAll(fullyUnpacked);
            }
        }

        return ret;
    }
    //endregion
    //endregion

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
