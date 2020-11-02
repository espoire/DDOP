package ddop.builds;

public class CharBuild extends StatCollection {
	public CharBuild addStat(String category) {
		return (CharBuild) this.addStat(category, 1, "boolean");
	}

	public CharBuild addStat(String category, double magnitude) {
		return (CharBuild) this.addStat(category, magnitude, "build");
	}
}
