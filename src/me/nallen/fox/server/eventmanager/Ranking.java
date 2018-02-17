package me.nallen.fox.server.eventmanager;

public class Ranking {
	private int team = -1;
	private int[] wlt = { 0, 0, 0 };
	private int[] rankdata = { 0, 0, 0 };

	public Ranking(int n_team, int n_win, int n_loss, int n_tie, int n_wps, int n_sps, int n_aps) {
		team = n_team;
		wlt = new int[] { n_win, n_loss, n_tie };
		rankdata = new int[] { n_wps, n_sps, n_aps };
	}
	
	public int getTeam() {
		return team;
	}

	public int[] getWLT() {
		return wlt;
	}
	
	public int[] getRankData() {
		return rankdata;
	}
	
	public void update(int n_team, int n_win, int n_loss, int n_tie, int n_wps, int n_sps, int n_aps) {
		team = n_team;
		wlt = new int[] { n_win, n_loss, n_tie };
		rankdata = new int[] { n_wps, n_sps, n_aps };
	}
}