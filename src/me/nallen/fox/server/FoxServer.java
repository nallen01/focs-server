package me.nallen.fox.server;

public class FoxServer {
	public static FoxData foxData;
	FoxGui gui;
	
	public static void main(String[] args) {
		new FoxServer();
	}
	
	public FoxServer() {
		init();
	}
	
	public void init() {
		// Start the TCP Server
		
		// Start the GUI
		gui = new FoxGui();
	}
}
