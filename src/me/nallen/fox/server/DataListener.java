package me.nallen.fox.server;

public interface DataListener {
	public void update(UpdateType type);
	
	public enum UpdateType {
	    TICK, SCORE, SETTING, CLEAR
	}
}