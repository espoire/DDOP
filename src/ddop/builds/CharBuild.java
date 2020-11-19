package ddop.builds;

import ddop.stat.StatCollection;

import java.util.Set;

public class CharBuild extends StatCollection {
	public CharBuild(Set<String> filter) { super(filter); }

	public CharBuild addStat(String category) {
		return (CharBuild) this.addStat(category, 1, "boolean");
	}

	public CharBuild addStat(String category, double magnitude) {
		return (CharBuild) this.addStat(category, magnitude, "build");
	}
}
