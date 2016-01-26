package me.nallen.fox.server;

public class FoxServer {
	public static FoxData foxData;
	FoxGui gui;
	TcpServer tcpServer;
	
	public static void main(String[] args) {
		new FoxServer();
	}
	
	public FoxServer() {
		init();
	}
	
	public void init() {
		foxData = new FoxData();
		foxData.clear();
		
		// Start the TCP Server
		tcpServer = new TcpServer();
		tcpServer.run();
		
		// Start the GUI
		gui = new FoxGui();
	}
}
