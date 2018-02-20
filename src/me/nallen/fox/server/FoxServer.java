package me.nallen.fox.server;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class FoxServer {
	public static FoxData foxData;
	FoxGui gui;
	public static TcpServer tcpServer;
	EventManagerClient emClient;
	
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
		
		String ip = null;
		String user = null;
		String password = null;
		String uploadUrl = null;
		
		try {
			File file = new File("automation.cfg");
			if(file.exists()) {
				List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
				
				for(String line: lines) {
					if(line.startsWith("ip:"))
						ip = line.substring(3).trim();

					else if(line.startsWith("user:"))
						user = line.substring(5).trim();

					else if(line.startsWith("password:"))
						password = line.substring(9).trim();

					else if(line.startsWith("uploadUrl:"))
						uploadUrl = line.substring(10).trim();
				}
			}
		}
		catch(Exception e) {}
		
		if(ip != null && user != null && password != null) {
			try {
				emClient = new EventManagerClient();
				emClient.connect(ip, user, password, uploadUrl);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		// Start the GUI
		gui = new FoxGui();
	}
}
