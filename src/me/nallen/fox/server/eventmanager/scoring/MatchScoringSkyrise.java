package me.nallen.fox.server.eventmanager.scoring;

import java.util.Arrays;

import me.nallen.fox.server.eventmanager.GameType;
import me.nallen.fox.server.eventmanager.scoring.MatchScoring;

public class MatchScoringSkyrise extends MatchScoring {
	public static final int MAX_CUBES = 22;
	public static final int MAX_SKYRISE = 14;
	public static final int MAX_POSTS = 10;
	private static final int NUM_FIELDS = 12;
	public static final int NUM_TOTAL_FIELDS = NUM_FIELDS + NUM_BASE_FIELDS;
	
	private final int[] field_scores = new int[NUM_FIELDS];
	private static final int B_FLOORCUBES = 3;
	private static final int R_FLOORCUBES = 2;

	private static final int B_POSTCUBES = 5;
	private static final int R_POSTCUBES = 4;
	private static final int B_POSTSOWNED = 7;
	private static final int R_POSTSOWNED = 6;

	private static final int B_SKYRISECUBES = 9;
	private static final int R_SKYRISECUBES = 8;
	private static final int B_SKYRISESECTS = 11;
	private static final int R_SKYRISESECTS = 10;
	
	private static final int R_AUTON = 0;
	private static final int B_AUTON = 1;

	public MatchScoringSkyrise(String[] parts) {
		HAS_AUTON = true;
		GAME_TYPE = GameType.SKYRISE;
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
	public MatchScoringSkyrise(String init) {
		this(init.split(","));
	}
	
	public MatchScoringSkyrise() {
		HAS_AUTON = true;
		GAME_TYPE = GameType.SKYRISE;
		for(int i=0; i<field_scores.length; i++) {
			field_scores[i] = 0;
		}
	}
	
	public int getRedFloorCubes() {
		return field_scores[R_FLOORCUBES];
	}
	public void setRedFloorCubes(int value) {
		field_scores[R_FLOORCUBES] = value;
	}
	public int getRedPostCubes() {
		return field_scores[R_POSTCUBES];
	}
	public void setRedPostCubes(int value) {
		field_scores[R_POSTCUBES] = value;
	}
	public int getRedPostsOwned() {
		return field_scores[R_POSTSOWNED];
	}
	public void setRedPostsOwned(int value) {
		field_scores[R_POSTSOWNED] = value;
	}
	public int getRedSkyriseCubes() {
		return field_scores[R_SKYRISECUBES];
	}
	public void setRedSkyriseCubes(int value) {
		field_scores[R_SKYRISECUBES] = value;
	}
	public int getRedSkyriseSections() {
		return field_scores[R_SKYRISESECTS];
	}
	public void setRedSkyriseSections(int value) {
		field_scores[R_SKYRISESECTS] = value;
	}
	
	public int getRedAuton() {
		return field_scores[R_AUTON];
	}
	public void setRedAuton(int value) {
		field_scores[R_AUTON] = (value == 1) ? 1 : 0;
	}
	
	public int getBlueFloorCubes() {
		return field_scores[B_FLOORCUBES];
	}
	public void setBlueFloorCubes(int value) {
		field_scores[B_FLOORCUBES] = value;
	}
	public int getBluePostCubes() {
		return field_scores[B_POSTCUBES];
	}
	public void setBluePostCubes(int value) {
		field_scores[B_POSTCUBES] = value;
	}
	public int getBluePostsOwned() {
		return field_scores[B_POSTSOWNED];
	}
	public void setBluePostsOwned(int value) {
		field_scores[B_POSTSOWNED] = value;
	}
	public int getBlueSkyriseCubes() {
		return field_scores[B_SKYRISECUBES];
	}
	public void setBlueSkyriseCubes(int value) {
		field_scores[B_SKYRISECUBES] = value;
	}
	public int getBlueSkyriseSections() {
		return field_scores[B_SKYRISESECTS];
	}
	public void setBlueSkyriseSections(int value) {
		field_scores[B_SKYRISESECTS] = value;
	}
	
	public int getBlueAuton() {
		return field_scores[B_AUTON];
	}
	public void setBlueAuton(int value) {
		field_scores[B_AUTON] = (value == 1) ? 1 : 0;
	}
	
	protected int[] getFieldScores() {
		int[] tmp = { 0, 0 };
		tmp[0] += field_scores[R_FLOORCUBES];
		tmp[0] += 2*field_scores[R_POSTCUBES] + field_scores[R_POSTSOWNED];
		tmp[0] += 4*field_scores[R_SKYRISESECTS] + 4*field_scores[R_SKYRISECUBES];
		tmp[0] += 10*field_scores[R_AUTON];
		
		tmp[1] += field_scores[B_FLOORCUBES];
		tmp[1] += 2*field_scores[B_POSTCUBES] + field_scores[B_POSTSOWNED];
		tmp[1] += 4*field_scores[B_SKYRISESECTS] + 4*field_scores[B_SKYRISECUBES];
		tmp[1] += 10*field_scores[B_AUTON];
		
		return tmp;
	}
	
	public int getRedCubesTotal() {
		return field_scores[R_FLOORCUBES] + field_scores[R_POSTCUBES] + field_scores[R_SKYRISECUBES];
	}
	
	public int getBlueCubesTotal() {
		return field_scores[B_FLOORCUBES] + field_scores[B_POSTCUBES] + field_scores[B_SKYRISECUBES];
	}
	
	public int getSkyriseTotal() {
		return field_scores[R_SKYRISESECTS] + field_scores[B_SKYRISESECTS];
	}
	
	public int getPostsOwnedTotal() {
		return field_scores[R_POSTSOWNED] + field_scores[B_POSTSOWNED];
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
