package me.nallen.fox.server;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.nallen.fox.server.DataListener.UpdateType;

public class FoxData {
	public static final int HISTORY_SECONDS = 120;
	public static final double HISTORY_FREQUENCY = 10;
	public static final int NUM_HISTORY_POINTS = (int) (HISTORY_SECONDS * HISTORY_FREQUENCY) + 1;
	public static final int HISTORY_MILLISECONDS = (int) (1000 / HISTORY_FREQUENCY);
	
	private int[] redBaseCones = {0, 0, 0, 0};
	private ScoringZone[] redBaseZones = {ScoringZone.NONE, ScoringZone.NONE, ScoringZone.NONE, ScoringZone.NONE};
	private int redStationaryCones = 0;
	private int redParking = 0;
	private boolean redAuton = false;
	
	private int[] blueBaseCones = {0, 0, 0, 0};
	private ScoringZone[] blueBaseZones = {ScoringZone.NONE, ScoringZone.NONE, ScoringZone.NONE, ScoringZone.NONE};
	private int blueStationaryCones = 0;
	private int blueParking = 0;
	private boolean blueAuton = false;

	private int[] redScoreHistory = new int[NUM_HISTORY_POINTS];
	private int[] blueScoreHistory = new int[NUM_HISTORY_POINTS];
	private int scoreHistoryPos = 0;
	
	private boolean isPaused = false;
	
	private boolean showHistory = true;
	private boolean largeHistory = true;
	private boolean isHidden = false;
	
	private boolean isThreeTeam = false;

	private LinkedList<DataListener> _listeners = new LinkedList<DataListener>();
	
	public enum ScoringZone {
	    NONE(0),
	    FIVE_POINT(1),
	    TEN_POINT(2),
	    TWENTY_POINT(3);
		
		private final int id;
		ScoringZone(int id) { this.id = id; }
		public int getValue() { return id; }
		public static ScoringZone fromInt(int id) {
			ScoringZone[] values = ScoringZone.values();
            for(int i=0; i<values.length; i++) {
                if(values[i].getValue() == id)
                    return values[i];
            }
            return null;
		}
		
		public int getScore() {
			switch(this) {
			case NONE: return 0;
			case FIVE_POINT: return 5;
			case TEN_POINT: return 10;
			case TWENTY_POINT: return 20;
			default: return 0;
			}
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
	
	public boolean getHidden() {
		return isHidden;
	}
	
	public boolean getThreeTeam() {
		return isThreeTeam;
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
	
	public int getRedBaseCones(int index) {
		return this.redBaseCones[index];
	}
	public void setRedBaseCones(int index, int num) {
		this.redBaseCones[index] = num;
		fireUpdate(UpdateType.SCORE);
	}
	
	public ScoringZone getRedBaseZone(int index) {
		return this.redBaseZones[index];
	}
	public void setRedBaseZone(int index, ScoringZone zone) {
		this.redBaseZones[index] = zone;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getRedStationaryCones() {
		return this.redStationaryCones;
	}
	public void setRedStationaryCones(int num) {
		this.redStationaryCones = num;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getRedParking() {
		return this.redParking;
	}
	public void setRedParking(int num) {
		this.redParking = num;
		fireUpdate(UpdateType.SCORE);
	}
	
	public boolean getRedAuton() {
		return this.redAuton;
	}
	public void setRedAuton(boolean auton) {
		this.redAuton = auton;
		
		if(this.redAuton)
			this.blueAuton = false;

		fireUpdate(UpdateType.SCORE);
	}
	
	public int getBlueBaseCones(int index) {
		return this.blueBaseCones[index];
	}
	public void setBlueBaseCones(int index, int num) {
		this.blueBaseCones[index] = num;
		fireUpdate(UpdateType.SCORE);
	}
	
	public ScoringZone getBlueBaseZone(int index) {
		return this.blueBaseZones[index];
	}
	public void setBlueBaseZone(int index, ScoringZone zone) {
		this.blueBaseZones[index] = zone;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getBlueStationaryCones() {
		return this.blueStationaryCones;
	}
	public void setBlueStationaryCones(int num) {
		this.blueStationaryCones = num;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getBlueParking() {
		return this.blueParking;
	}
	public void setBlueParking(int num) {
		this.blueParking = num;
		fireUpdate(UpdateType.SCORE);
	}
	
	public boolean getBlueAuton() {
		return this.blueAuton;
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
	
	public void setHidden(boolean hidden) {
		this.isHidden = hidden;
		fireUpdate(UpdateType.SETTING);
	}
	
	public void setThreeTeam(boolean threeTeam) {
		this.isThreeTeam = threeTeam;
		fireUpdate(UpdateType.SETTING);
	}
	
	private int getHighestStacks(int alliance) {
		int[][] highestStacks = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};
		
		for(int i=0; i<4; i++) {
			if(redBaseZones[i] != ScoringZone.NONE) {
				int zone = redBaseZones[i].getValue();
				if(redBaseCones[i] > highestStacks[0][zone])
					highestStacks[0][zone] = redBaseCones[i];
			}
			
			if(blueBaseZones[i] != ScoringZone.NONE) {
				int zone = blueBaseZones[i].getValue();
				if(blueBaseCones[i] > highestStacks[1][zone])
					highestStacks[1][zone] = blueBaseCones[i];
			}
		}
		
		highestStacks[0][4] = redStationaryCones;
		highestStacks[1][4] = blueStationaryCones;
		
		int count = 0;
		for(int i=0; i<5; i++) {
			if(highestStacks[alliance][i] > highestStacks[1-alliance][i])
				count++;
		}
		
		return count;
	}
	
	public int getRedHighestStacks() {
		return getHighestStacks(0);
	}
	
	public int getBlueHighestStacks() {
		return getHighestStacks(1);
	}
	
	public int getRedScore() {
		int score = 0;
		for(int i=0; i<4; i++) {
			score += redBaseCones[i] * 2;
			score += redBaseZones[i].getScore();
		}
		score += redStationaryCones * 2;
		
		score += getRedHighestStacks() * 5;
		
		score += redParking * 2;
		score += redAuton ? 10 : 0;
		return score;
	}
	
	public int getBlueScore() {
		int score = 0;
		for(int i=0; i<4; i++) {
			score += blueBaseCones[i] * 2;
			score += blueBaseZones[i].getScore();
		}
		score += blueStationaryCones * 2;
		
		score += getBlueHighestStacks() * 5;
		
		score += blueParking * 2;
		score += blueAuton ? 10 : 0;
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
		}
		else {
			int writePos = scoreHistoryPos - 1;
			if(writePos < 0)
				writePos += NUM_HISTORY_POINTS;
			
			redScoreHistory[writePos] = getRedScore();
			blueScoreHistory[writePos] = getBlueScore();
		}
		
		fireUpdate(UpdateType.TICK);
	}
	
	public String getScoreFieldTitlesAsCsvLine() {
		StringBuilder builder = new StringBuilder();
		builder.append("Red Score,Blue Score,");
		for(int i=0; i<redBaseCones.length; i++) {
			builder.append(String.format("Red Mogo %d Cones,Red Mogo %d Zone,", i+1, i+1));
		}
		builder.append("Red Stationary Cones,");
		builder.append("Red Auton,");
		builder.append("Red Parking,");

		for(int i=0; i<blueBaseCones.length; i++) {
			builder.append(String.format("Blue Mogo %d Cones,Blue Mogo %d Zone,", i+1, i+1));
		}
		builder.append("Blue Stationary Cones,");
		builder.append("Blue Auton,");
		builder.append("Blue Parking");
		
		return builder.toString();
	}
	
	public String getCurrentScoreFieldsAsCsvLine() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%d,%d,", getRedScore(), getBlueScore()));
		for(int i=0; i<redBaseCones.length; i++) {
			builder.append(String.format("%d,%d,", redBaseCones[i], redBaseZones[i].getScore()));
		}
		builder.append(String.format("%d,", redStationaryCones));
		builder.append(String.format("%d,", redAuton ? 1 : 0));
		builder.append(String.format("%d,", redParking));
		
		for(int i=0; i<blueBaseCones.length; i++) {
			builder.append(String.format("%d,%d,", blueBaseCones[i], blueBaseZones[i].getScore()));
		}
		builder.append(String.format("%d,", blueStationaryCones));
		builder.append(String.format("%d,", blueAuton ? 1 : 0));
		builder.append(String.format("%d", blueParking));
		
		return builder.toString();
	}
	
	public void clear() {
		Arrays.fill(redBaseCones, 0);
		Arrays.fill(redBaseZones, ScoringZone.NONE);
		redStationaryCones = 0;
		redAuton = false;
		redParking = 0;
		

		Arrays.fill(blueBaseCones, 0);
		Arrays.fill(blueBaseZones, ScoringZone.NONE);
		blueStationaryCones = 0;
		blueAuton = false;
		blueParking = 0;
		
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
