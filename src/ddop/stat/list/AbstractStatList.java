package ddop.stat.list;

import ddop.stat.Stat;
import ddop.stat.StatSource;
import util.StatTotals;

import java.util.Collection;
import java.util.Set;

public abstract class AbstractStatList {
    protected final Set<String> filter;

    public AbstractStatList(StatSource... sources) { this(null, sources); }
    public AbstractStatList(Set<String> filter, StatSource... sources) {
        this.filter = filter;
        this.init();
        this.add(sources);
    }

    public AbstractStatList add(StatSource[] sources) {
        if(sources == null) return this;
        for(StatSource ss : sources) this.add(ss);
        return this;
    }

    public AbstractStatList add(Iterable<StatSource> sources) {
        if(sources == null) return this;
        for(StatSource ss : sources) this.add(ss);
        return this;
    }

    public AbstractStatList add(StatSource ss) {
        if(ss != null) this.addAll(ss.getStats());
        return this;
    }

    protected AbstractStatList addAll(Collection<Stat> stats) {
        for(Stat s : stats) this.add(s);
        return this;
    }

    protected abstract void init();
    public AbstractStatList add(Stat s) {
        if(s != null) this.addImplementation(s);
        return this;
    }
    protected abstract void addImplementation(Stat s);
    public StatTotals getStatTotals() { return this.getStatTotals(this.filter); }
    protected abstract StatTotals getStatTotals(Set<String> filter);
}
