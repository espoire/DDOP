package ddop.builds;

import ddop.stat.Stat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EnhancementTree {
    private final Enhancement[][] enhancements;
    public EnhancementTree(Enhancement[][] enhancements) { this.enhancements = enhancements; }
    
    public List<Stat> getStatsFromBuild(EnhancementBuild build) {
        List<Stat> ret = new ArrayList<>();
        
        for(int i = 0; i < enhancements.length; i++) {
            for(int j = 0; j < enhancements[i].length; j++) {
                Collection<Stat> stats = getStats(build, i, j);
                ret.addAll(stats);
            }
        }
        
        return ret;
    }
    
    private Collection<Stat> getStats(EnhancementBuild build, int i, int j) {
        Enhancement enhancement = enhancements[i][j];
        int ranks = build.getRanks(i,j);
        return enhancement.getEffect(ranks);
    }
}
