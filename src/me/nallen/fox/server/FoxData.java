package me.nallen.fox.server;

public class FoxData {
	public int redHighBalls;
	public int redLowBalls;
	public boolean redAuton;
	public ElevatedState redElevation; 
	
	public int blueHighBalls;
	public int blueLowBalls;
	public boolean blueAuton;
	public ElevatedState blueElevation;
	
	public boolean show;
	
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
	
	public boolean getShow() {
		return show;
	}
}
