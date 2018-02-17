package me.nallen.fox.server;

import me.nallen.fox.server.eventmanager.DataEvent;
import me.nallen.fox.server.eventmanager.TcpClient;

public class EventManagerClient implements me.nallen.fox.server.eventmanager.DataListener {
	private TcpClient tcpClient;
	
	private Boolean hasStartedMatch = false;
	
	public EventManagerClient() {
	}
	
	public void connect(String ip, String username, String password) throws Exception {
		tcpClient = new TcpClient();
		Boolean result = tcpClient.login(ip, username, tcpClient.encryptPassword(password));
		
		if(!result) {
			throw new Exception("Unable to log in: " + tcpClient.getError());
		}
		
		tcpClient.addDataListener(this);
	}

	@Override
	public void dataReceived(DataEvent e) {
		if(e.getDataType().equals("5")) {
			int[] mode = tcpClient.getMatchMode();
			if(mode[0] == 1 || mode[0] == 2) {
				if(!hasStartedMatch) {
					FoxServer.foxData.clear();
					FoxServer.tcpServer.clearAll();
					hasStartedMatch = true;
					
					System.out.println("Start Match");
				}

				FoxServer.foxData.setPaused(false);
				
				System.out.println("Resuming graph");
			}
			else {
				if(mode[1] == 1) {
					hasStartedMatch = false;
					
					System.out.println("End Match");
				}

				FoxServer.foxData.setPaused(true);
				
				System.out.println("Pausing graph");
			}
		}
		else if(e.getDataType().equals("7")) {
			//System.out.println("Log Data");
		}
		else if(e.getDataType().equals("13")) {
			if(tcpClient.getAutonWinner() == TcpClient.AUTON_BLUE) {
				FoxServer.foxData.setBlueAuton(true);
			}
			else if(tcpClient.getAutonWinner() == TcpClient.AUTON_RED) {
				FoxServer.foxData.setRedAuton(true);
			}
			else {
				FoxServer.foxData.setBlueAuton(false);
				FoxServer.foxData.setRedAuton(false);
			}
			
			System.out.println("Auton winner set");
		}
	}
}