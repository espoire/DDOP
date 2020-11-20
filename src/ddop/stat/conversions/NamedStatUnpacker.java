package ddop.stat.conversions;

import ddop.stat.StatTransformTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NamedStatUnpacker {
    /** Recursively unpacks all NamedStat definitions until they convert directly into basic
     * stats without referencing another NamedStat along the way. */
    public static Map<String, NamedStat> unpack(Map<String, NamedStat> toUnpack) {
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
}
