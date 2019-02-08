package me.nallen.fox.server.eventmanager.scoring;

import me.nallen.fox.server.eventmanager.GameType;

public abstract class MatchScoring {
	protected static final int NUM_BASE_FIELDS = 8;
	public boolean HAS_AUTON = false;
	public GameType GAME_TYPE = GameType.BASIC;
	
	protected final int[] scores = new int[NUM_BASE_FIELDS+1];
	
	private static final int R1_DQ = 0;
	private static final int R1_NS = 1;
	private static final int R2_DQ = 2;
	private static final int R2_NS = 3;
	
	private static final int B1_DQ = 4;
	private static final int B1_NS = 5;
	private static final int B2_DQ = 6;
	private static final int B2_NS = 7;
	
	protected static final int IS_DETAILED = 8;
	
	protected void initBase(String[] parts) {
		if(parts.length != NUM_BASE_FIELDS) {
			throw new IllegalArgumentException("Array must have " + NUM_BASE_FIELDS + " elements");
		}
		for(int i=0; i<NUM_BASE_FIELDS; i++) {
			try {
				scores[i] = Integer.parseInt(parts[i]);
			}
			catch(NumberFormatException ignored) {
				scores[i] = Boolean.parseBoolean(parts[i]) ? 1 : 0;
			}
		}
	}
	
	public int[] getRedDQ() {
		return new int[] { scores[R1_DQ], scores[R2_DQ] };
	}
	public void setRedDQ(int robot, int val) {
		val = (val == 1) ? 1 : 0;
		if(robot == 0) {
			scores[R1_DQ] = val;
		}
		else if(robot == 1) {
			scores[R2_DQ] = val;
		}
	}
	public int[] getRedNS() {
		return new int[] { scores[R1_NS], scores[R2_NS] };
	}
	public void setRedNS(int robot, int val) {
		val = (val == 1) ? 1 : 0;
		if(robot == 0) {
			scores[R1_NS] = val;
		}
		else if(robot == 1) {
			scores[R2_NS] = val;
		}
	}
	public int getRedAuton() {
		return 0;
	}
	public void setRedAuton(int val) {
	}
	
	public int[] getBlueDQ() {
		return new int[] { scores[B1_DQ], scores[B2_DQ] };
	}
	public void setBlueDQ(int robot, int val) {
		val = (val == 1) ? 1 : 0;
		if(robot == 0) {
			scores[B1_DQ] = val;
		}
		else if(robot == 1) {
			scores[B2_DQ] = val;
		}
	}
	public int[] getBlueNS() {
		return new int[] { scores[B1_NS], scores[B2_NS] };
	}
	public void setBlueNS(int robot, int val) {
		val = (val == 1) ? 1 : 0;
		if(robot == 0) {
			scores[B1_NS] = val;
		}
		else if(robot == 1) {
			scores[B2_NS] = val;
		}
	}
	public int getBlueAuton() {
		return 0;
	}
	public void setBlueAuton(int val) {
	}
	
	protected int[] getFieldScores() {
		return new int[]{ 0, 0 };
	}
	public int[] getScores(boolean isIq) {
		int[] tmp = getFieldScores();
		
		if(((scores[R1_DQ] == 1) || (scores[R1_NS] == 1)) && (isIq || (scores[R2_DQ] == 1) || (scores[R2_NS] == 1))) {
			tmp[0] = 0;
		}
		if(((scores[B1_DQ] == 1) || (scores[B1_NS] == 1)) && (isIq || (scores[B2_DQ] == 1) || (scores[B2_NS] == 1))) {
			tmp[1] = 0;
		}
		
		return tmp;
	}
	
	protected String getFieldSendingFormat() {
        return "";
	}
	public String getSendingFormat() {
		String tmp = "";
		tmp += scores[IS_DETAILED] + "" + ((char)29);
		tmp += getFieldSendingFormat();
		for(int i=0; i<NUM_BASE_FIELDS; i++) {
			if(tmp.length() != 0) {
				tmp += ((char)29);
			}
			tmp += scores[i];
		}
		return tmp;
	}
}
