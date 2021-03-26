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
	
	private static final int BALL_POINTS = 1;
	private static final int ROW_POINTS = 6;
	
	private BallType[][] goalOwnership = {
			{
				BallType.NONE, BallType.NONE, BallType.NONE
			},{
				BallType.NONE, BallType.NONE, BallType.NONE
			},{
				BallType.NONE, BallType.NONE, BallType.NONE
			}
		};
	
	private AutonWinner autonWinner = AutonWinner.NONE;
	
	private int redBalls = 0;
	
	private int blueBalls = 0;

	private int[] redScoreHistory = new int[NUM_HISTORY_POINTS];
	private int[] blueScoreHistory = new int[NUM_HISTORY_POINTS];
	private int scoreHistoryPos = 0;
	
	private boolean isPaused = false;
	
	private HistoryMethod historyMethod = HistoryMethod.SIDE;
	private boolean isHidden = false;
	
	private boolean isThreeTeam = false;

	private LinkedList<DataListener> _listeners = new LinkedList<DataListener>();
	
	public enum HistoryMethod {
		NONE(0),
	    CORNER(1),
	    SIDE(2),
	    FULL(3);
		
		private final int id;
		HistoryMethod(int id) { this.id = id; }
		public int getValue() { return id; }
		public static HistoryMethod fromInt(int id) {
			HistoryMethod[] values = HistoryMethod.values();
            for(int i=0; i<values.length; i++) {
                if(values[i].getValue() == id)
                    return values[i];
            }
            return null;
		}
	}
	
	public enum BallType {
	    NONE(0),
	    RED(1),
	    BLUE(2);
		
		private final int id;
		BallType(int id) { this.id = id; }
		public int getValue() { return id; }
		public static BallType fromInt(int id) {
			BallType[] values = BallType.values();
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
	
	public HistoryMethod getHistoryMethod() {
		return historyMethod;
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
	
	public BallType getGoalOwnership(int x, int y) {
		return this.goalOwnership[x][y];
	}
	public void setGoalOwnership(int x, int y, BallType ballType) {
		this.goalOwnership[x][y] = ballType;
		fireUpdate(UpdateType.SCORE);
	}
	
	public AutonWinner getAutonWinner() {
		return this.autonWinner;
	}
	public void setAutonWinner(AutonWinner winner) {
		this.autonWinner = winner;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getRedBalls() {
		return this.redBalls;
	}
	public void setRedBalls(int balls) {
		this.redBalls = balls;
		fireUpdate(UpdateType.SCORE);
	}
	
	public int getBlueBalls() {
		return this.blueBalls;
	}
	public void setBlueBalls(int balls) {
		this.blueBalls = balls;
		fireUpdate(UpdateType.SCORE);
	}
	
	public void setPaused(boolean paused) {
		this.isPaused = paused;
		fireUpdate(UpdateType.SETTING);
	}
	
	public void setHistoryMethod(HistoryMethod method) {
		this.historyMethod = method;
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
	
	public int getRows(BallType ballType) {
		int count = 0;
		
		// Check horizontal rows
		for(int i=0; i<goalOwnership.length; i++) {
			boolean valid = true;
			for(int j=0; j<goalOwnership.length; j++) {
				if(goalOwnership[i][j] != ballType) {
					valid = false;
					break;
				}
			}
			
			if(valid) {
				count += 1;
			}
		}
		
		// Check vertical columns
		for(int i=0; i<goalOwnership.length; i++) {
			boolean valid = true;
			for(int j=0; j<goalOwnership.length; j++) {
				if(goalOwnership[j][i] != ballType) {
					valid = false;
					break;
				}
			}
			
			if(valid) {
				count += 1;
			}
		}

		// Check first diagonal
		boolean valid = true;
		for(int i=0; i<goalOwnership.length; i++) {
			if(goalOwnership[i][i] != ballType) {
				valid = false;
				break;
			}
		}
		
		if(valid) {
			count += 1;
		}
		
		// Check other diagonal
		valid = true;
		for(int i=0; i<goalOwnership.length; i++) {
			if(goalOwnership[goalOwnership.length-i][i] != ballType) {
				valid = false;
				break;
			}
		}
		
		if(valid) {
			count += 1;
		}
		
		return count;
	}
	
	public int getRedRows() {
		return getRows(BallType.RED);
	}
	
	public int getBlueRows() {
		return getRows(BallType.BLUE);
	}
	
	public int getRedScore() {
		int score = 0;
		
		score += BALL_POINTS * getRedBalls();
		score += ROW_POINTS * getRedRows();
		
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
		
		score += BALL_POINTS * getBlueBalls();
		score += ROW_POINTS * getBlueRows();
		
		if(getAutonWinner() == AutonWinner.BLUE) {
			score += AUTON_WIN_POINTS;
		}
		else if(getAutonWinner() == AutonWinner.TIE) {
			score += AUTON_TIE_POINTS;
		}
		
		return score;
	}
	
	public int getMaxRedScore() {
		int max = 0;
		
		for(int i=0; i<redScoreHistory.length; i++) {
			if(redScoreHistory[i] > max)
				max = redScoreHistory[i];
		}
		
		return max;
	}
	
	public int getMaxBlueScore() {
		int max = 0;
		
		for(int i=0; i<blueScoreHistory.length; i++) {
			if(blueScoreHistory[i] > max)
				max = blueScoreHistory[i];
		}
		
		return max;
	}
	
	public int getMaxScore() {
		int red = getMaxRedScore();
		int blue = getMaxBlueScore();
		
		if(red > blue)
			return red;
		
		return blue;
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
		
		for(int i=0; i<goalOwnership.length; i++) {
			for(int j=0; j<goalOwnership[i].length; j++) {
				builder.append(String.format("Goal %d-%d Ownership,", i, j));
			}
		}
		
		builder.append("Auton,");
		
		builder.append("Red Balls,");

		builder.append("Blue Balls,");
		
		return builder.toString();
	}
	
	public String getCurrentScoreFieldsAsCsvLine() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%d,%d,", getRedScore(), getBlueScore()));
		
		for(int i=0; i<goalOwnership.length; i++) {
			for(int j=0; j<goalOwnership[i].length; j++) {
				builder.append(String.format("%s,", goalOwnership[i][j].name()));
			}
		}

		builder.append(String.format("%s,", autonWinner.name()));
		
		builder.append(String.format("%d,", redBalls));
		
		builder.append(String.format("%d,", blueBalls));
		
		return builder.toString();
	}
	
	public void clear() {
		goalOwnership[0][0] = BallType.RED;
		goalOwnership[0][1] = BallType.BLUE;
		goalOwnership[0][2] = BallType.BLUE;
		goalOwnership[1][0] = BallType.RED;
		goalOwnership[1][1] = BallType.NONE;
		goalOwnership[1][2] = BallType.BLUE;
		goalOwnership[2][0] = BallType.RED;
		goalOwnership[2][1] = BallType.RED;
		goalOwnership[2][2] = BallType.BLUE;
		
		autonWinner = AutonWinner.NONE;
		
		redBalls = 0;
		
		blueBalls = 0;
		
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
