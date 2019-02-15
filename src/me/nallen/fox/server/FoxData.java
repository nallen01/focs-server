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
	
	private static final int AUTON_POINTS = 4;
	private static final int HIGH_FLAG_POINTS = 2;
	private static final int LOW_FLAG_POINTS = 1;
	private static final int HIGH_CAP_POINTS = 2;
	private static final int LOW_CAP_POINTS = 1;
	private static final int ALLIANCE_PARKED_POINTS = 3;
	private static final int CENTRE_PARKED_POINTS = 6;
	
	private ToggleState[][] highFlags = {
			{ToggleState.NONE, ToggleState.NONE, ToggleState.NONE},
			{ToggleState.NONE, ToggleState.NONE, ToggleState.NONE}
		};
	private ToggleState[] lowFlags = {ToggleState.NONE, ToggleState.NONE, ToggleState.NONE};
	
	private int redHighCaps = 0;
	private int redLowCaps = 0;
	private ParkingState[] redParking = {ParkingState.NONE, ParkingState.NONE};
	private boolean redAuton = false;
	
	private int blueHighCaps = 0;
	private int blueLowCaps = 0;
	private ParkingState[] blueParking = {ParkingState.NONE, ParkingState.NONE};
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
	
	public enum ToggleState {
	    NONE(0),
	    RED(1),
	    BLUE(2);
		
		private final int id;
		ToggleState(int id) { this.id = id; }
		public int getValue() { return id; }
		public static ToggleState fromInt(int id) {
			ToggleState[] values = ToggleState.values();
            for(int i=0; i<values.length; i++) {
                if(values[i].getValue() == id)
                    return values[i];
            }
            return null;
		}
	}
	
	public enum ParkingState {
	    NONE(0),
	    ALLIANCE_PARKED(1),
	    CENTRE_PARKED(2);
		
		private final int id;
		ParkingState(int id) { this.id = id; }
		public int getValue() { return id; }
		public static ParkingState fromInt(int id) {
			ParkingState[] values = ParkingState.values();
            for(int i=0; i<values.length; i++) {
                if(values[i].getValue() == id)
                    return values[i];
            }
            return null;
		}
		
		public int getScore() {
			switch(this) {
			case NONE: return 0;
			case ALLIANCE_PARKED: return ALLIANCE_PARKED_POINTS;
			case CENTRE_PARKED: return CENTRE_PARKED_POINTS;
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
	
	public ToggleState getHighFlag(int column, int row) {
		return this.highFlags[row][column];
	}
	public void setHighFlag(int column, int row, ToggleState toggleState) {
		this.highFlags[row][column] = toggleState;
		fireUpdate(UpdateType.SCORE);
	}
	
	public ToggleState getLowFlag(int column) {
		return this.lowFlags[column];
	}
	public void setLowFlag(int column, ToggleState toggleState) {
		this.lowFlags[column] = toggleState;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getRedHighCaps() {
		return this.redHighCaps;
	}
	public void setRedHighCaps(int caps) {
		this.redHighCaps = caps;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getRedLowCaps() {
		return this.redLowCaps;
	}
	public void setRedLowCaps(int caps) {
		this.redLowCaps = caps;
		fireUpdate(UpdateType.SCORE);
	}
	
	public ParkingState getRedParking(int index) {
		return this.redParking[index];
	}
	public void setRedParking(int index, ParkingState state) {
		this.redParking[index] = state;
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
	
	public int getBlueHighCaps() {
		return this.blueHighCaps;
	}
	public void setBlueHighCaps(int caps) {
		this.blueHighCaps = caps;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getBlueLowCaps() {
		return this.blueLowCaps;
	}
	public void setBlueLowCaps(int caps) {
		this.blueLowCaps = caps;
		fireUpdate(UpdateType.SCORE);
	}
	
	public ParkingState getBlueParking(int index) {
		return this.blueParking[index];
	}
	public void setBlueParking(int index, ParkingState state) {
		this.blueParking[index] = state;
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
	public int getRedScore() {
		int score = 0;
		
		// High Flags
		for(int i=0; i<2; i++) {
			for(int j=0; j<3; j++) {
				if(highFlags[i][j] == ToggleState.RED)
					score += HIGH_FLAG_POINTS;
			}
		}
		
		// Low Flags
		for(int i=0; i<3; i++) {
			if(lowFlags[i] == ToggleState.RED)
				score += LOW_FLAG_POINTS;
		}
		
		// Caps
		score += redHighCaps * HIGH_CAP_POINTS;
		score += redLowCaps * LOW_CAP_POINTS;
		
		// Parking
		for(int i=0; i<2; i++) {
			score += redParking[i].getScore();
		}
		
		// Auton
		score += redAuton ? AUTON_POINTS : 0;
		return score;
	}
	
	public int getBlueScore() {
		int score = 0;
		// High Flags
		for(int i=0; i<2; i++) {
			for(int j=0; j<3; j++) {
				if(highFlags[i][j] == ToggleState.BLUE)
					score += HIGH_FLAG_POINTS;
			}
		}
		
		// Low Flags
		for(int i=0; i<3; i++) {
			if(lowFlags[i] == ToggleState.BLUE)
				score += LOW_FLAG_POINTS;
		}
		
		// Caps
		score += blueHighCaps * HIGH_CAP_POINTS;
		score += blueLowCaps * LOW_CAP_POINTS;
		
		// Parking
		for(int i=0; i<2; i++) {
			score += blueParking[i].getScore();
		}
		
		// Auton
		score += blueAuton ? AUTON_POINTS : 0;
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
		
		for(int i=0; i<highFlags.length; i++) {
			for(int j=0; j<highFlags[i].length; j++) {
				builder.append(String.format("Flag [%d, %d],", i, j));
			}
		}
		for(int j=0; j<lowFlags.length; j++) {
			builder.append(String.format("Flag [%d, %d],", highFlags.length, j));
		}
		
		builder.append("Red High Caps,");
		builder.append("Red Low Caps,");
		for(int i=0; i<redParking.length; i++) {
			builder.append(String.format("Red Parking %d,", i));
		}
		builder.append("Red Auton,");

		builder.append("Blue High Caps,");
		builder.append("Blue Low Caps,");
		for(int i=0; i<blueParking.length; i++) {
			builder.append(String.format("Blue Parking %d,", i));
		}
		builder.append("Blue Auton,");
		
		return builder.toString();
	}
	
	public String getCurrentScoreFieldsAsCsvLine() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%d,%d,", getRedScore(), getBlueScore()));
		
		for(int i=0; i<highFlags.length; i++) {
			for(int j=0; j<highFlags[i].length; j++) {
				builder.append(String.format("%s,", highFlags[i][j].name()));
			}
		}
		for(int j=0; j<lowFlags.length; j++) {
			builder.append(String.format("%s,", lowFlags[j].name()));
		}
		
		
		builder.append(String.format("%d,", redHighCaps));
		builder.append(String.format("%d,", redLowCaps));
		for(int i=0; i<redParking.length; i++) {
			builder.append(String.format("%d,", redParking[i].getScore()));
		}
		builder.append(String.format("%d,", redAuton ? 1 : 0));
		
		builder.append(String.format("%d,", blueHighCaps));
		builder.append(String.format("%d,", blueLowCaps));
		for(int i=0; i<blueParking.length; i++) {
			builder.append(String.format("%d,", blueParking[i].getScore()));
		}
		builder.append(String.format("%d,", blueAuton ? 1 : 0));
		
		return builder.toString();
	}
	
	public void clear() {
		for(int i=0; i<highFlags.length; i++)
			Arrays.fill(highFlags[i], ToggleState.NONE);
		Arrays.fill(lowFlags, ToggleState.NONE);
		
		highFlags[0][0] = ToggleState.BLUE;
		highFlags[0][2] = ToggleState.RED;
		highFlags[1][0] = ToggleState.BLUE;
		highFlags[1][2] = ToggleState.RED;
		lowFlags[0] = ToggleState.BLUE;
		lowFlags[2] = ToggleState.RED;
		
		redHighCaps = 0;
		redLowCaps = 4;
		Arrays.fill(redParking, ParkingState.NONE);
		redAuton = false;
		

		blueHighCaps = 0;
		blueLowCaps = 4;
		Arrays.fill(blueParking, ParkingState.NONE);
		blueAuton = false;
		
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
