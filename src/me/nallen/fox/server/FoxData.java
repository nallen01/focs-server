package me.nallen.fox.server;

public class FoxData {
	public static final int NUM_HISTORY_POINTS = 201;
	
	public int redHighBalls;
	public int redLowBalls;
	public boolean redAuton;
	public ElevatedState redElevation; 
	
	public int blueHighBalls;
	public int blueLowBalls;
	public boolean blueAuton;
	public ElevatedState blueElevation;

	public int[] redScoreHistory = new int[NUM_HISTORY_POINTS];
	public int redScoreHistoryPos = 0;
	public int[] blueScoreHistory = new int[NUM_HISTORY_POINTS];
	public int blueScoreHistoryPos = 0;
	
	public enum ElevatedState {
	    NONE, LOW, HIGH
	}
	
	public int getRedScore() {
		int score = 0;
		score += redHighBalls * 5;
		score += redLowBalls;
		score += redAuton ? 10 : 0;
		score += redElevation == ElevatedState.HIGH ? 50 : redElevation == ElevatedState.LOW ? 25 : 0;
		return score;
	}
	
	public int getBlueScore() {
		int score = 0;
		score += blueHighBalls * 5;
		score += blueLowBalls;
		score += blueAuton ? 10 : 0;
		score += blueElevation == ElevatedState.HIGH ? 50 : blueElevation == ElevatedState.LOW ? 25 : 0;
		return score;
	}
	
	public void clear() {
		redHighBalls = 0;
		redLowBalls = 0;
		redAuton = false;
		redElevation = ElevatedState.NONE;
		
		blueHighBalls = 0;
		blueLowBalls = 0;
		blueAuton = false;
		blueElevation = ElevatedState.NONE;
		
		for(int i=0; i<NUM_HISTORY_POINTS; i++) {
			redScoreHistory[i] = -1;
			blueScoreHistory[i] = -1;
		}
		redScoreHistoryPos = 0;
		blueScoreHistoryPos = 0;
	}
}
