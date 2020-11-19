package ddop.stat;

import ddop.stat.list.VerboseStatList;

import java.util.Collection;
import java.util.Set;

public class StatCollection implements StatSource {
    private final VerboseStatList stats = new VerboseStatList();
    private final Set<String> filter;

    public StatCollection() { this(null); }
    public StatCollection(Set<String> filter) {
        this.filter = filter;
    }

    public StatCollection addStat(String category, int magnitude, String bonusType) {
        return this.addStat(category, (double) magnitude, bonusType);
    }
    public StatCollection addStat(String category, double magnitude, String bonusType) {
        if(this.filter != null && ! this.filter.contains(category)) return this;

        Stat s = new Stat(category, bonusType, magnitude);
        this.stats.add(s);

        return this;
    }

    public Collection<Stat> getStats() {
        return this.stats.getStats();
    }

}
