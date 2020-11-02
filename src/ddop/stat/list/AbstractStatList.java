package ddop.stat.list;

import ddop.stat.Stat;
import ddop.stat.StatSource;
import util.StatTotals;

import java.util.Collection;

public abstract class AbstractStatList {
    public AbstractStatList() { this.init(); }
    public AbstractStatList(StatSource... sources) { this.init(); this.add(sources); }
    public AbstractStatList(Collection<StatSource> sources) { this.init(); this.add(sources); }

    public AbstractStatList add(StatSource[] sources) {
        if(sources == null) return this;
        for(StatSource ss : sources) this.add(ss);
        return this;
    }

    public AbstractStatList add(Collection<StatSource> sources) {
        if(sources == null) return this;
        for(StatSource ss : sources) this.add(ss);
        return this;
    }

    public AbstractStatList add(StatSource ss) {
        if(ss == null) return this;
        for(Stat s : ss.getStats()) this.add(s);
        return this;
    }

    protected abstract void init();
    public abstract AbstractStatList add(Stat s);
    public abstract StatTotals getStatTotals();
}
