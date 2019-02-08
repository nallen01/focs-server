package me.nallen.fox.server.eventmanager.scoring;

import java.util.Arrays;

import me.nallen.fox.server.eventmanager.GameType;
import me.nallen.fox.server.eventmanager.scoring.MatchScoring;

public class MatchScoringStarstruck extends MatchScoring {
	public static final int MAX_STARS = 24;
	public static final int MAX_CUBES = 4;
	private static final int NUM_FIELDS = 14;
	public static final int NUM_TOTAL_FIELDS = NUM_FIELDS + NUM_BASE_FIELDS;

	private final int[] field_scores = new int[NUM_FIELDS];
	private static final int R_FARCUBES = 2;
	private static final int B_FARCUBES = 3;
	private static final int R_FARSTARS = 4;
	private static final int B_FARSTARS = 5;

	private static final int R_NEARCUBES = 10;
	private static final int B_NEARCUBES = 11;
	private static final int R_NEARSTARS = 12;
	private static final int B_NEARSTARS = 13;

	private static final int R_HIGHROBOTS = 6;
	private static final int B_HIGHROBOTS = 7;
	private static final int R_LOWROBOTS = 8;
	private static final int B_LOWROBOTS = 9;

	private static final int R_AUTON = 0;
	private static final int B_AUTON = 1;

	public MatchScoringStarstruck(String[] parts) {
		HAS_AUTON = true;
		GAME_TYPE = GameType.STARSTRUCK;
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
	public MatchScoringStarstruck(String init) {
		this(init.split(","));
	}

	public MatchScoringStarstruck() {
		HAS_AUTON = true;
		GAME_TYPE = GameType.STARSTRUCK;
		for(int i=0; i<field_scores.length; i++) {
			field_scores[i] = 0;
		}
	}
	
	public int getRedFarCubes() {
		return field_scores[R_FARCUBES];
	}
	public void setRedFarCubes(int value) {
		field_scores[R_FARCUBES] = value;
	}
	public int getRedFarStars() {
		return field_scores[R_FARSTARS];
	}
	public void setRedFarStars(int value) {
		field_scores[R_FARSTARS] = value;
	}
	public int getRedNearCubes() {
		return field_scores[R_NEARCUBES];
	}
	public void setRedNearCubes(int value) {
		field_scores[R_NEARCUBES] = value;
	}
	public int getRedNearStars() {
		return field_scores[R_NEARSTARS];
	}
	public void setRedNearStars(int value) {
		field_scores[R_NEARSTARS] = value;
	}

    public int getRedHighRobots() {
        return field_scores[R_HIGHROBOTS];
    }
    public void setRedHighRobots(int value) {
        field_scores[R_HIGHROBOTS] = value;
    }
    public int getRedLowRobots() {
        return field_scores[R_LOWROBOTS];
    }
    public void setRedLowRobots(int value) {
        field_scores[R_LOWROBOTS] = value;
    }

	public int getRedAuton() {
		return field_scores[R_AUTON];
	}
	public void setRedAuton(int value) {
		field_scores[R_AUTON] = (value == 1) ? 1 : 0;
	}


	public int getBlueFarCubes() {
		return field_scores[B_FARCUBES];
	}
	public void setBlueFarCubes(int value) {
		field_scores[B_FARCUBES] = value;
	}
	public int getBlueFarStars() {
		return field_scores[B_FARSTARS];
	}
	public void setBlueFarStars(int value) {
		field_scores[B_FARSTARS] = value;
	}
	public int getBlueNearCubes() {
		return field_scores[B_NEARCUBES];
	}
	public void setBlueNearCubes(int value) {
		field_scores[B_NEARCUBES] = value;
	}
	public int getBlueNearStars() {
		return field_scores[B_NEARSTARS];
	}
	public void setBlueNearStars(int value) {
		field_scores[B_NEARSTARS] = value;
	}

    public int getBlueHighRobots() {
        return field_scores[B_HIGHROBOTS];
    }
    public void setBlueHighRobots(int value) {
        field_scores[B_HIGHROBOTS] = value;
    }
    public int getBlueLowRobots() {
        return field_scores[B_LOWROBOTS];
    }
    public void setBlueLowRobots(int value) {
        field_scores[B_LOWROBOTS] = value;
    }
	
	public int getBlueAuton() {
		return field_scores[B_AUTON];
	}
	public void setBlueAuton(int value) {
		field_scores[B_AUTON] = (value == 1) ? 1 : 0;
	}
	
	protected int[] getFieldScores() {
		int[] tmp = { 0, 0 };
		tmp[0] += field_scores[R_NEARSTARS] + 2*field_scores[R_NEARCUBES];
		tmp[0] += 2*field_scores[R_FARSTARS] + 4*field_scores[R_FARCUBES];
        tmp[0] += 4*field_scores[R_LOWROBOTS] + 12*field_scores[R_HIGHROBOTS];
		tmp[0] += 4*field_scores[R_AUTON];

		tmp[1] += field_scores[B_NEARSTARS] + 2*field_scores[B_NEARCUBES];
		tmp[1] += 2*field_scores[B_FARSTARS] + 4*field_scores[B_FARCUBES];
		tmp[1] += 4*field_scores[B_LOWROBOTS] + 12*field_scores[B_HIGHROBOTS];
		tmp[1] += 4*field_scores[B_AUTON];
		
		return tmp;
	}
	
	public int getStarsTotal() {
		return field_scores[R_FARSTARS] + field_scores[R_NEARSTARS] + field_scores[B_FARSTARS] + field_scores[B_NEARSTARS];
	}
	
	public int getCubesTotal() {
		return field_scores[R_FARCUBES] + field_scores[R_NEARCUBES] + field_scores[B_FARCUBES] + field_scores[B_NEARCUBES];
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
