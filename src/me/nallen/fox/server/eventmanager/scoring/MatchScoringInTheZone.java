package me.nallen.fox.server.eventmanager.scoring;

import java.util.Arrays;

import me.nallen.fox.server.eventmanager.GameType;
import me.nallen.fox.server.eventmanager.scoring.MatchScoring;

public class MatchScoringInTheZone extends MatchScoring {
	static final int MAX_CONES = 80;
	static final int MAX_BASES = 4;
	static final int MAX_HIGHEST_STACKS = 4;
	static final int MAX_PARKING = 2;
	static final int MAX_TWENTY_POINT_BASES = 1;
	private static final int NUM_FIELDS = 14;
	public static final int NUM_TOTAL_FIELDS = NUM_FIELDS + NUM_BASE_FIELDS;

	private int[] field_scores = new int[NUM_FIELDS];
	private static final int R_FIVEPOINTZONE = 2;
	private static final int B_FIVEPOINTZONE = 3;
	private static final int R_TENPOINTZONE = 10;
	private static final int B_TENPOINTZONE = 11;
	private static final int R_TWENTYPOINTZONE = 12;
	private static final int B_TWENTYPOINTZONE = 13;

	private static final int R_HIGHESTSTACKS = 4;
	private static final int B_HIGHESTSTACKS = 5;
	private static final int R_STACKEDCONES = 8;
	private static final int B_STACKEDCONES = 9;

	private static final int R_PARKED = 6;
	private static final int B_PARKED = 7;

	private static final int R_AUTON = 0;
	private static final int B_AUTON = 1;

	public MatchScoringInTheZone(String[] parts) {
		HAS_AUTON = true;
		GAME_TYPE = GameType.IN_THE_ZONE;
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
	public MatchScoringInTheZone(String init) {
		this(init.split(","));
	}

	public MatchScoringInTheZone() {
		HAS_AUTON = true;
		GAME_TYPE = GameType.IN_THE_ZONE;
		for(int i=0; i<field_scores.length; i++) {
			field_scores[i] = 0;
		}
	}
	
	int getRedFivePointZone() {
		return field_scores[R_FIVEPOINTZONE];
	}
	void setRedFivePointZone(int value) {
		field_scores[R_FIVEPOINTZONE] = value;
	}
	int getRedTenPointZone() {
		return field_scores[R_TENPOINTZONE];
	}
	void setRedTenPointZone(int value) {
		field_scores[R_TENPOINTZONE] = value;
	}
	int getRedTwentyPointZone() {
		return field_scores[R_TWENTYPOINTZONE];
	}
	void setRedTwentyPointZone(int value) {
		field_scores[R_TWENTYPOINTZONE] = value;
	}

    int getRedHighestStacks() {
        return field_scores[R_HIGHESTSTACKS];
    }
    void setRedHighestStacks(int value) {
        field_scores[R_HIGHESTSTACKS] = value;
    }
    int getRedStackedCones() {
        return field_scores[R_STACKEDCONES];
    }
    void setRedStackedCones(int value) {
        field_scores[R_STACKEDCONES] = value;
    }

	int getRedParked() {
		return field_scores[R_PARKED];
	}
	void setRedParked(int value) {
		field_scores[R_PARKED] = value;
	}

	public int getRedAuton() {
		return field_scores[R_AUTON];
	}
	public void setRedAuton(int value) {
		field_scores[R_AUTON] = (value == 1) ? 1 : 0;
	}


	int getBlueFivePointZone() {
		return field_scores[B_FIVEPOINTZONE];
	}
	void setBlueFivePointZone(int value) {
		field_scores[B_FIVEPOINTZONE] = value;
	}
	int getBlueTenPointZone() {
		return field_scores[B_TENPOINTZONE];
	}
	void setBlueTenPointZone(int value) {
		field_scores[B_TENPOINTZONE] = value;
	}
	int getBlueTwentyPointZone() {
		return field_scores[B_TWENTYPOINTZONE];
	}
	void setBlueTwentyPointZone(int value) {
		field_scores[B_TWENTYPOINTZONE] = value;
	}

    int getBlueHighestStacks() {
        return field_scores[B_HIGHESTSTACKS];
    }
    void setBlueHighestStacks(int value) {
        field_scores[B_HIGHESTSTACKS] = value;
    }
    int getBlueStackedCones() {
        return field_scores[B_STACKEDCONES];
    }
    void setBlueStackedCones(int value) {
        field_scores[B_STACKEDCONES] = value;
    }

	int getBlueParked() {
		return field_scores[B_PARKED];
	}
	void setBlueParked(int value) {
		field_scores[B_PARKED] = value;
	}
	
	public int getBlueAuton() {
		return field_scores[B_AUTON];
	}
	public void setBlueAuton(int value) {
		field_scores[B_AUTON] = (value == 1) ? 1 : 0;
	}
	
	protected int[] getFieldScores() {
		int[] tmp = { 0, 0 };
		tmp[0] += 2*field_scores[R_STACKEDCONES];
		tmp[0] += 5*field_scores[R_HIGHESTSTACKS];
		tmp[0] += 5*field_scores[R_FIVEPOINTZONE] + 10*field_scores[R_TENPOINTZONE] + 20*field_scores[R_TWENTYPOINTZONE];
        tmp[0] += 2*field_scores[R_PARKED];
		tmp[0] += 10*field_scores[R_AUTON];

		tmp[1] += 2*field_scores[B_STACKEDCONES];
		tmp[1] += 5*field_scores[B_HIGHESTSTACKS];
		tmp[1] += 5*field_scores[B_FIVEPOINTZONE] + 10*field_scores[B_TENPOINTZONE] + 20*field_scores[B_TWENTYPOINTZONE];
		tmp[1] += 2*field_scores[B_PARKED];
		tmp[1] += 10*field_scores[B_AUTON];
		
		return tmp;
	}
	
	int getConesTotal() {
		return field_scores[R_STACKEDCONES];
	}
	
	int getRedBasesTotal() {
		return field_scores[R_FIVEPOINTZONE] + field_scores[R_TENPOINTZONE] + field_scores[R_TWENTYPOINTZONE];
	}

	int getBlueBasesTotal() {
		return field_scores[B_FIVEPOINTZONE] + field_scores[B_TENPOINTZONE] + field_scores[B_TWENTYPOINTZONE];
	}

	int getHighestStacksTotal() {
		return field_scores[R_HIGHESTSTACKS] + field_scores[B_HIGHESTSTACKS];
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
