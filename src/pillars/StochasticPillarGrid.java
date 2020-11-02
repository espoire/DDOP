package pillars;

import java.util.ArrayList;

public class StochasticPillarGrid {
	// Set start state here. -1 indicates the given cell is not present.
	private static final int[][] state = new int[][] {{3,3,3},  // First row (north)
													  {4,3,3},  // Second row
													  {2,4,2}}; // Third row (south)
	
	public static void main(String... s) {
		new StochasticPillarGrid(state);
	}
	
	private static final int MAX_MOVES = 200;
	
	private static final int[] UP = new int[] {-1,  0},
							 DOWN = new int[] { 1,  0},
							 LEFT = new int[] { 0, -1},
							RIGHT = new int[] { 0,  1};
	private static final int[][] ORDER = new int[][] {UP, DOWN, LEFT, RIGHT};
	
	private int[][] pillars;
	
	public StochasticPillarGrid(int[][] state) {
		this.pillars = state;
		int[][] solution = this.solve();
		printSolution(solution);
	}
	
	
	private void printSolution(int[][] solution) {
		for(int i = 0; i < solution.length; i++) {
			int[] move = solution[i];
			if(move == null) continue;
			System.out.println(i + "> Row: " + move[0] + ", Col: " + move[1]);
		}
	}


	private int[][] solve() {
		int[][] bestSolution = null;
		int bestSolutionLength = MAX_MOVES;
		
		for(int trials = 0; trials < 1000000; trials++) {
			int[][] state = this.copyState();
			int rows = state.length;
			int cols = state[0].length;
			
			int[][] moves = new int[bestSolutionLength][];
			int movesTaken = 0;
			
			for(int move = 0; move < bestSolutionLength; move++) {
				int row = random(0, rows -1);
				int col = random(0, cols -1);
				
				int height = state[row][col];
				
				if(height < 0 || height > 3) continue;
				
				ArrayList<int[]> queue = new ArrayList<int[]>();
				for(int i = 0; i < 4; i++) {
					int[] offset = ORDER[i];
					
					int y = row + offset[0],
						x = col + offset[1];
					
					if(y < 0 || y >= rows || x < 0 || x >= cols || state[y][x] < 1) continue;
					
					queue.add(new int[] {x, y});
				}
				
				boolean changed = false;
				for(int targetHeight = 4; targetHeight > 0 && queue.size() > 0 && height < 4; targetHeight--) {
					for(int i = 0; i < queue.size() && height < 4; i++) {
						if(state[queue.get(i)[0]][queue.get(i)[1]] == targetHeight) {
							changed = true;
							state[queue.get(i)[0]][queue.get(i)[1]]--;
							state[row][col]++;
							height++;
							queue.remove(i);
							i--;
						}
					}
				}
				if(changed) moves[movesTaken++] = new int[] {row, col};
				
				if(isSolved(state)) {
					if(movesTaken < bestSolutionLength) {
						bestSolution = moves;
						bestSolutionLength = movesTaken;
					}
					break;
				}
			}
		}
		
		return bestSolution;
	}

	private static boolean isSolved(int[][] state) {
		int target = -1;
		
		for(int[] row : state) {
			for(int i : row) {
				if(i != target) {
					if(target == -1) {
						target = i;
					} else if(i >= 0) {
						return false;
					}
				}
			}
		}
		
		return true;
	}


	private int[][] copyState() {
		int[][] ret = new int[pillars.length][pillars[0].length];
		
		for(int row = 0; row < pillars.length; row++) {
			for(int col = 0; col < pillars[0].length; col++) {
				ret[row][col] = pillars[row][col];
			}
		}
		
		return ret;
	}
	
	public static int random(int min, int max) {
		return (int) (Math.random() * (max - min + 1) + min);
	}
}
