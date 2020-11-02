package ddop.builds;

public class EnhancementBuild {
    protected final int[][] ranks;
    public EnhancementBuild(int[][] ranks) { this.ranks = ranks; }
    public int getRanks(int rowsFromTop, int columnsFromLeft) { return this.ranks[rowsFromTop][columnsFromLeft]; }
}
