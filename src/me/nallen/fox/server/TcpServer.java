package me.nallen.fox.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class TcpServer {
	public static int DEFAULT_PORT = 5005;
	
	private ServerSocket serverSocket = null;
	
	private boolean listening = true;
	
	private List<TcpThread> threads = new ArrayList<TcpThread>();
	
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
				TcpThread t = null;
		        while (listening)
					try {
							t = new TcpThread(serverSocket.accept());
							threads.add(t);
							t.start();
					} catch (IOException e) { }

		        if(t != null) {
		        	threads.remove(t);
		        }
		        
		        try {
					serverSocket.close();
				} catch (IOException e) { }
			}
		});
		thread.setName("Fox TCP Server");
		thread.start();
	}
	
	public void clearAll() {
		for(TcpThread t : threads) {
			t.clear();
		}
	}
}
