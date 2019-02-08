package me.nallen.fox.server.eventmanager.scoring;

import java.util.Arrays;

import me.nallen.fox.server.eventmanager.GameType;
import me.nallen.fox.server.eventmanager.scoring.MatchScoring;

public class MatchScoringBasic extends MatchScoring {
	private static final int NUM_FIELDS = 2;
	public static final int NUM_TOTAL_FIELDS = NUM_FIELDS + NUM_BASE_FIELDS;
	
	private final int[] field_scores = new int[NUM_FIELDS];
	private static final int R_SCORE = 0;
	private static final int B_SCORE = 1;

	public MatchScoringBasic(String[] parts) {
		HAS_AUTON = false;
		GAME_TYPE = GameType.BASIC;
		scores[IS_DETAILED] = 0;
		if(parts.length != NUM_TOTAL_FIELDS) {
			throw new IllegalArgumentException("Array must have " + NUM_TOTAL_FIELDS + " elements");
		}
		initBase(Arrays.copyOfRange(parts, NUM_FIELDS, parts.length));
		for(int i=0; i<NUM_FIELDS; i++) {
			field_scores[i] = Integer.parseInt(parts[i]);
		}
	}
	public MatchScoringBasic(String init) {
		this(init.split(","));
	}
	
	public MatchScoringBasic() {
		HAS_AUTON = false;
		GAME_TYPE = GameType.BASIC;
		for(int i=0; i<NUM_FIELDS; i++) {
			field_scores[i] = 0;
		}
	}
	
	public void setRedScore(int n_r_score) {
		field_scores[R_SCORE] = n_r_score;
	}
	public int getRedScore() {
		return field_scores[R_SCORE];
	}
	
	public void setBlueScore(int n_b_score) {
		field_scores[B_SCORE] = n_b_score;
	}
	public int getBlueScore() {
		return field_scores[B_SCORE];
	}
	
	protected int[] getFieldScores() {
		int[] tmp = { 0, 0 };
		tmp[0] += field_scores[R_SCORE];

		tmp[1] += field_scores[B_SCORE];
		
		return tmp;
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
