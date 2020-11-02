package ddop.builds;

import ddop.stat.Stat;

import java.util.ArrayList;
import java.util.List;

public abstract class Enhancement {
    public final String name;
    public final int maxRanks;
    
    public Enhancement(String name, int maxRanks) {
        this.name = name;
        this.maxRanks = maxRanks;
    }
    
    private boolean isValidRank(int rank) { return (0 <= rank && rank <= this.maxRanks); }
    public List<Stat> getEffect(int rank) {
        if(!this.isValidRank(rank))
            throw new Error("Invalid rank \"" + rank + "\" for Enhancement \"" + this.name + "\" with max ranks \"" + this.maxRanks + "\".");
        if(rank > 0) return this.getEffectAt(rank);
        return new ArrayList<>();
    }
    protected abstract List<Stat> getEffectAt(int rank);
}
