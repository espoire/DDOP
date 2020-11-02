package ddop.builds;

import ddop.stat.Stat;
import ddop.stat.list.VerboseStatList;
import ddop.stat.StatSource;

import java.util.Collection;

public class StatCollection implements StatSource {
    private final VerboseStatList stats = new VerboseStatList();

    public StatCollection addStat(String category, double magnitude, String bonusType) {
        Stat s = new Stat(category, bonusType, magnitude);

        this.stats.add(s);

        return this;
    }

    public Collection<Stat> getStats() {
        return this.stats.getStats();
    }

}
