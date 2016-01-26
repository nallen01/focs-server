package me.nallen.fox.server;

import java.io.IOException;
import java.net.ServerSocket;

public class TcpServer {
	public static int DEFAULT_PORT = 5006;
	
	private ServerSocket serverSocket = null;
	
	private boolean listening = true;
	
	public TcpServer() {
	}
	
	public void run() {
		try {
			serverSocket = new ServerSocket(DEFAULT_PORT);
        } catch (IOException e) {
        	return;
        }
		
		Thread thread = new Thread(new Runnable() {
			public void run() {
		        while (listening)
					try {
							new TcpThread(serverSocket.accept()).start();
					} catch (IOException e) { }

		        try {
					serverSocket.close();
				} catch (IOException e) { }
			}
		});
		thread.setName("Fox TCP Server");
		thread.start();
	}
}
