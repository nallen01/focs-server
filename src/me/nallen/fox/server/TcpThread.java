package me.nallen.fox.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import me.nallen.fox.server.FoxData.ElevatedState;

public class TcpThread extends Thread implements DataListener {
    private Socket socket = null;
    private BufferedReader in = null;
    private BufferedWriter out = null;
    
    public enum ScoreField {
		RED_FAR_STARS(0),
		RED_FAR_CUBES(1),
		RED_NEAR_STARS(2),
		RED_NEAR_CUBES(3),
		RED_ELEVATION(4),
		RED_AUTON(10),
		
		BLUE_FAR_STARS(5),
		BLUE_FAR_CUBES(6),
		BLUE_NEAR_STARS(7),
		BLUE_NEAR_CUBES(8),
		BLUE_ELEVATION(9),
    	BLUE_AUTON(11),
    	
    	PAUSED(12),
    	HISTORY(13),
    	LARGE_HISTORY(14),
    	
    	CLEAR(15);
		
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
	
	public void run() {
		try {
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    
		    // Check that they can use it
		    
		    if(out != null) {
		    	sendMessage("1");
		    	
		    	sendFoxCommand(ScoreField.RED_AUTON, MessageType.SET, FoxServer.foxData.getRedAuton() ? 1 : 0);
		    	sendFoxCommand(ScoreField.RED_ELEVATION, MessageType.SET, FoxServer.foxData.getRedElevation().getValue());
		    	sendFoxCommand(ScoreField.RED_FAR_CUBES, MessageType.SET, FoxServer.foxData.getRedFarCubes());
		    	sendFoxCommand(ScoreField.RED_FAR_STARS, MessageType.SET, FoxServer.foxData.getRedFarStars());
		    	sendFoxCommand(ScoreField.RED_NEAR_CUBES, MessageType.SET, FoxServer.foxData.getRedNearCubes());
		    	sendFoxCommand(ScoreField.RED_NEAR_STARS, MessageType.SET, FoxServer.foxData.getRedNearStars());

		    	sendFoxCommand(ScoreField.BLUE_AUTON, MessageType.SET, FoxServer.foxData.getBlueAuton() ? 1 : 0);
		    	sendFoxCommand(ScoreField.BLUE_ELEVATION, MessageType.SET, FoxServer.foxData.getBlueElevation().getValue());
		    	sendFoxCommand(ScoreField.BLUE_FAR_CUBES, MessageType.SET, FoxServer.foxData.getBlueFarCubes());
		    	sendFoxCommand(ScoreField.BLUE_FAR_STARS, MessageType.SET, FoxServer.foxData.getBlueFarStars());
		    	sendFoxCommand(ScoreField.BLUE_NEAR_CUBES, MessageType.SET, FoxServer.foxData.getBlueNearCubes());
		    	sendFoxCommand(ScoreField.BLUE_NEAR_STARS, MessageType.SET, FoxServer.foxData.getBlueNearStars());

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
		    					
		    					if(field == ScoreField.RED_FAR_CUBES) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getRedFarCubes() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getRedFarCubes() - num;
		    						}
		    						
	    							FoxServer.foxData.setRedFarCubes(num);
		    					}
		    					else if(field == ScoreField.RED_FAR_STARS) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getRedFarStars() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getRedFarStars() - num;
		    						}
		    						
	    							FoxServer.foxData.setRedFarStars(num);
		    					}
		    					else if(field == ScoreField.RED_NEAR_CUBES) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getRedNearCubes() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getRedNearCubes() - num;
		    						}
		    						
	    							FoxServer.foxData.setRedNearCubes(num);
		    					}
		    					else if(field == ScoreField.RED_NEAR_STARS) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getRedNearStars() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getRedNearStars() - num;
		    						}
		    						
	    							FoxServer.foxData.setRedNearStars(num);
		    					}
		    					else if(field == ScoreField.RED_ELEVATION) {
		    						ElevatedState state = ElevatedState.fromInt(num);
	    							FoxServer.foxData.setRedElevation(state);
		    					}
		    					else if(field == ScoreField.RED_AUTON) {
	    							FoxServer.foxData.setRedAuton(num > 0);
		    					}
		    					else if(field == ScoreField.BLUE_FAR_CUBES) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getBlueFarCubes() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getBlueFarCubes() - num;
		    						}
		    						
	    							FoxServer.foxData.setBlueFarCubes(num);
		    					}
		    					else if(field == ScoreField.BLUE_FAR_STARS) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getBlueFarStars() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getBlueFarStars() - num;
		    						}
		    						
	    							FoxServer.foxData.setBlueFarStars(num);
		    					}
		    					else if(field == ScoreField.BLUE_NEAR_CUBES) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getBlueNearCubes() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getBlueNearCubes() - num;
		    						}
		    						
	    							FoxServer.foxData.setBlueNearCubes(num);
		    					}
		    					else if(field == ScoreField.BLUE_NEAR_STARS) {
		    						if(type == MessageType.ADD) {
		    							num = FoxServer.foxData.getBlueNearStars() + num;
		    						}
		    						else if(type == MessageType.SUBTRACT) {
		    							num = FoxServer.foxData.getBlueNearStars() - num;
		    						}
		    						
	    							FoxServer.foxData.setBlueNearStars(num);
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
		    					else if(field == ScoreField.CLEAR) {
		    						FoxServer.foxData.clear();
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