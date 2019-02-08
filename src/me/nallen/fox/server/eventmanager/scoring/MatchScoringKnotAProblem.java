package me.nallen.fox.server.eventmanager.scoring;

import java.util.Arrays;

import me.nallen.fox.server.eventmanager.GameType;
import me.nallen.fox.server.eventmanager.scoring.MatchScoring;

public class MatchScoringKnotAProblem extends MatchScoring {
	public static final int MAX_KNOTS_PER_COLOR = 40;
	private static final int NUM_FIELDS = 6;
	public static final int NUM_TOTAL_FIELDS = NUM_FIELDS + NUM_BASE_FIELDS;

	private final int[] field_scores = new int[NUM_FIELDS];
	private static final int R_PIERS = 0;
	private static final int B_PIERS = 1;

	private static final int R_JETTIES = 2;
	private static final int B_JETTIES = 3;

	private static final int R_TILES = 4;
	private static final int B_TILES = 5;

	private MatchScoringKnotAProblem(String[] parts) {
		HAS_AUTON = false;
		GAME_TYPE = GameType.KNOT_A_PROBLEM;
		scores[IS_DETAILED] = 0;
		if(parts.length != NUM_TOTAL_FIELDS) {
			throw new IllegalArgumentException("Array must have " + NUM_TOTAL_FIELDS + " elements");
		}
		initBase(Arrays.copyOfRange(parts, NUM_FIELDS, parts.length));
		for(int i=0; i<NUM_FIELDS; i++) {
			field_scores[i] = Integer.parseInt(parts[i]);
		}
	}
	public MatchScoringKnotAProblem(String init) {
		this(init.split(","));
	}

	public MatchScoringKnotAProblem() {
		HAS_AUTON = false;
		GAME_TYPE = GameType.KNOT_A_PROBLEM;
		for(int i=0; i<field_scores.length; i++) {
			field_scores[i] = 0;
		}
	}
	
	public int getRedPiers() {
		return field_scores[R_PIERS];
	}
	public void setRedPiers(int value) {
		field_scores[R_PIERS] = value;
	}
	public int getRedJetties() {
		return field_scores[R_JETTIES];
	}
	public void setRedJetties(int value) {
		field_scores[R_JETTIES] = value;
	}
	public int getRedTiles() {
		return field_scores[R_TILES];
	}
	public void setRedTiles(int value) {
		field_scores[R_TILES] = value;
	}
	
	
	public int getBluePiers() {
		return field_scores[B_PIERS];
	}
	public void setBluePiers(int value) {
		field_scores[B_PIERS] = value;
	}
	public int getBlueJetties() {
		return field_scores[B_JETTIES];
	}
	public void setBlueJetties(int value) {
		field_scores[B_JETTIES] = value;
	}
	public int getBlueTiles() {
		return field_scores[B_TILES];
	}
	public void setBlueTiles(int value) {
		field_scores[B_TILES] = value;
	}
	
	protected int[] getFieldScores() {
		int[] tmp = { 0, 0 };
		tmp[0] += field_scores[R_TILES];
		tmp[0] += 2*field_scores[R_JETTIES];
		tmp[0] += 3*field_scores[R_PIERS];
		
		tmp[1] += field_scores[B_TILES];
		tmp[1] += 2*field_scores[B_JETTIES];
		tmp[1] += 3*field_scores[B_PIERS];
		
		return tmp;
	}
	
	public int getRedKnotsTotal() {
		return field_scores[R_PIERS] + field_scores[R_JETTIES]
				+ field_scores[R_TILES];
	}

	public int getBlueKnotsTotal() {
		return field_scores[B_PIERS] + field_scores[B_JETTIES]
				+ field_scores[B_TILES];
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
