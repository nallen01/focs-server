package me.nallen.fox.server;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.nallen.fox.server.DataListener.UpdateType;

public class FoxData {
	public static final int HISTORY_SECONDS = 180;
	public static final double HISTORY_FREQUENCY = 10;
	public static final int NUM_HISTORY_POINTS = (int) (HISTORY_SECONDS * HISTORY_FREQUENCY) + 1;
	public static final int HISTORY_MILLISECONDS = (int) (1000 / HISTORY_FREQUENCY);
	
	private int redFarStars;
	private int redFarCubes;
	private int redNearStars;
	private int redNearCubes;
	private boolean redAuton;
	private ElevatedState redElevation;
	
	private int blueFarStars;
	private int blueFarCubes;
	private int blueNearStars;
	private int blueNearCubes;
	private boolean blueAuton;
	private ElevatedState blueElevation;

	private int[] redScoreHistory = new int[NUM_HISTORY_POINTS];
	private int[] blueScoreHistory = new int[NUM_HISTORY_POINTS];
	private int scoreHistoryPos = 0;
	
	private boolean isPaused = false;
	
	private boolean showHistory = true;
	private boolean largeHistory = true;

	private LinkedList<DataListener> _listeners = new LinkedList<DataListener>();
	
	public enum ElevatedState {
	    NONE(0),
	    LOW(1),
	    HIGH(2);
		
		private final int id;
		ElevatedState(int id) { this.id = id; }
		public int getValue() { return id; }
		public static ElevatedState fromInt(int id) {
			ElevatedState[] values = ElevatedState.values();
            for(int i=0; i<values.length; i++) {
                if(values[i].getValue() == id)
                    return values[i];
            }
            return null;
		}
	}
	
	public FoxData() {
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
		
		ses.scheduleAtFixedRate(new Runnable() {
		    public void run() {
		    	doTick();
		    }
		}, 0, HISTORY_MILLISECONDS, TimeUnit.MILLISECONDS);
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
	
	public int getRedFarStars() {
		return this.redFarStars;
	}
	public void setRedFarStars(int num) {
		this.redFarStars = num;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getRedFarCubes() {
		return this.redFarCubes;
	}
	public void setRedFarCubes(int num) {
		this.redFarCubes = num;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getRedNearStars() {
		return this.redNearStars;
	}
	public void setRedNearStars(int num) {
		this.redNearStars = num;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getRedNearCubes() {
		return this.redNearCubes;
	}
	public void setRedNearCubes(int num) {
		this.redNearCubes = num;
		fireUpdate(UpdateType.SCORE);
	}
	
	public void setRedElevation(ElevatedState state) {
		this.redElevation = state;
		fireUpdate(UpdateType.SCORE);
	}
	
	public void setRedAuton(boolean auton) {
		this.redAuton = auton;
		
		if(this.redAuton)
			this.blueAuton = false;

		fireUpdate(UpdateType.SCORE);
	}
	
	public int getBlueFarStars() {
		return this.blueFarStars;
	}
	public void setBlueFarStars(int num) {
		this.blueFarStars = num;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getBlueFarCubes() {
		return this.blueFarCubes;
	}
	public void setBlueFarCubes(int num) {
		this.blueFarCubes = num;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getBlueNearStars() {
		return this.blueNearStars;
	}
	public void setBlueNearStars(int num) {
		this.blueNearStars = num;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getBlueNearCubes() {
		return this.blueNearCubes;
	}
	public void setBlueNearCubes(int num) {
		this.blueNearCubes = num;
		fireUpdate(UpdateType.SCORE);
	}
	
	public void setBlueElevation(ElevatedState state) {
		this.blueElevation = state;
		fireUpdate(UpdateType.SCORE);
	}
	
	public void setBlueAuton(boolean auton) {
		this.blueAuton = auton;
		
		if(this.blueAuton)
			this.redAuton = false;
		
		fireUpdate(UpdateType.SCORE);
	}
	
	public void setPaused(boolean paused) {
		this.isPaused = paused;
		fireUpdate(UpdateType.SETTING);
	}
	
	public void setShowHistory(boolean show) {
		this.showHistory = show;
		fireUpdate(UpdateType.SETTING);
	}
	
	public void setLargeHistory(boolean large) {
		this.largeHistory = large;
		fireUpdate(UpdateType.SETTING);
	}
	
	public int getRedScore() {
		int score = 0;
		score += redNearStars;
		score += redNearCubes * 2;
		score += redFarStars * 2;
		score += redFarCubes * 4;
		score += redAuton ? 4 : 0;
		score += redElevation == ElevatedState.HIGH ? 12 : redElevation == ElevatedState.LOW ? 4 : 0;
		return score;
	}
	
	public int getBlueScore() {
		int score = 0;
		score += blueNearStars;
		score += blueNearCubes * 2;
		score += blueFarStars * 2;
		score += blueFarCubes * 4;
		score += blueAuton ? 4 : 0;
		score += blueElevation == ElevatedState.HIGH ? 12 : blueElevation == ElevatedState.LOW ? 4 : 0;
		return score;
	}
	
	public int[] getRedScoreHistory() {
		int[] returnArray = new int[NUM_HISTORY_POINTS];
		
		int[] arrayOne = Arrays.copyOfRange(redScoreHistory, scoreHistoryPos, redScoreHistory.length);
		int[] arrayTwo = Arrays.copyOfRange(redScoreHistory, 0, scoreHistoryPos);
		
		if(redScoreHistory[scoreHistoryPos] > -1) {
			System.arraycopy(arrayOne, 0, returnArray, 0, arrayOne.length);
			System.arraycopy(arrayTwo, 0, returnArray, arrayOne.length, arrayTwo.length);
		}
		else {
			System.arraycopy(arrayTwo, 0, returnArray, 0, arrayTwo.length);
			System.arraycopy(arrayOne, 0, returnArray, arrayTwo.length, arrayOne.length);
		}
		
		return returnArray;
	}
	
	public int[] getBlueScoreHistory() {
		int[] returnArray = new int[NUM_HISTORY_POINTS];
		
		int[] arrayOne = Arrays.copyOfRange(blueScoreHistory, scoreHistoryPos, blueScoreHistory.length);
		int[] arrayTwo = Arrays.copyOfRange(blueScoreHistory, 0, scoreHistoryPos);

		if(blueScoreHistory[scoreHistoryPos] > -1) {
			System.arraycopy(arrayOne, 0, returnArray, 0, arrayOne.length);
			System.arraycopy(arrayTwo, 0, returnArray, arrayOne.length, arrayTwo.length);
		}
		else {
			System.arraycopy(arrayTwo, 0, returnArray, 0, arrayTwo.length);
			System.arraycopy(arrayOne, 0, returnArray, arrayTwo.length, arrayOne.length);
		}
		
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
		redFarStars = 0;
		redFarCubes = 0;
		redNearStars = 0;
		redNearCubes = 0;
		redAuton = false;
		redElevation = ElevatedState.NONE;
		
		blueFarStars = 0;
		blueFarCubes = 0;
		blueNearStars = 0;
		blueNearCubes = 0;
		blueAuton = false;
		blueElevation = ElevatedState.NONE;
		
		for(int i=0; i<NUM_HISTORY_POINTS; i++) {
			redScoreHistory[i] = -1;
			blueScoreHistory[i] = -1;
		}
		scoreHistoryPos = 0;
		
		fireUpdate(UpdateType.CLEAR);
		fireUpdate(UpdateType.TICK);
		fireUpdate(UpdateType.SCORE);
	}
}
