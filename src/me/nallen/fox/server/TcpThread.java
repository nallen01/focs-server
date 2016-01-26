package me.nallen.fox.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import me.nallen.fox.server.FoxData.ElevatedState;

public class TcpThread extends Thread {
    private Socket socket = null;
    private BufferedReader in = null;
    private BufferedWriter out = null;
    
    public enum ScoreField {
		RED_HIGH_BALLS(0),
		RED_LOW_BALLS(1),
		RED_HIGH_BONUS_BALLS(2),
		RED_LOW_BONUS_BALLS(3),
		RED_ELEVATION(4),
		RED_AUTON(10),
		
		BLUE_HIGH_BALLS(5),
		BLUE_LOW_BALLS(6),
		BLUE_HIGH_BONUS_BALLS(7),
		BLUE_LOW_BONUS_BALLS(8),
		BLUE_ELEVATION(9),
    	BLUE_AUTON(11),
    	
    	PAUSED(12),
    	HISTORY(13),
    	LARGE_HISTORY(14);
		
		private final int id;
		ScoreField(int id) { this.id = id; }
		public int getValue() { return id; }
		public static ScoreField fromInt(int id) {
			ScoreField[] values = ScoreField.values();
            for(int i=0; i<values.length; i++) {
                if(values[i].getValue() == id)
                    return values[i];
            }
            return null;
		}
	}
    
    public enum MessageType {
		ADD(0),
		SUBTRACT(1),
		SET(2);
		
		private final int id;
		MessageType(int id) { this.id = id; }
		public int getValue() { return id; }
		public static MessageType fromInt(int id) {
			MessageType[] values = MessageType.values();
            for(int i=0; i<values.length; i++) {
                if(values[i].getValue() == id)
                    return values[i];
            }
            return null;
		}
	}
    
	public TcpThread(Socket socket) {
		super("Fox TCP Thread");
		this.socket = socket;
	}
	
	private boolean sendMessage(String paramString) {
		if (out != null) {
			try {
				out.write(paramString + '\n');
				out.flush();
				return true;
			} catch (Exception e) {}
		}
		return false;
	}
	
	public void run() {
		try {
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    
		    // Check that they can use it
		    
		    if(out != null) {
		    	sendMessage("1");
		    	
		    	// Loop for messages from the client
		    	while(true) {
		    		String[] parts;
		    		try {
		    			String str = in.readLine();
		    			
		    			if(str != null) {
		    				parts = str.split("" + ((char)29), -1);
		    				if(parts.length == 3) {
		    					ScoreField field = ScoreField.fromInt(Integer.parseInt(parts[0]));
		    					MessageType type = MessageType.fromInt(Integer.parseInt(parts[1]));
		    					int num = Integer.parseInt(parts[2]);
		    					
		    					if(field == ScoreField.RED_HIGH_BALLS) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getRedHighBalls() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getRedHighBalls() - num;
		    						}
		    						
	    							FoxServer.foxData.setRedHighBalls(num);
		    					}
		    					else if(field == ScoreField.RED_LOW_BALLS) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getRedLowBalls() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getRedLowBalls() - num;
		    						}
		    						
	    							FoxServer.foxData.setRedLowBalls(num);
		    					}
		    					else if(field == ScoreField.RED_ELEVATION) {
		    						ElevatedState state = ElevatedState.fromInt(num);
	    							FoxServer.foxData.setRedElevation(state);
		    					}
		    					else if(field == ScoreField.RED_AUTON) {
	    							FoxServer.foxData.setRedAuton(num > 0);
		    					}
		    					else if(field == ScoreField.BLUE_HIGH_BALLS) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getBlueHighBalls() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getBlueHighBalls() - num;
		    						}
		    						
	    							FoxServer.foxData.setBlueHighBalls(num);
		    					}
		    					else if(field == ScoreField.BLUE_LOW_BALLS) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getBlueLowBalls() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getBlueLowBalls() - num;
		    						}
		    						
	    							FoxServer.foxData.setBlueLowBalls(num);
		    					}
		    					else if(field == ScoreField.BLUE_ELEVATION) {
		    						ElevatedState state = ElevatedState.fromInt(num);
	    							FoxServer.foxData.setBlueElevation(state);
		    					}
		    					else if(field == ScoreField.BLUE_AUTON) {
	    							FoxServer.foxData.setBlueAuton(num > 0);
		    					}
		    					else if(field == ScoreField.PAUSED) {
	    							FoxServer.foxData.setPaused(num > 0);
		    					}
		    					else if(field == ScoreField.HISTORY) {
	    							FoxServer.foxData.setShowHistory(num > 0);
		    					}
		    					else if(field == ScoreField.LARGE_HISTORY) {
	    							FoxServer.foxData.setLargeHistory(num > 0);
		    					}
		    				}
		    			}
		    			else {
		    				break;
		    			}
		    		
						Thread.sleep(10);
		    		} catch (IOException | InterruptedException e) {
						break;
					}
		    	}
		    }
		    else {
		    	sendMessage("0");
		    }
		    
		    out.close();
		    in.close();
		    socket.close();
		
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
}