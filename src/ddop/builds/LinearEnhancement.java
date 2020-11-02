package ddop.builds;

import ddop.stat.Stat;

import java.util.ArrayList;
import java.util.List;

public class LinearEnhancement extends Enhancement {
    private final Stat[] perRank;

    public LinearEnhancement(String name, int maxRanks) {
        super(name, maxRanks);
        this.perRank = null;
    }
    
    public LinearEnhancement(String name, int maxRanks, Stat... perRank) {
        super(name, maxRanks);
        this.perRank = perRank;
    }
    
    public LinearEnhancement(String name, int maxRank, String statCategory, double statMagnitude) {
        this(name, maxRank, statCategory, statMagnitude, "Stacking");
    }
    
    public LinearEnhancement(String name, int maxRank, String statCategory, double statMagnitude, String statBonusType) {
        this(name, maxRank, new Stat(statCategory, statBonusType, statMagnitude));
    }
    
    @Override
    public List<Stat> getEffectAt(int rank) {
        List<Stat> ret = new ArrayList<>();
        
        if(this.perRank != null) for(int i = 0; i < this.perRank.length; i++) {
            Stat template = this.perRank[i];
            Stat effectAtRank = new Stat(template.category, template.bonusType, template.magnitude * rank);
            ret.add(effectAtRank);
        }
        
        return ret;
    }
}
