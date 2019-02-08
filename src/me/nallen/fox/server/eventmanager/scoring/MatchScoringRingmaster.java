package me.nallen.fox.server.eventmanager.scoring;

import java.util.Arrays;

import me.nallen.fox.server.eventmanager.GameType;
import me.nallen.fox.server.eventmanager.scoring.MatchScoring;

public class MatchScoringRingmaster extends MatchScoring {
	static final int MAX_RINGS = 60;
	static final int MAX_EMPTIED_PEGS = 3;
	private static final int NUM_FIELDS = 5;
	public static final int NUM_TOTAL_FIELDS = NUM_FIELDS + NUM_BASE_FIELDS;

	private int[] field_scores = new int[NUM_FIELDS];
	private static final int BONUS_TRAY = 0;
	private static final int EMPTIED_PEGS = 1;
	private static final int FLOOR_GOALS = 2;
	private static final int NON_UNIFORM_POSTS = 3;
	private static final int UNIFORM_POSTS = 4;

	public MatchScoringRingmaster(String[] parts) {
		HAS_AUTON = false;
		GAME_TYPE = GameType.RINGMASTER;
		scores[IS_DETAILED] = 0;
		if(parts.length != NUM_TOTAL_FIELDS) {
			throw new IllegalArgumentException("Array must have " + NUM_TOTAL_FIELDS + " elements");
		}
		initBase(Arrays.copyOfRange(parts, NUM_FIELDS, parts.length));
		for(int i=0; i<NUM_FIELDS; i++) {
			field_scores[i] = Integer.parseInt(parts[i]);
		}
	}
	public MatchScoringRingmaster(String init) {
		this(init.split(","));
	}

	public MatchScoringRingmaster() {
		HAS_AUTON = false;
		GAME_TYPE = GameType.RINGMASTER;
		for(int i=0; i<field_scores.length; i++) {
			field_scores[i] = 0;
		}
	}
	
	int getBonusTray() {
		return field_scores[BONUS_TRAY];
	}
	void setBonusTray(int value) {
		field_scores[BONUS_TRAY] = value;
	}
	int getEmptiedPegs() {
		return field_scores[EMPTIED_PEGS];
	}
	void setEmptiedPegs(int value) {
		field_scores[EMPTIED_PEGS] = value;
	}

	int getFloorGoals() {
		return field_scores[FLOOR_GOALS];
	}
	void setFloorGoals(int value) {
		field_scores[FLOOR_GOALS] = value;
	}
    int getNonUniformPosts() {
        return field_scores[NON_UNIFORM_POSTS];
    }
    void setNonUniformPosts(int value) {
        field_scores[NON_UNIFORM_POSTS] = value;
    }
    int getUniformPosts() {
        return field_scores[UNIFORM_POSTS];
    }
    void setUniformPosts(int value) {
        field_scores[UNIFORM_POSTS] = value;
    }

	protected int[] getFieldScores() {
		int[] tmp = { 0, 0 };
		tmp[0] += field_scores[FLOOR_GOALS];
		tmp[0] += 5*field_scores[NON_UNIFORM_POSTS];
		tmp[0] += 10*field_scores[UNIFORM_POSTS];
        tmp[0] += 5*field_scores[EMPTIED_PEGS];
		tmp[0] += 20*field_scores[BONUS_TRAY];

		tmp[1] = tmp[0];
		
		return tmp;
	}
	
	int getRingsTotal() {
		return field_scores[FLOOR_GOALS] + field_scores[NON_UNIFORM_POSTS] + field_scores[UNIFORM_POSTS];
	}

	int getEmptiedPegsTotal() {
		return field_scores[EMPTIED_PEGS];
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
