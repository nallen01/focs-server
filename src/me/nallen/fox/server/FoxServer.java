package me.nallen.fox.server;

public class FoxServer {
	public static FoxData foxData;
	FoxGui gui;
	public static TcpServer tcpServer;
	EventManagerClient emClient;
	
	public static final String tmIP = "192.168.0.105";
	public static final String tmUser = "nathan";
	public static final String tmPassword = "abc123";
	
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
		
		try {
			emClient = new EventManagerClient();
			emClient.connect(tmIP, tmUser, tmPassword);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		// Start the GUI
		gui = new FoxGui();
	}
}
