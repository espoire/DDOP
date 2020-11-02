package ddop.stat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CombinedStatSource implements StatSource {
    private List<Stat> stats = new ArrayList<>();
    
    public CombinedStatSource(StatSource... sources) {
        for(StatSource source : sources) {
            this.stats.addAll(source.getStats());
        }
    }
    
    @Override
    public Collection<Stat> getStats() {
        return this.stats;
    }
}
