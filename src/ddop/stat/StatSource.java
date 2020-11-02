package ddop.stat;

import java.util.Collection;

public interface StatSource {
    public Collection<Stat> getStats();
    
    public default StatSource combine(StatSource another) {
        return new CombinedStatSource(this, another);
    }
}
