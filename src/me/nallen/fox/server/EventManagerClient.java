package me.nallen.fox.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.nallen.fox.server.eventmanager.DataEvent;
import me.nallen.fox.server.eventmanager.Match;
import me.nallen.fox.server.eventmanager.TcpClient;

public class EventManagerClient implements me.nallen.fox.server.eventmanager.DataListener {
	private static final int POST_MATCH_SCORE_DELAY_MILLISECONDS = 10000;
	
	private TcpClient tcpClient;
	
	private Boolean hasStartedMatch = false;
	private String prevMatchName = null;
	private StringBuilder scoreLogBuilder = new StringBuilder();
	private int lastTimerValue = -1;
	private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
	
	public EventManagerClient() {
	}
	
	public void connect(String ip, String username, String password) throws Exception {
		tcpClient = new TcpClient();
		Boolean result = tcpClient.login(ip, username, tcpClient.encryptPassword(password));
		
		if(!result) {
			throw new Exception("Unable to log in: " + tcpClient.getError());
		}
		
		File outputFile = new File("scores.csv");
		if(!outputFile.exists()) {
			try {
				outputFile.createNewFile();
				Files.write(Paths.get("scores.csv"), ("Match,Time Remaining," + FoxServer.foxData.getScoreFieldTitlesAsCsvLine() + "\n").getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) { }
		}
		
		while(!tcpClient.getReceiveFinished()) {
			Thread.sleep(100);
		}
		
		updateFoxState();
		updateThreeTeams();
		updateAutonWinner();
		
		tcpClient.addDataListener(this);
	}
	
	private void writeLogToFile() {
		System.out.println("Running");
		if(lastTimerValue == 0) {
			System.out.println("Writing");
			scoreLogBuilder.append(prevMatchName + ",0," + FoxServer.foxData.getCurrentScoreFieldsAsCsvLine() + "\n");
			
			try {
				Files.write(Paths.get("scores.csv"), scoreLogBuilder.toString().getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e1) { }

			scoreLogBuilder.setLength(0);
			lastTimerValue = -1;
		}
	}
	
	private void updateFoxState() {
		int[] mode = tcpClient.getMatchMode();
		if(mode[0] == 1 || mode[0] == 2) {
			if(!hasStartedMatch) {
				writeLogToFile();
				
				FoxServer.foxData.clear();
				FoxServer.tcpServer.clearAll();
				hasStartedMatch = true;
				
				int[] queued_match = tcpClient.getQueuedMatch();
				Match match = tcpClient.getMatch(queued_match[0], queued_match[1], queued_match[2]);
				
				prevMatchName = match.getName();
			}

			FoxServer.foxData.setPaused(false);
		}
		else {
			if(mode[1] == 1) {
				hasStartedMatch = false;
			}

			FoxServer.foxData.setPaused(true);
		}
	}
	
	private void updateThreeTeams() {
		int[] queued_match = tcpClient.getQueuedMatch();
		Match cur = tcpClient.getMatch(queued_match[0], queued_match[1], queued_match[2]);
		
		if(cur != null)
			FoxServer.foxData.setThreeTeam(cur.getRed().length > 2);
	}
	
	private void updateAutonWinner() {
		if(lastTimerValue > 0) {
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
		}
	}

	@Override
	public void dataReceived(DataEvent e) {
		if(e.getDataType().equals("5")) {
			updateFoxState();
		}
		else if(e.getDataType().equals("6")) {
			updateThreeTeams();
		}
		else if(e.getDataType().equals("7")) {
			if(hasStartedMatch) {
				int[] queued_match = tcpClient.getQueuedMatch();
				Match match = tcpClient.getMatch(queued_match[0], queued_match[1], queued_match[2]);
				
				prevMatchName = match.getName();
				
				int totalSeconds = (int) Math.ceil(tcpClient.getTimeRemaining() / 1000.0);
				
				if(totalSeconds > 0) {
					if(tcpClient.getMatchMode()[0] == 2)
						totalSeconds += 105;
					
					if(lastTimerValue < 0 || totalSeconds < lastTimerValue) {
						scoreLogBuilder.append(prevMatchName + "," + totalSeconds + "," + FoxServer.foxData.getCurrentScoreFieldsAsCsvLine() + "\n");
						
						lastTimerValue = totalSeconds;
					}
				}
				else {
					if(lastTimerValue == 1) {
						executor.schedule(new Runnable() {
							@Override
							public void run() {
								writeLogToFile();
							}
						}, POST_MATCH_SCORE_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS);
						
						lastTimerValue = 0;
					}
				}
			}
		}
		else if(e.getDataType().equals("13")) {
			updateAutonWinner();
		}
	}
}
