package me.nallen.fox.server.eventmanager.scoring;

import java.util.Arrays;

import me.nallen.fox.server.eventmanager.GameType;
import me.nallen.fox.server.eventmanager.scoring.MatchScoring;

public class MatchScoringNothingButNet extends MatchScoring {
	public static final int MAX_BALLS = 94;
	public static final int MAX_BONUS_BALLS = 10;
	private static final int NUM_FIELDS = 14;
	public static final int NUM_TOTAL_FIELDS = NUM_FIELDS + NUM_BASE_FIELDS;

	private final int[] field_scores = new int[NUM_FIELDS];
	private static final int R_HIGHBALLS = 2;
	private static final int B_HIGHBALLS = 3;
	private static final int R_HIGHBONUSBALLS = 4;
	private static final int B_HIGHBONUSBALLS = 5;

	private static final int R_LOWBALLS = 8;
	private static final int B_LOWBALLS = 9;
	private static final int R_LOWBONUSBALLS = 10;
	private static final int B_LOWBONUSBALLS = 11;

	private static final int R_HIGHROBOTS = 6;
	private static final int B_HIGHROBOTS = 7;
	private static final int R_LOWROBOTS = 12;
	private static final int B_LOWROBOTS = 13;

	private static final int R_AUTON = 0;
	private static final int B_AUTON = 1;

	public MatchScoringNothingButNet(String[] parts) {
		HAS_AUTON = true;
		GAME_TYPE = GameType.NOTHING_BUT_NET;
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
	public MatchScoringNothingButNet(String init) {
		this(init.split(","));
	}

	public MatchScoringNothingButNet() {
		HAS_AUTON = true;
		GAME_TYPE = GameType.NOTHING_BUT_NET;
		for(int i=0; i<field_scores.length; i++) {
			field_scores[i] = 0;
		}
	}
	
	public int getRedHighBalls() {
		return field_scores[R_HIGHBALLS];
	}
	public void setRedHighBalls(int value) {
		field_scores[R_HIGHBALLS] = value;
	}
	public int getRedHighBonusBalls() {
		return field_scores[R_HIGHBONUSBALLS];
	}
	public void setRedHighBonusBalls(int value) {
		field_scores[R_HIGHBONUSBALLS] = value;
	}
	public int getRedLowBalls() {
		return field_scores[R_LOWBALLS];
	}
	public void setRedLowBalls(int value) {
		field_scores[R_LOWBALLS] = value;
	}
	public int getRedLowBonusBalls() {
		return field_scores[R_LOWBONUSBALLS];
	}
	public void setRedLowBonusBalls(int value) {
		field_scores[R_LOWBONUSBALLS] = value;
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


    public int getBlueHighBalls() {
        return field_scores[B_HIGHBALLS];
    }
    public void setBlueHighBalls(int value) {
        field_scores[B_HIGHBALLS] = value;
    }
    public int getBlueHighBonusBalls() {
        return field_scores[B_HIGHBONUSBALLS];
    }
    public void setBlueHighBonusBalls(int value) {
        field_scores[B_HIGHBONUSBALLS] = value;
    }
    public int getBlueLowBalls() {
        return field_scores[B_LOWBALLS];
    }
    public void setBlueLowBalls(int value) {
        field_scores[B_LOWBALLS] = value;
    }
    public int getBlueLowBonusBalls() {
        return field_scores[B_LOWBONUSBALLS];
    }
    public void setBlueLowBonusBalls(int value) {
        field_scores[B_LOWBONUSBALLS] = value;
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
		tmp[0] += field_scores[R_LOWBALLS] + 2*field_scores[R_LOWBONUSBALLS];
		tmp[0] += 5*field_scores[R_HIGHBALLS] + 10*field_scores[R_HIGHBONUSBALLS];
        tmp[0] += 25*field_scores[R_LOWROBOTS] + 50*field_scores[R_HIGHROBOTS];
		tmp[0] += 10*field_scores[R_AUTON];

        tmp[1] += field_scores[B_LOWBALLS] + 2*field_scores[B_LOWBONUSBALLS];
        tmp[1] += 5*field_scores[B_HIGHBALLS] + 10*field_scores[B_HIGHBONUSBALLS];
        tmp[1] += 25*field_scores[B_LOWROBOTS] + 50*field_scores[B_HIGHROBOTS];
		tmp[1] += 10*field_scores[B_AUTON];
		
		return tmp;
	}
	
	public int getBallsTotal() {
		return field_scores[R_HIGHBALLS] + field_scores[R_LOWBALLS] + field_scores[B_HIGHBALLS] + field_scores[B_LOWBALLS];
	}
	
	public int getBonusBallsTotal() {
		return field_scores[R_HIGHBONUSBALLS] + field_scores[B_HIGHBONUSBALLS] + field_scores[R_LOWBONUSBALLS] + field_scores[B_LOWBONUSBALLS];
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
