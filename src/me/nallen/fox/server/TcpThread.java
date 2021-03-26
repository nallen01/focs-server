package me.nallen.fox.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import me.nallen.fox.server.FoxData.AutonWinner;
import me.nallen.fox.server.FoxData.BallType;
import me.nallen.fox.server.FoxData.HistoryMethod;

public class TcpThread extends Thread implements DataListener {
    private Socket socket = null;
    private BufferedReader in = null;
    private BufferedWriter out = null;
    
    public enum ScoreField {
		GOAL_OWNERSHIP_0_0(0),
		GOAL_OWNERSHIP_0_1(1),
		GOAL_OWNERSHIP_0_2(2),
		GOAL_OWNERSHIP_1_0(3),
		GOAL_OWNERSHIP_1_1(4),
		GOAL_OWNERSHIP_1_2(5),
		GOAL_OWNERSHIP_2_0(6),
		GOAL_OWNERSHIP_2_1(7),
		GOAL_OWNERSHIP_2_2(8),
		
		AUTON(9),
		
		RED_BALLS(10),
		
		BLUE_BALLS(11),
    	
    	PAUSED(22),
    	HISTORY_METHOD(23),
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
		    	

		    	sendFoxCommand(ScoreField.GOAL_OWNERSHIP_0_0, MessageType.SET, FoxServer.foxData.getGoalOwnership(0, 0).getValue());
		    	sendFoxCommand(ScoreField.GOAL_OWNERSHIP_0_1, MessageType.SET, FoxServer.foxData.getGoalOwnership(0, 1).getValue());
		    	sendFoxCommand(ScoreField.GOAL_OWNERSHIP_0_2, MessageType.SET, FoxServer.foxData.getGoalOwnership(0, 2).getValue());
		    	sendFoxCommand(ScoreField.GOAL_OWNERSHIP_1_0, MessageType.SET, FoxServer.foxData.getGoalOwnership(1, 0).getValue());
		    	sendFoxCommand(ScoreField.GOAL_OWNERSHIP_1_1, MessageType.SET, FoxServer.foxData.getGoalOwnership(1, 1).getValue());
		    	sendFoxCommand(ScoreField.GOAL_OWNERSHIP_1_2, MessageType.SET, FoxServer.foxData.getGoalOwnership(1, 2).getValue());
		    	sendFoxCommand(ScoreField.GOAL_OWNERSHIP_2_0, MessageType.SET, FoxServer.foxData.getGoalOwnership(2, 0).getValue());
		    	sendFoxCommand(ScoreField.GOAL_OWNERSHIP_2_1, MessageType.SET, FoxServer.foxData.getGoalOwnership(2, 1).getValue());
		    	sendFoxCommand(ScoreField.GOAL_OWNERSHIP_2_2, MessageType.SET, FoxServer.foxData.getGoalOwnership(2, 2).getValue());
		    	
		    	
		    	sendFoxCommand(ScoreField.AUTON, MessageType.SET, FoxServer.foxData.getAutonWinner().getValue());
		    	
		    	sendFoxCommand(ScoreField.RED_BALLS, MessageType.SET, FoxServer.foxData.getRedBalls());
		    	
		    	sendFoxCommand(ScoreField.BLUE_BALLS, MessageType.SET, FoxServer.foxData.getBlueBalls());
		    	
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
		    					
		    					if(field == ScoreField.GOAL_OWNERSHIP_0_0
		    							|| field == ScoreField.GOAL_OWNERSHIP_0_1
		    							|| field == ScoreField.GOAL_OWNERSHIP_0_2
		    							|| field == ScoreField.GOAL_OWNERSHIP_1_0
		    							|| field == ScoreField.GOAL_OWNERSHIP_1_1
		    							|| field == ScoreField.GOAL_OWNERSHIP_1_2
		    							|| field == ScoreField.GOAL_OWNERSHIP_2_0
		    							|| field == ScoreField.GOAL_OWNERSHIP_2_1
		    							|| field == ScoreField.GOAL_OWNERSHIP_2_2) {
		    						int x = 0, y = 0;
		    						switch(field) {
			    						case GOAL_OWNERSHIP_0_0: x = 0; y = 0; break;
			    						case GOAL_OWNERSHIP_0_1: x = 0; y = 1; break;
			    						case GOAL_OWNERSHIP_0_2: x = 0; y = 2; break;
			    						case GOAL_OWNERSHIP_1_0: x = 1; y = 0; break;
			    						case GOAL_OWNERSHIP_1_1: x = 1; y = 1; break;
			    						case GOAL_OWNERSHIP_1_2: x = 1; y = 2; break;
			    						case GOAL_OWNERSHIP_2_0: x = 2; y = 0; break;
			    						case GOAL_OWNERSHIP_2_1: x = 2; y = 1; break;
			    						case GOAL_OWNERSHIP_2_2: x = 2; y = 2; break;
			    						default: break;
		    						}
		    						
	    							FoxServer.foxData.setGoalOwnership(x, y, BallType.fromInt(num));
		    					}
		    					else if(field == ScoreField.AUTON) {
		    						FoxServer.foxData.setAutonWinner(AutonWinner.fromInt(num));
		    					}
		    					else if(field == ScoreField.RED_BALLS) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getRedBalls() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getRedBalls() - num;
		    						}
		    						
	    							FoxServer.foxData.setRedBalls(num);
		    					}
		    					else if(field == ScoreField.BLUE_BALLS) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getBlueBalls() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getBlueBalls() - num;
		    						}
		    						
	    							FoxServer.foxData.setBlueBalls(num);
		    					}
		    					else if(field == ScoreField.PAUSED) {
	    							FoxServer.foxData.setPaused(num > 0);
		    					}
		    					else if(field == ScoreField.HISTORY_METHOD) {
	    							FoxServer.foxData.setHistoryMethod(HistoryMethod.fromInt(num));
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