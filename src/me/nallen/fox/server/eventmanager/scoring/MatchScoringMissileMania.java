package me.nallen.fox.server.eventmanager.scoring;

import java.util.Arrays;

import me.nallen.fox.server.eventmanager.GameType;
import me.nallen.fox.server.eventmanager.scoring.MatchScoring;

public class MatchScoringMissileMania extends MatchScoring {
	public static final int MAX_MISSILES = 48;
	private static final int NUM_FIELDS = 6;
	public static final int NUM_TOTAL_FIELDS = NUM_FIELDS + NUM_BASE_FIELDS;
	
	private final int[] field_scores = new int[NUM_FIELDS];
	private static final int R_BUNKERS = 0;
	private static final int B_BUNKERS = 1;

	private static final int R_TOWERS = 2;
	private static final int B_TOWERS = 3;

	private static final int R_TRAILERS = 4;
	private static final int B_TRAILERS = 5;

	private MatchScoringMissileMania(String[] parts) {
		HAS_AUTON = false;
		GAME_TYPE = GameType.MISSILE_MANIA;
		scores[IS_DETAILED] = 0;
		if(parts.length != NUM_TOTAL_FIELDS) {
			throw new IllegalArgumentException("Array must have " + NUM_TOTAL_FIELDS + " elements");
		}
		initBase(Arrays.copyOfRange(parts, NUM_FIELDS, parts.length));
		for(int i=0; i<NUM_FIELDS; i++) {
			field_scores[i] = Integer.parseInt(parts[i]);
		}
	}
	public MatchScoringMissileMania(String init) {
		this(init.split(","));
	}
	
	public MatchScoringMissileMania() {
		HAS_AUTON = false;
		GAME_TYPE = GameType.MISSILE_MANIA;
		for(int i=0; i<field_scores.length; i++) {
			field_scores[i] = 0;
		}
	}
	
	public int getRedBunkers() {
		return field_scores[R_BUNKERS];
	}
	public void setRedBunkers(int value) {
		field_scores[R_BUNKERS] = value;
	}
	public int getRedTowers() {
		return field_scores[R_TOWERS];
	}
	public void setRedTowers(int value) {
		field_scores[R_TOWERS] = value;
	}
	public int getRedTrailers() {
		return field_scores[R_TRAILERS];
	}
	public void setRedTrailers(int value) {
		field_scores[R_TRAILERS] = value;
	}
	
	
	public int getBlueBunkers() {
		return field_scores[B_BUNKERS];
	}
	public void setBlueBunkers(int value) {
		field_scores[B_BUNKERS] = value;
	}
	public int getBlueTowers() {
		return field_scores[B_TOWERS];
	}
	public void setBlueTowers(int value) {
		field_scores[B_TOWERS] = value;
	}
	public int getBlueTrailers() {
		return field_scores[B_TRAILERS];
	}
	public void setBlueTrailers(int value) {
		field_scores[B_TRAILERS] = value;
	}
	
	protected int[] getFieldScores() {
		int[] tmp = { 0, 0 };
		tmp[0] += 2*field_scores[R_BUNKERS];
		tmp[0] += 3*field_scores[R_TOWERS];
		tmp[0] += 5*field_scores[R_TRAILERS];
		
		tmp[1] += 2*field_scores[B_BUNKERS];
		tmp[1] += 3*field_scores[B_TOWERS];
		tmp[1] += 5*field_scores[B_TRAILERS];
		
		return tmp;
	}
	
	public int getMissilesTotal() {
		return field_scores[R_BUNKERS] + field_scores[B_BUNKERS]
				+ field_scores[R_TOWERS] + field_scores[B_TOWERS]
				+ field_scores[R_TRAILERS] + field_scores[B_TRAILERS];
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
