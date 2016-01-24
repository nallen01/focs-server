package me.nallen.fox.server;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FoxData {
	public static final int NUM_HISTORY_POINTS = 200;
	
	public int redHighBalls;
	public int redLowBalls;
	public boolean redAuton;
	public ElevatedState redElevation;
	
	public int blueHighBalls;
	public int blueLowBalls;
	public boolean blueAuton;
	public ElevatedState blueElevation;

	public int[] redScoreHistory = new int[NUM_HISTORY_POINTS];
	public int[] blueScoreHistory = new int[NUM_HISTORY_POINTS];
	public int scoreHistoryPos = 0;
	
	public boolean isPaused = false;
	
	public boolean showHistory = true;
	public boolean largeHistory = false;
	
	public enum ElevatedState {
	    NONE, LOW, HIGH
	}
	
	public FoxData() {
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

		ses.scheduleAtFixedRate(new Runnable() {
		    public void run() {
		    	doTick();
		    }
		}, 0, 1, TimeUnit.SECONDS);
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
	
	public int[] getRedScoreHistory() {
		int[] returnArray = new int[NUM_HISTORY_POINTS];
		
		int[] arrayOne = Arrays.copyOfRange(redScoreHistory, scoreHistoryPos, redScoreHistory.length);
		int[] arrayTwo = Arrays.copyOfRange(redScoreHistory, 0, scoreHistoryPos);
		
		System.arraycopy(arrayOne, 0, returnArray, 0, arrayOne.length);
		System.arraycopy(arrayTwo, 0, returnArray, arrayOne.length, arrayTwo.length);
		
		return returnArray;
	}
	
	public int[] getBlueScoreHistory() {
		int[] returnArray = new int[NUM_HISTORY_POINTS];
		
		int[] arrayOne = Arrays.copyOfRange(blueScoreHistory, scoreHistoryPos, blueScoreHistory.length);
		int[] arrayTwo = Arrays.copyOfRange(blueScoreHistory, 0, scoreHistoryPos);
		
		System.arraycopy(arrayOne, 0, returnArray, 0, arrayOne.length);
		System.arraycopy(arrayTwo, 0, returnArray, arrayOne.length, arrayTwo.length);
		
		return returnArray;
	}
	
	public void doTick() {
		if(!isPaused) {
			redScoreHistory[scoreHistoryPos] = getRedScore();
			blueScoreHistory[scoreHistoryPos] = getBlueScore();
			
			scoreHistoryPos = (scoreHistoryPos + 1) % NUM_HISTORY_POINTS;
		}
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
		scoreHistoryPos = 0;
	}
}
