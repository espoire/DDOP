package ddop.builds;

import ddop.stat.Stat;
import util.collection.TwoKeyedHashMap;
import util.collection.TwoKeyedMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EnhancementTree {
    private final Enhancement[][] enhancements;
    public EnhancementTree(Enhancement[][] enhancements) { this.enhancements = enhancements; }
    
    public List<Stat> getStatsFromBuild(EnhancementBuild build) {
        TwoKeyedHashMap<String, String, Double> stacker = new TwoKeyedHashMap<>();

        for(int i = 0; i < enhancements.length; i++)
            for(int j = 0; j < enhancements[i].length; j++)
                for(Stat s : getStats(build, i, j))
                    stacker.put(s.category, s.bonusType, stacker.getOrDefault(s.category, s.bonusType, 0.0) + s.magnitude);

        List<Stat> ret = new ArrayList<>();

        for(TwoKeyedMap<String, String, Double>.Entry entry : stacker.entrySet())
            ret.add(new Stat(entry.getKey1(), entry.getKey2(), entry.getValue()));
        
        return ret;
    }
    
    private Collection<Stat> getStats(EnhancementBuild build, int i, int j) {
        Enhancement enhancement = enhancements[i][j];
        int ranks = build.getRanks(i,j);
        return enhancement.getEffect(ranks);
    }
}
