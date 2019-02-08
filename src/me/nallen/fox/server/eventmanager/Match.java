package me.nallen.fox.server.eventmanager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class Match {
	private int[] blue = null;
	private int[] red = null;
	private int redscore = -1;
	private int bluescore = -1;
	private int num = -1;
	private int round = -1;
	private int type = -1;
	private int field;
	private final Date scheduled;
	private final int[] sitting = { 2, 2 };
	private boolean has_played = false;

	private long ignoreMillis;

	public Match(int n_type, int n_round, int n_num, int[] n_red, int[] n_blue, int n_field, Date n_scheduled) {
		type = n_type;
		round = n_round;
		num = n_num;
		field = n_field;
		scheduled = n_scheduled;
		ignoreMillis = System.currentTimeMillis();

		LinkedList<Integer> tmp = new LinkedList<Integer>();
		for(int i=0; i<n_red.length; i++) {
			if(n_red[i] >= 0) {
				tmp.add(i);
			}
		}
		red = new int[tmp.size()];
		for(int i=0; i<tmp.size(); i++) {
			red[i] = n_red[tmp.get(i)];
		}
		
		tmp.clear();
		for(int i=0; i<n_blue.length; i++) {
			if(n_blue[i] >= 0) {
				tmp.add(i);
			}
		}
		blue = new int[tmp.size()];
		for(int i=0; i<tmp.size(); i++) {
			blue[i] = n_blue[tmp.get(i)];
		}
	}

	public int[] getBlue() {
		return blue;
	}

	public String getName() {
		return toName(type, round, num);
	}
	
	public static String toName(int n_type, int n_round, int n_num) {
		if((n_type < 0) || (n_round < 0) || (n_num < 0))
			return "";
		String str = "";
		if (n_type == 1)
			return "P " + n_num;
		if (n_type == 2)
			return "Q " + n_num;
		if (n_type == 5)
			return "F " + n_num;
		if (n_type == 3)
			str = "QF ";
		if (n_type == 4)
			str = "SF ";
		if (n_type == 15)
			return "Finals " + n_num;
		return str + n_round + "-" + n_num;
	}

	public int getNum() {
		return num;
	}

	public int[] getRed() {
		return red;
	}

	public int getRound() {
		return round;
	}

	public int[] getScores() {
		return new int[] { redscore, bluescore };
	}

	public int getType() {
		return type;
	}
	
	public int getField() {
		return field;
	}
	public Date getScheduled() {
		return scheduled;
	}
	
	public String getScheduledString() {
		DateFormat df = new SimpleDateFormat("hh:mm:ss a");
		return df.format(scheduled);
	}
	
	public int[] getSitting() {
		return sitting;
	}

	public void setScore(int n_redscore, int n_bluescore) {
		redscore = n_redscore;
		bluescore = n_bluescore;
	}
	public void setField(int n_field) {
		field = n_field;
	}
	
	public void setSitting(int n_redsit, int n_bluesit) {
		sitting[0] = n_redsit;
		sitting[1] = n_bluesit;
	}
	
	public void setHasPlayed(boolean n_has_played) {
		has_played = n_has_played;
	}
	public boolean getHasPlayed() {
		return has_played;
	}

	public void setIgnoreMillis(long ignoreMillis) {
		this.ignoreMillis = ignoreMillis;
	}
	public long getIgnoreMillis() {
		return ignoreMillis;
	}
}