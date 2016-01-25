package me.nallen.fox.server;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.nallen.fox.server.DataListener.UpdateType;

public class FoxData {
	public static final int NUM_HISTORY_POINTS = 200;
	
	private int redHighBalls;
	private int redLowBalls;
	private boolean redAuton;
	private ElevatedState redElevation;
	
	private int blueHighBalls;
	private int blueLowBalls;
	private boolean blueAuton;
	private ElevatedState blueElevation;

	private int[] redScoreHistory = new int[NUM_HISTORY_POINTS];
	private int[] blueScoreHistory = new int[NUM_HISTORY_POINTS];
	private int scoreHistoryPos = 0;
	
	private boolean isPaused = false;
	
	private boolean showHistory = true;
	private boolean largeHistory = false;

	private LinkedList<DataListener> _listeners = new LinkedList<DataListener>();
	
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
	
	public boolean getLargeHistory() {
		return largeHistory;
	}
	
	public boolean getShowHistory() {
		return showHistory;
	}
	
	public synchronized void addListener(DataListener listener)  {
		_listeners.add(listener);
	}
	public synchronized void removeListener(DataListener listener)   {
		_listeners.remove(listener);
	}
	private synchronized void fireUpdate(UpdateType type) {
		Iterator<DataListener> i = _listeners.iterator();
		while(i.hasNext())  {
			((DataListener) i.next()).update(type);
		}
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
			
			fireUpdate(UpdateType.TICK);
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
