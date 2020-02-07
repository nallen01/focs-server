package me.nallen.fox.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import me.nallen.fox.server.FoxData.AutonWinner;
import me.nallen.fox.server.FoxData.CubeType;

public class TcpThread extends Thread implements DataListener {
    private Socket socket = null;
    private BufferedReader in = null;
    private BufferedWriter out = null;
    
    public enum ScoreField {
		TOWER_CUBE_1(0),
		TOWER_CUBE_2(1),
		TOWER_CUBE_3(2),
		TOWER_CUBE_4(3),
		TOWER_CUBE_5(4),
		TOWER_CUBE_6(5),
		TOWER_CUBE_7(6),
		
		AUTON(7),
		
		RED_ORANGE_CUBES(8),
		RED_GREEN_CUBES(9),
		RED_PURPLE_CUBES(10),
		
		BLUE_ORANGE_CUBES(11),
		BLUE_GREEN_CUBES(12),
		BLUE_PURPLE_CUBES(13),
    	
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
		    	

		    	sendFoxCommand(ScoreField.TOWER_CUBE_1, MessageType.SET, FoxServer.foxData.getTowerCube(0).getValue());
		    	sendFoxCommand(ScoreField.TOWER_CUBE_2, MessageType.SET, FoxServer.foxData.getTowerCube(1).getValue());
		    	sendFoxCommand(ScoreField.TOWER_CUBE_3, MessageType.SET, FoxServer.foxData.getTowerCube(2).getValue());
		    	sendFoxCommand(ScoreField.TOWER_CUBE_4, MessageType.SET, FoxServer.foxData.getTowerCube(3).getValue());
		    	sendFoxCommand(ScoreField.TOWER_CUBE_5, MessageType.SET, FoxServer.foxData.getTowerCube(4).getValue());
		    	sendFoxCommand(ScoreField.TOWER_CUBE_6, MessageType.SET, FoxServer.foxData.getTowerCube(5).getValue());
		    	sendFoxCommand(ScoreField.TOWER_CUBE_7, MessageType.SET, FoxServer.foxData.getTowerCube(6).getValue());
		    	
		    	
		    	sendFoxCommand(ScoreField.AUTON, MessageType.SET, FoxServer.foxData.getAutonWinner().getValue());
		    	
		    	sendFoxCommand(ScoreField.RED_ORANGE_CUBES, MessageType.SET, FoxServer.foxData.getRedOrangeCubes());
		    	sendFoxCommand(ScoreField.RED_GREEN_CUBES, MessageType.SET, FoxServer.foxData.getRedGreenCubes());
		    	sendFoxCommand(ScoreField.RED_PURPLE_CUBES, MessageType.SET, FoxServer.foxData.getRedPurpleCubes());

		    	sendFoxCommand(ScoreField.BLUE_ORANGE_CUBES, MessageType.SET, FoxServer.foxData.getBlueOrangeCubes());
		    	sendFoxCommand(ScoreField.BLUE_GREEN_CUBES, MessageType.SET, FoxServer.foxData.getBlueGreenCubes());
		    	sendFoxCommand(ScoreField.BLUE_PURPLE_CUBES, MessageType.SET, FoxServer.foxData.getBluePurpleCubes());
		    	
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
		    					
		    					if(field == ScoreField.TOWER_CUBE_1
		    							|| field == ScoreField.TOWER_CUBE_2
		    							|| field == ScoreField.TOWER_CUBE_3
		    							|| field == ScoreField.TOWER_CUBE_4
		    							|| field == ScoreField.TOWER_CUBE_5
		    							|| field == ScoreField.TOWER_CUBE_6
		    							|| field == ScoreField.TOWER_CUBE_7) {
		    						int pos = 0;
		    						switch(field) {
			    						case TOWER_CUBE_1: pos = 0; break;
			    						case TOWER_CUBE_2: pos = 1; break;
			    						case TOWER_CUBE_3: pos = 2; break;
			    						case TOWER_CUBE_4: pos = 3; break;
			    						case TOWER_CUBE_5: pos = 4; break;
			    						case TOWER_CUBE_6: pos = 5; break;
			    						case TOWER_CUBE_7: pos = 6; break;
			    						default: break;
		    						}
		    						
	    							FoxServer.foxData.setTowerCube(pos, CubeType.fromInt(num));
		    					}
		    					else if(field == ScoreField.AUTON) {
		    						FoxServer.foxData.setAutonWinner(AutonWinner.fromInt(num));
		    					}
		    					else if(field == ScoreField.RED_ORANGE_CUBES) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getRedOrangeCubes() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getRedOrangeCubes() - num;
		    						}
		    						
	    							FoxServer.foxData.setRedOrangeCubes(num);
		    					}
		    					else if(field == ScoreField.RED_GREEN_CUBES) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getRedGreenCubes() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getRedGreenCubes() - num;
		    						}
		    						
	    							FoxServer.foxData.setRedGreenCubes(num);
		    					}
		    					else if(field == ScoreField.RED_PURPLE_CUBES) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getRedPurpleCubes() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getRedPurpleCubes() - num;
		    						}
		    						
	    							FoxServer.foxData.setRedPurpleCubes(num);
		    					}
		    					else if(field == ScoreField.BLUE_ORANGE_CUBES) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getBlueOrangeCubes() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getBlueOrangeCubes() - num;
		    						}
		    						
	    							FoxServer.foxData.setBlueOrangeCubes(num);
		    					}
		    					else if(field == ScoreField.BLUE_GREEN_CUBES) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getBlueGreenCubes() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getBlueGreenCubes() - num;
		    						}
		    						
	    							FoxServer.foxData.setBlueGreenCubes(num);
		    					}
		    					else if(field == ScoreField.BLUE_PURPLE_CUBES) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getBluePurpleCubes() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getBluePurpleCubes() - num;
		    						}
		    						
	    							FoxServer.foxData.setBluePurpleCubes(num);
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