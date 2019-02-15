package me.nallen.fox.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import me.nallen.fox.server.FoxData.ParkingState;
import me.nallen.fox.server.FoxData.ToggleState;

public class TcpThread extends Thread implements DataListener {
    private Socket socket = null;
    private BufferedReader in = null;
    private BufferedWriter out = null;
    
    public enum ScoreField {
		HIGH_FLAG_1_1(0),
		HIGH_FLAG_1_2(1),
		HIGH_FLAG_1_3(2),
		HIGH_FLAG_2_1(3),
		HIGH_FLAG_2_2(4),
		HIGH_FLAG_2_3(5),
		LOW_FLAG_1(6),
		LOW_FLAG_2(7),
		LOW_FLAG_3(8),
		
		RED_HIGH_CAPS(9),
		RED_LOW_CAPS(10),
		RED_PARKING_1(11),
		RED_PARKING_2(12),
		RED_AUTON(13),
		
		BLUE_HIGH_CAPS(14),
		BLUE_LOW_CAPS(15),
		BLUE_PARKING_1(16),
		BLUE_PARKING_2(17),
		BLUE_AUTON(18),
    	
    	PAUSED(22),
    	HISTORY(23),
    	LARGE_HISTORY(24),
    	HIDE(25),
    	
    	THREE_TEAM(26),
    	
    	CLEAR(27);
		
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

    private boolean sendFoxCommand(ScoreField field, MessageType type, int value) {
        return sendMessage("" + field.getValue() + ((char)29) + type.getValue() + ((char)29) + value);
    }
    
    public void clear() {
    	sendFoxCommand(ScoreField.CLEAR, MessageType.SET, 1);
    }
	
	public void run() {
		try {
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    
		    // Check that they can use it
		    
		    if(out != null) {
		    	sendMessage("1");
		    	

		    	sendFoxCommand(ScoreField.HIGH_FLAG_1_1, MessageType.SET, FoxServer.foxData.getHighFlag(0, 0).getValue());
		    	sendFoxCommand(ScoreField.HIGH_FLAG_1_2, MessageType.SET, FoxServer.foxData.getHighFlag(0, 1).getValue());
		    	sendFoxCommand(ScoreField.HIGH_FLAG_1_3, MessageType.SET, FoxServer.foxData.getHighFlag(0, 2).getValue());
		    	sendFoxCommand(ScoreField.HIGH_FLAG_2_1, MessageType.SET, FoxServer.foxData.getHighFlag(1, 0).getValue());
		    	sendFoxCommand(ScoreField.HIGH_FLAG_2_2, MessageType.SET, FoxServer.foxData.getHighFlag(1, 1).getValue());
		    	sendFoxCommand(ScoreField.HIGH_FLAG_2_3, MessageType.SET, FoxServer.foxData.getHighFlag(1, 2).getValue());
		    	
		    	sendFoxCommand(ScoreField.LOW_FLAG_1, MessageType.SET, FoxServer.foxData.getLowFlag(0).getValue());
		    	sendFoxCommand(ScoreField.LOW_FLAG_2, MessageType.SET, FoxServer.foxData.getLowFlag(1).getValue());
		    	sendFoxCommand(ScoreField.LOW_FLAG_3, MessageType.SET, FoxServer.foxData.getLowFlag(2).getValue());
		    	
		    	sendFoxCommand(ScoreField.RED_AUTON, MessageType.SET, FoxServer.foxData.getRedAuton() ? 1 : 0);
		    	sendFoxCommand(ScoreField.RED_HIGH_CAPS, MessageType.SET, FoxServer.foxData.getRedHighCaps());
		    	sendFoxCommand(ScoreField.RED_LOW_CAPS, MessageType.SET, FoxServer.foxData.getRedLowCaps());
		    	sendFoxCommand(ScoreField.RED_PARKING_1, MessageType.SET, FoxServer.foxData.getRedParking(0).getValue());
		    	sendFoxCommand(ScoreField.RED_PARKING_2, MessageType.SET, FoxServer.foxData.getRedParking(1).getValue());
		    	
		    	sendFoxCommand(ScoreField.BLUE_AUTON, MessageType.SET, FoxServer.foxData.getBlueAuton() ? 1 : 0);
		    	sendFoxCommand(ScoreField.BLUE_HIGH_CAPS, MessageType.SET, FoxServer.foxData.getBlueHighCaps());
		    	sendFoxCommand(ScoreField.BLUE_LOW_CAPS, MessageType.SET, FoxServer.foxData.getBlueLowCaps());
		    	sendFoxCommand(ScoreField.BLUE_PARKING_1, MessageType.SET, FoxServer.foxData.getBlueParking(0).getValue());
		    	sendFoxCommand(ScoreField.BLUE_PARKING_2, MessageType.SET, FoxServer.foxData.getBlueParking(1).getValue());
		    	
			    FoxServer.foxData.addListener(this);
		    	
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
		    					
		    					if(field == ScoreField.HIGH_FLAG_1_1
		    							|| field == ScoreField.HIGH_FLAG_1_2
		    							|| field == ScoreField.HIGH_FLAG_1_3
		    							|| field == ScoreField.HIGH_FLAG_2_1
		    							|| field == ScoreField.HIGH_FLAG_2_2
		    							|| field == ScoreField.HIGH_FLAG_2_3) {
		    						int row = 0;
		    						int col = 0;
		    						switch(field) {
			    						case HIGH_FLAG_1_1: row = 0; col = 0; break;
			    						case HIGH_FLAG_1_2: row = 0; col = 1; break;
			    						case HIGH_FLAG_1_3: row = 0; col = 2; break;
			    						case HIGH_FLAG_2_1: row = 1; col = 0; break;
			    						case HIGH_FLAG_2_2: row = 1; col = 1; break;
			    						case HIGH_FLAG_2_3: row = 1; col = 2; break;
			    						default: break;
		    						}
		    						
	    							FoxServer.foxData.setHighFlag(row, col, ToggleState.fromInt(num));
		    					}
		    					else if(field == ScoreField.LOW_FLAG_1
		    							|| field == ScoreField.LOW_FLAG_2
		    							|| field == ScoreField.LOW_FLAG_3) {
		    						int col = 0;
		    						switch(field) {
			    						case LOW_FLAG_1: col = 0; break;
			    						case LOW_FLAG_2: col = 1; break;
			    						case LOW_FLAG_3: col = 2; break;
			    						default: break;
		    						}
		    						
	    							FoxServer.foxData.setLowFlag(col, ToggleState.fromInt(num));
		    					}
		    					else if(field == ScoreField.RED_HIGH_CAPS) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getRedHighCaps() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getRedHighCaps() - num;
		    						}
		    						
	    							FoxServer.foxData.setRedHighCaps(num);
		    					}
		    					else if(field == ScoreField.RED_LOW_CAPS) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getRedLowCaps() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getRedLowCaps() - num;
		    						}
		    						
	    							FoxServer.foxData.setRedLowCaps(num);
		    					}
		    					else if(field == ScoreField.RED_PARKING_1) {
	    							FoxServer.foxData.setRedParking(0, ParkingState.fromInt(num));
		    					}
		    					else if(field == ScoreField.RED_PARKING_2) {
	    							FoxServer.foxData.setRedParking(1, ParkingState.fromInt(num));
		    					}
		    					else if(field == ScoreField.RED_AUTON) {
	    							FoxServer.foxData.setRedAuton(num > 0);
		    					}
		    					else if(field == ScoreField.BLUE_HIGH_CAPS) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getBlueHighCaps() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getBlueHighCaps() - num;
		    						}
		    						
	    							FoxServer.foxData.setBlueHighCaps(num);
		    					}
		    					else if(field == ScoreField.BLUE_LOW_CAPS) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getBlueLowCaps() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getBlueLowCaps() - num;
		    						}
		    						
	    							FoxServer.foxData.setBlueLowCaps(num);
		    					}
		    					else if(field == ScoreField.BLUE_PARKING_1) {
	    							FoxServer.foxData.setBlueParking(0, ParkingState.fromInt(num));
		    					}
		    					else if(field == ScoreField.BLUE_PARKING_2) {
	    							FoxServer.foxData.setBlueParking(1, ParkingState.fromInt(num));
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
		    					else if(field == ScoreField.CLEAR) {
		    						FoxServer.foxData.clear();
		    						FoxServer.tcpServer.clearAll();
		    					}
		    					else if(field == ScoreField.HIDE) {
		    						FoxServer.foxData.setHidden(num > 0);
		    					}
		    					else if(field == ScoreField.THREE_TEAM) {
		    						FoxServer.foxData.setThreeTeam(num > 0);
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

	@Override
	public void update(UpdateType type) {
		if(type == UpdateType.CLEAR) {
			if (out != null) {
	            try {
	                out.write(ScoreField.CLEAR.getValue() + ((char)29) + MessageType.SET.getValue() + ((char)29) + "1\n");
	                out.flush();
	            } catch (Exception e) {}
	        }
		}
	}
}