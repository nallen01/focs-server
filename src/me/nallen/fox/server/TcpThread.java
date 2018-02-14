package me.nallen.fox.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import me.nallen.fox.server.FoxData.ScoringZone;

public class TcpThread extends Thread implements DataListener {
    private Socket socket = null;
    private BufferedReader in = null;
    private BufferedWriter out = null;
    
    public enum ScoreField {
		RED_BASE_ONE_CONES(0),
		RED_BASE_TWO_CONES(1),
		RED_BASE_THREE_CONES(2),
		RED_BASE_FOUR_CONES(3),
		RED_BASE_ONE_ZONE(4),
		RED_BASE_TWO_ZONE(5),
		RED_BASE_THREE_ZONE(6),
		RED_BASE_FOUR_ZONE(7),
		RED_STATIONARY_CONES(8),
		RED_PARKING(9),
		RED_AUTON(10),

		BLUE_BASE_ONE_CONES(11),
		BLUE_BASE_TWO_CONES(12),
		BLUE_BASE_THREE_CONES(13),
		BLUE_BASE_FOUR_CONES(14),
		BLUE_BASE_ONE_ZONE(15),
		BLUE_BASE_TWO_ZONE(16),
		BLUE_BASE_THREE_ZONE(17),
		BLUE_BASE_FOUR_ZONE(18),
		BLUE_STATIONARY_CONES(19),
		BLUE_PARKING(20),
		BLUE_AUTON(21),
    	
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
		    	
		    	sendFoxCommand(ScoreField.RED_AUTON, MessageType.SET, FoxServer.foxData.getRedAuton() ? 1 : 0);
		    	sendFoxCommand(ScoreField.RED_BASE_ONE_CONES, MessageType.SET, FoxServer.foxData.getRedBaseCones(0));
		    	sendFoxCommand(ScoreField.RED_BASE_TWO_CONES, MessageType.SET, FoxServer.foxData.getRedBaseCones(1));
		    	sendFoxCommand(ScoreField.RED_BASE_THREE_CONES, MessageType.SET, FoxServer.foxData.getRedBaseCones(2));
		    	sendFoxCommand(ScoreField.RED_BASE_FOUR_CONES, MessageType.SET, FoxServer.foxData.getRedBaseCones(3));
		    	sendFoxCommand(ScoreField.RED_BASE_ONE_ZONE, MessageType.SET, FoxServer.foxData.getRedBaseZone(0).getValue());
		    	sendFoxCommand(ScoreField.RED_BASE_TWO_ZONE, MessageType.SET, FoxServer.foxData.getRedBaseZone(1).getValue());
		    	sendFoxCommand(ScoreField.RED_BASE_THREE_ZONE, MessageType.SET, FoxServer.foxData.getRedBaseZone(2).getValue());
		    	sendFoxCommand(ScoreField.RED_BASE_FOUR_ZONE, MessageType.SET, FoxServer.foxData.getRedBaseZone(3).getValue());
		    	sendFoxCommand(ScoreField.RED_STATIONARY_CONES, MessageType.SET, FoxServer.foxData.getRedStationaryCones());
		    	sendFoxCommand(ScoreField.RED_PARKING, MessageType.SET, FoxServer.foxData.getRedParking());
		    	
		    	sendFoxCommand(ScoreField.BLUE_AUTON, MessageType.SET, FoxServer.foxData.getBlueAuton() ? 1 : 0);
		    	sendFoxCommand(ScoreField.BLUE_BASE_ONE_CONES, MessageType.SET, FoxServer.foxData.getBlueBaseCones(0));
		    	sendFoxCommand(ScoreField.BLUE_BASE_TWO_CONES, MessageType.SET, FoxServer.foxData.getBlueBaseCones(1));
		    	sendFoxCommand(ScoreField.BLUE_BASE_THREE_CONES, MessageType.SET, FoxServer.foxData.getBlueBaseCones(2));
		    	sendFoxCommand(ScoreField.BLUE_BASE_FOUR_CONES, MessageType.SET, FoxServer.foxData.getBlueBaseCones(3));
		    	sendFoxCommand(ScoreField.BLUE_BASE_ONE_ZONE, MessageType.SET, FoxServer.foxData.getBlueBaseZone(0).getValue());
		    	sendFoxCommand(ScoreField.BLUE_BASE_TWO_ZONE, MessageType.SET, FoxServer.foxData.getBlueBaseZone(1).getValue());
		    	sendFoxCommand(ScoreField.BLUE_BASE_THREE_ZONE, MessageType.SET, FoxServer.foxData.getBlueBaseZone(2).getValue());
		    	sendFoxCommand(ScoreField.BLUE_BASE_FOUR_ZONE, MessageType.SET, FoxServer.foxData.getBlueBaseZone(3).getValue());
		    	sendFoxCommand(ScoreField.BLUE_STATIONARY_CONES, MessageType.SET, FoxServer.foxData.getBlueStationaryCones());
		    	sendFoxCommand(ScoreField.BLUE_PARKING, MessageType.SET, FoxServer.foxData.getBlueParking());

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
		    					
		    					if(field == ScoreField.RED_BASE_ONE_CONES
		    							|| field == ScoreField.RED_BASE_TWO_CONES
		    							|| field == ScoreField.RED_BASE_THREE_CONES
		    							|| field == ScoreField.RED_BASE_FOUR_CONES) {
		    						int index = 0;
		    						switch(field) {
			    						case RED_BASE_ONE_CONES: index = 0; break;
			    						case RED_BASE_TWO_CONES: index = 1; break;
			    						case RED_BASE_THREE_CONES: index = 2; break;
			    						case RED_BASE_FOUR_CONES: index = 3; break;
			    						default: break;
		    						}
		    						
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getRedBaseCones(index) + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getRedBaseCones(index) - num;
		    						}
		    						
	    							FoxServer.foxData.setRedBaseCones(index, num);
		    					}
		    					if(field == ScoreField.RED_BASE_ONE_ZONE
		    							|| field == ScoreField.RED_BASE_TWO_ZONE
		    							|| field == ScoreField.RED_BASE_THREE_ZONE
		    							|| field == ScoreField.RED_BASE_FOUR_ZONE) {
		    						int index = 0;
		    						switch(field) {
			    						case RED_BASE_ONE_ZONE: index = 0; break;
			    						case RED_BASE_TWO_ZONE: index = 1; break;
			    						case RED_BASE_THREE_ZONE: index = 2; break;
			    						case RED_BASE_FOUR_ZONE: index = 3; break;
			    						default: break;
		    						}
		    						
	    							FoxServer.foxData.setRedBaseZone(index, ScoringZone.fromInt(num));
		    					}
		    					else if(field == ScoreField.RED_STATIONARY_CONES) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getRedStationaryCones() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getRedStationaryCones() - num;
		    						}
		    						
	    							FoxServer.foxData.setRedStationaryCones(num);
		    					}
		    					else if(field == ScoreField.RED_PARKING) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getRedParking() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getRedParking() - num;
		    						}
		    						
	    							FoxServer.foxData.setRedParking(num);
		    					}
		    					else if(field == ScoreField.RED_AUTON) {
	    							FoxServer.foxData.setRedAuton(num > 0);
		    					}
		    					else if(field == ScoreField.BLUE_BASE_ONE_CONES
		    							|| field == ScoreField.BLUE_BASE_TWO_CONES
		    							|| field == ScoreField.BLUE_BASE_THREE_CONES
		    							|| field == ScoreField.BLUE_BASE_FOUR_CONES) {
		    						int index = 0;
		    						switch(field) {
			    						case BLUE_BASE_ONE_CONES: index = 0; break;
			    						case BLUE_BASE_TWO_CONES: index = 1; break;
			    						case BLUE_BASE_THREE_CONES: index = 2; break;
			    						case BLUE_BASE_FOUR_CONES: index = 3; break;
			    						default: break;
		    						}
		    						
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getBlueBaseCones(index) + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getBlueBaseCones(index) - num;
		    						}
		    						
	    							FoxServer.foxData.setBlueBaseCones(index, num);
		    					}
		    					if(field == ScoreField.BLUE_BASE_ONE_ZONE
		    							|| field == ScoreField.BLUE_BASE_TWO_ZONE
		    							|| field == ScoreField.BLUE_BASE_THREE_ZONE
		    							|| field == ScoreField.BLUE_BASE_FOUR_ZONE) {
		    						int index = 0;
		    						switch(field) {
			    						case BLUE_BASE_ONE_ZONE: index = 0; break;
			    						case BLUE_BASE_TWO_ZONE: index = 1; break;
			    						case BLUE_BASE_THREE_ZONE: index = 2; break;
			    						case BLUE_BASE_FOUR_ZONE: index = 3; break;
			    						default: break;
		    						}
		    						
	    							FoxServer.foxData.setBlueBaseZone(index, ScoringZone.fromInt(num));
		    					}
		    					else if(field == ScoreField.BLUE_STATIONARY_CONES) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getBlueStationaryCones() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getBlueStationaryCones() - num;
		    						}
		    						
	    							FoxServer.foxData.setBlueStationaryCones(num);
		    					}
		    					else if(field == ScoreField.BLUE_PARKING) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getBlueParking() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getBlueParking() - num;
		    						}
		    						
	    							FoxServer.foxData.setBlueParking(num);
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