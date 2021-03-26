package me.nallen.fox.server;

import java.awt.Color;
import java.io.File;
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
		Color chromaColor = null;
		
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

					else if(line.startsWith("chroma:")) {
						String[] parts = line.substring(7).trim().split(",");
						if(parts.length == 3) {
							try {
								int red, green, blue = 0;
								red = Integer.parseInt(parts[0].trim());
								green = Integer.parseInt(parts[1].trim());
								blue = Integer.parseInt(parts[2].trim());
								
								chromaColor = new Color(red, green, blue);
							}
							catch(NumberFormatException e) {
								e.printStackTrace();
							}
							catch(IllegalArgumentException e) {
								e.printStackTrace();
							}
						}
					}
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
		gui = new FoxGui(chromaColor);
	}
}
