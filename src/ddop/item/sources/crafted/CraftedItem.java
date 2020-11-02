package ddop.item.sources.crafted;

import ddop.stat.Stat;
import ddop.stat.StatSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CraftedItem implements StatSource {
    private final List<Stat> stats = new ArrayList<Stat>();
    
    public void addStats(StatSource... sources) {
        for(StatSource source : sources) {
            this.stats.addAll(source.getStats());
        }
    }
    
    @Override
    public Collection<Stat> getStats() {
        return this.stats;
    }
}
