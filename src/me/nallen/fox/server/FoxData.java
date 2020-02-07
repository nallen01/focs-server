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
	
	private static final int AUTON_TIE_POINTS = 3;
	private static final int AUTON_WIN_POINTS = 6;
	
	private CubeType[] towerCubes = {
			CubeType.NONE, CubeType.NONE, CubeType.NONE, CubeType.NONE, CubeType.NONE, CubeType.NONE, CubeType.NONE
		};
	
	private AutonWinner autonWinner = AutonWinner.NONE;
	
	private int redOrangeCubes = 0;
	private int redGreenCubes = 0;
	private int redPurpleCubes = 0;
	
	private int blueOrangeCubes = 0;
	private int blueGreenCubes = 0;
	private int bluePurpleCubes = 0;

	private int[] redScoreHistory = new int[NUM_HISTORY_POINTS];
	private int[] blueScoreHistory = new int[NUM_HISTORY_POINTS];
	private int scoreHistoryPos = 0;
	
	private boolean isPaused = false;
	
	private boolean showHistory = true;
	private boolean largeHistory = true;
	private boolean isHidden = false;
	
	private boolean isThreeTeam = false;

	private LinkedList<DataListener> _listeners = new LinkedList<DataListener>();
	
	public enum CubeType {
	    NONE(0),
	    ORANGE(1),
	    GREEN(2),
	    PURPLE(3);
		
		private final int id;
		CubeType(int id) { this.id = id; }
		public int getValue() { return id; }
		public static CubeType fromInt(int id) {
			CubeType[] values = CubeType.values();
            for(int i=0; i<values.length; i++) {
                if(values[i].getValue() == id)
                    return values[i];
            }
            return null;
		}
	}
	
	public enum AutonWinner {
	    NONE(0),
	    RED(1),
	    BLUE(2),
	    TIE(3);
		
		private final int id;
		AutonWinner(int id) { this.id = id; }
		public int getValue() { return id; }
		public static AutonWinner fromInt(int id) {
			AutonWinner[] values = AutonWinner.values();
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
	
	public CubeType getTowerCube(int pos) {
		return this.towerCubes[pos];
	}
	public void setTowerCube(int pos, CubeType cubeType) {
		this.towerCubes[pos] = cubeType;
		fireUpdate(UpdateType.SCORE);
	}
	
	public AutonWinner getAutonWinner() {
		return this.autonWinner;
	}
	public void setAutonWinner(AutonWinner winner) {
		this.autonWinner = winner;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getRedOrangeCubes() {
		return this.redOrangeCubes;
	}
	public void setRedOrangeCubes(int cubes) {
		this.redOrangeCubes = cubes;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getRedPurpleCubes() {
		return this.redPurpleCubes;
	}
	public void setRedPurpleCubes(int cubes) {
		this.redPurpleCubes = cubes;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getRedGreenCubes() {
		return this.redGreenCubes;
	}
	public void setRedGreenCubes(int cubes) {
		this.redGreenCubes = cubes;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getBlueOrangeCubes() {
		return this.blueOrangeCubes;
	}
	public void setBlueOrangeCubes(int cubes) {
		this.blueOrangeCubes = cubes;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getBluePurpleCubes() {
		return this.bluePurpleCubes;
	}
	public void setBluePurpleCubes(int cubes) {
		this.bluePurpleCubes = cubes;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getBlueGreenCubes() {
		return this.blueGreenCubes;
	}
	public void setBlueGreenCubes(int cubes) {
		this.blueGreenCubes = cubes;
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
	
	public int getTowerCubeTotal(CubeType cubeType) {
		int count = 0;
		for(int i=0; i<this.towerCubes.length; i++) {
			if(this.towerCubes[i] == cubeType)
				count++;
		}
		
		return count;
	}
	
	public int getRedScore() {
		int score = 0;
		
		score += getRedOrangeCubes() * (1 + getTowerCubeTotal(CubeType.ORANGE));
		score += getRedGreenCubes() * (1 + getTowerCubeTotal(CubeType.GREEN));
		score += getRedPurpleCubes() * (1 + getTowerCubeTotal(CubeType.PURPLE));
		
		if(getAutonWinner() == AutonWinner.RED) {
			score += AUTON_WIN_POINTS;
		}
		else if(getAutonWinner() == AutonWinner.TIE) {
			score += AUTON_TIE_POINTS;
		}
		
		return score;
	}
	
	public int getBlueScore() {
		int score = 0;
		
		score += getBlueOrangeCubes() * (1 + getTowerCubeTotal(CubeType.ORANGE));
		score += getBlueGreenCubes() * (1 + getTowerCubeTotal(CubeType.GREEN));
		score += getBluePurpleCubes() * (1 + getTowerCubeTotal(CubeType.PURPLE));
		
		if(getAutonWinner() == AutonWinner.BLUE) {
			score += AUTON_WIN_POINTS;
		}
		else if(getAutonWinner() == AutonWinner.TIE) {
			score += AUTON_TIE_POINTS;
		}
		
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
		
		for(int i=0; i<towerCubes.length; i++) {
			builder.append(String.format("Tower Cube [%d],", i));
		}
		
		builder.append("Auton,");
		
		builder.append("Red Orange Cubes,");
		builder.append("Red Green Cubes,");
		builder.append("Red Purple Cubes,");

		builder.append("Blue Orange Cubes,");
		builder.append("Blue Green Cubes,");
		builder.append("Blue Purple Cubes,");
		
		return builder.toString();
	}
	
	public String getCurrentScoreFieldsAsCsvLine() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%d,%d,", getRedScore(), getBlueScore()));
		
		for(int i=0; i<towerCubes.length; i++) {
			builder.append(String.format("%s,", towerCubes[i].name()));
		}

		builder.append(String.format("%s,", autonWinner.name()));
		
		builder.append(String.format("%d,", redOrangeCubes));
		builder.append(String.format("%d,", redGreenCubes));
		builder.append(String.format("%d,", redPurpleCubes));
		
		builder.append(String.format("%d,", blueOrangeCubes));
		builder.append(String.format("%d,", blueGreenCubes));
		builder.append(String.format("%d,", bluePurpleCubes));
		
		return builder.toString();
	}
	
	public void clear() {
		Arrays.fill(towerCubes, CubeType.NONE);
		
		autonWinner = AutonWinner.NONE;
		
		redOrangeCubes = 0;
		redGreenCubes = 0;
		redPurpleCubes = 0;
		
		blueOrangeCubes = 0;
		blueGreenCubes = 0;
		bluePurpleCubes = 0;
		
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
