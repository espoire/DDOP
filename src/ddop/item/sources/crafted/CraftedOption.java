package ddop.item.sources.crafted;

import ddop.stat.Stat;
import ddop.stat.StatSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CraftedOption implements StatSource {
    public final String name;
    private final List<Stat> stats = new ArrayList<Stat>();
    
    public CraftedOption(String name, Stat... stats) {
        this.name = name;
        this.stats.addAll(Arrays.asList(stats));
    }
    
    @Override
    public Collection<Stat> getStats() {
        return this.stats;
    }
}
