package me.nallen.fox.server.eventmanager.scoring;

import java.util.Arrays;

import me.nallen.fox.server.eventmanager.GameType;
import me.nallen.fox.server.eventmanager.scoring.MatchScoring;

public class MatchScoringTossUp extends MatchScoring {
	public static final int MAX_BIG = 4;
	public static final int MAX_BUCKY = 10;
	private static final int NUM_FIELDS = 30;
	public static final int NUM_TOTAL_FIELDS = NUM_FIELDS + NUM_BASE_FIELDS;
	
	private final int[] field_scores = new int[NUM_FIELDS];
	private static final int R_BUCKY_GOAL = 2;
	private static final int R_BUCKY_MID = 4;
	private static final int R_BUCKY_STASH = 6;
	private static final int R_BIG_GOAL = 8;
	private static final int R_BIG_MID = 10;
	private static final int R_BIG_STASH = 12;
	
	private static final int B_BUCKY_GOAL = 3;
	private static final int B_BUCKY_MID = 5;
	private static final int B_BUCKY_STASH = 7;
	private static final int B_BIG_GOAL = 9;
	private static final int B_BIG_MID = 11;
	private static final int B_BIG_STASH = 13;
	
	private static final int R1_BALL = 14;
	private static final int R1_HIGH = 16;
	private static final int R1_LOW = 18;
	private static final int R1_NO = 20;
	private static final int R2_BALL = 22;
	private static final int R2_HIGH = 24;
	private static final int R2_LOW = 26;
	private static final int R2_NO = 28;
	
	private static final int B1_BALL = 15;
	private static final int B1_HIGH = 17;
	private static final int B1_LOW = 19;
	private static final int B1_NO = 21;
	private static final int B2_BALL = 23;
	private static final int B2_HIGH = 25;
	private static final int B2_LOW = 27;
	private static final int B2_NO = 29;
	
	private static final int R_AUTON = 0;
	private static final int B_AUTON = 1;

	public MatchScoringTossUp(String[] parts) {
		HAS_AUTON = true;
		GAME_TYPE = GameType.TOSS_UP;
		scores[IS_DETAILED] = 0;
		if(parts.length != NUM_TOTAL_FIELDS) {
			throw new IllegalArgumentException("Array must have " + NUM_TOTAL_FIELDS + " elements");
		}
		initBase(Arrays.copyOfRange(parts, NUM_FIELDS, parts.length));
		for(int i=0; i<NUM_FIELDS; i++) {
			field_scores[i] = Integer.parseInt(parts[i]);
			if(i == R_AUTON || i == B_AUTON) {
				field_scores[i] = (field_scores[i] == 1) ? 1 : 0;
			}
		}
	}
	public MatchScoringTossUp(String init) {
		this(init.split(","));
	}
	
	public MatchScoringTossUp() {
		HAS_AUTON = true;
		GAME_TYPE = GameType.TOSS_UP;
		for(int i=0; i<field_scores.length; i++) {
			field_scores[i] = 0;
		}
		field_scores[R1_NO] = 1;
		field_scores[R2_NO] = 1;
		field_scores[B1_NO] = 1;
		field_scores[B2_NO] = 1;
	}
	
	public int getRedBuckyGoal() {
		return field_scores[R_BUCKY_GOAL];
	}
	public void setRedBuckyGoal(int value) {
		field_scores[R_BUCKY_GOAL] = value;
	}
	public int getRedBuckyStashed() {
		return field_scores[R_BUCKY_STASH];
	}
	public void setRedBuckyStashed(int value) {
		field_scores[R_BUCKY_STASH] = value;
	}
	public int getRedBuckyMiddle() {
		return field_scores[R_BUCKY_MID];
	}
	public void setRedBuckyMiddle(int value) {
		field_scores[R_BUCKY_MID] = value;
	}
	public int getRedBigGoal() {
		return field_scores[R_BIG_GOAL];
	}
	public void setRedBigGoal(int value) {
		field_scores[R_BIG_GOAL] = value;
	}
	public int getRedBigStashed() {
		return field_scores[R_BIG_STASH];
	}
	public void setRedBigStashed(int value) {
		field_scores[R_BIG_STASH] = value;
	}
	public int getRedBigMiddle() {
		return field_scores[R_BIG_MID];
	}
	public void setRedBigMiddle(int value) {
		field_scores[R_BIG_MID] = value;
	}
	
	public int[][] getRedHanging() {
		return new int[][] { { field_scores[R1_LOW] + 2*field_scores[R1_HIGH], field_scores[R1_BALL] }, { field_scores[R2_LOW] + 2*field_scores[R2_HIGH], field_scores[R2_BALL] } };
	}
	public void setRedHangingHeight(int robot, int val) {
		val = (val > 2) ? 2 : (val < 0) ? 0 : val;
		int[] split = { (val == 0) ? 1 : 0, val % 2, val / 2 };
		split[2] = (split[2] >= 1) ? 1 : 0;
		if(robot == 0) {
			field_scores[R1_NO] = split[0];
			field_scores[R1_LOW] = split[1];
			field_scores[R1_HIGH] = split[2];
		}
		else if(robot == 1) {
			field_scores[R1_NO] = split[0];
			field_scores[R2_LOW] = split[1];
			field_scores[R2_HIGH] = split[2];
		}
	}
	public void setRedHangingBall(int robot, int val) {
		val = (val == 1) ? 1 : 0;
		if(robot == 0) {
			field_scores[R1_BALL] = val;
		}
		else if(robot == 1) {
			field_scores[R2_BALL] = val;
		}
	}
	
	public int getRedAuton() {
		return field_scores[R_AUTON];
	}
	public void setRedAuton(int value) {
		field_scores[R_AUTON] = (value == 1) ? 1 : 0;
	}
	
	public int getBlueBuckyGoal() {
		return field_scores[B_BUCKY_GOAL];
	}
	public void setBlueBuckyGoal(int value) {
		field_scores[B_BUCKY_GOAL] = value;
	}
	public int getBlueBuckyStashed() {
		return field_scores[B_BUCKY_STASH];
	}
	public void setBlueBuckyStashed(int value) {
		field_scores[B_BUCKY_STASH] = value;
	}
	public int getBlueBuckyMiddle() {
		return field_scores[B_BUCKY_MID];
	}
	public void setBlueBuckyMiddle(int value) {
		field_scores[B_BUCKY_MID] = value;
	}
	public int getBlueBigGoal() {
		return field_scores[B_BIG_GOAL];
	}
	public void setBlueBigGoal(int value) {
		field_scores[B_BIG_GOAL] = value;
	}
	public int getBlueBigStashed() {
		return field_scores[B_BIG_STASH];
	}
	public void setBlueBigStashed(int value) {
		field_scores[B_BIG_STASH] = value;
	}
	public int getBlueBigMiddle() {
		return field_scores[B_BIG_MID];
	}
	public void setBlueBigMiddle(int value) {
		field_scores[B_BIG_MID] = value;
	}
	
	public int[][] getBlueHanging() {
		return new int[][] { { field_scores[B1_LOW] + 2*field_scores[B1_HIGH], field_scores[B1_BALL] }, { field_scores[B2_LOW] + 2*field_scores[B2_HIGH], field_scores[B2_BALL] } };
	}
	public void setBlueHangingHeight(int robot, int val) {
		val = (val > 2) ? 2 : (val < 0) ? 0 : val;
		int[] split = { (val == 0) ? 1 : 0, val % 2, val / 2 };
		split[2] = (split[2] >= 1) ? 1 : 0;
		if(robot == 0) {
			field_scores[B1_NO] = split[0];
			field_scores[B1_LOW] = split[1];
			field_scores[B1_HIGH] = split[2];
		}
		else if(robot == 1) {
			field_scores[B1_NO] = split[0];
			field_scores[B2_LOW] = split[1];
			field_scores[B2_HIGH] = split[2];
		}
	}
	public void setBlueHangingBall(int robot, int val) {
		val = (val == 1) ? 1 : 0;
		if(robot == 0) {
			field_scores[B1_BALL] = val;
		}
		else if(robot == 1) {
			field_scores[B2_BALL] = val;
		}
	}
	
	public int getBlueAuton() {
		return field_scores[B_AUTON];
	}
	public void setBlueAuton(int value) {
		field_scores[B_AUTON] = (value == 1) ? 1 : 0;
	}
	
	protected int[] getFieldScores() {
		int[] tmp = { 0, 0 };
		tmp[0] += 5*field_scores[R_BUCKY_STASH] + 2*field_scores[R_BUCKY_GOAL] + field_scores[R_BUCKY_MID];
		tmp[0] += 10*field_scores[R_BIG_STASH] + 5*field_scores[R_BIG_GOAL] + field_scores[R_BIG_MID];
		tmp[0] += 10*field_scores[R1_HIGH] + 5*field_scores[R1_LOW] + 10*field_scores[R1_BALL];
		tmp[0] += 10*field_scores[R2_HIGH] + 5*field_scores[R2_LOW] + 10*field_scores[R2_BALL];
		tmp[0] += 10*field_scores[R_AUTON];
		
		tmp[1] += 5*field_scores[B_BUCKY_STASH] + 2*field_scores[B_BUCKY_GOAL] + field_scores[B_BUCKY_MID];
		tmp[1] += 10*field_scores[B_BIG_STASH] + 5*field_scores[B_BIG_GOAL] + field_scores[B_BIG_MID];
		tmp[1] += 10*field_scores[B1_HIGH] + 5*field_scores[B1_LOW] + 10*field_scores[B1_BALL];
		tmp[1] += 10*field_scores[B2_HIGH] + 5*field_scores[B2_LOW] + 10*field_scores[B2_BALL];
		tmp[1] += 10*field_scores[B_AUTON];
		
		return tmp;
	}
	
	public int getRedBigTotal() {
		return field_scores[R_BIG_STASH] + field_scores[R_BIG_GOAL] + field_scores[R_BIG_MID] + field_scores[R1_BALL] + field_scores[R2_BALL];
	}
	public int getRedBuckyTotal() {
		return field_scores[R_BUCKY_STASH] + field_scores[R_BUCKY_GOAL] + field_scores[R_BUCKY_MID];
	}
	
	public int getBlueBigTotal() {
		return field_scores[B_BIG_STASH] + field_scores[B_BIG_GOAL] + field_scores[B_BIG_MID] + field_scores[B1_BALL] + field_scores[B2_BALL];
	}
	public int getBlueBuckyTotal() {
		return field_scores[B_BUCKY_STASH] + field_scores[B_BUCKY_GOAL] + field_scores[B_BUCKY_MID];
	}
	
	protected String getFieldSendingFormat() {
		String tmp = "";
		for(int i=0; i<NUM_FIELDS; i++) {
			if(i != 0) {
				tmp += ((char)29);
			}
			tmp += field_scores[i];
		}
		return tmp;
	}
}
