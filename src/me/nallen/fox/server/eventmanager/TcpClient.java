package me.nallen.fox.server.eventmanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.nallen.fox.server.eventmanager.scoring.MatchScoring;
import me.nallen.fox.server.eventmanager.scoring.MatchScoringBasic;
import me.nallen.fox.server.eventmanager.scoring.MatchScoringInTheZone;
import me.nallen.fox.server.eventmanager.scoring.MatchScoringKnotAProblem;
import me.nallen.fox.server.eventmanager.scoring.MatchScoringMissileMania;
import me.nallen.fox.server.eventmanager.scoring.MatchScoringNothingButNet;
import me.nallen.fox.server.eventmanager.scoring.MatchScoringRingmaster;
import me.nallen.fox.server.eventmanager.scoring.MatchScoringSkyrise;
import me.nallen.fox.server.eventmanager.scoring.MatchScoringStarstruck;
import me.nallen.fox.server.eventmanager.scoring.MatchScoringTossUp;

public class TcpClient {
    public static final int CONNECT_OK = 0;
    public static final int CONNECT_FOX_IP_ISSUE = 1;
    public static final int CONNECT_AUTOMATION_IP_ISSUE = 2;
    public static final int CONNECT_ALREADY_CONNECTED = 3;
    
    public static final int AUDIENCE_NONE = 0;
    public static final int AUDIENCE_LOGO = 5;
	public static final int AUDIENCE_INTRO = 1;
	public static final int AUDIENCE_INMATCH = 2;
	public static final int AUDIENCE_SAVED_MATCH_RESULTS = 3;
	public static final int AUDIENCE_SCHEDULE = 12;
	public static final int AUDIENCE_RANKINGS = 4;
	public static final int AUDIENCE_SKILLS_RANKINGS = 8;
	public static final int AUDIENCE_ALLIANCE_SELECTION = 6;
	public static final int AUDIENCE_ELIM_BRACKET = 7;
	public static final int AUDIENCE_SLIDES = 11;
	public static final int AUDIENCE_INSPECTION = 14;
	
	public static final int AUTON_RED = 2;
	public static final int AUTON_NONE = 1;
	public static final int AUTON_BLUE = 3;
	public static final int AUTON_TIE = 4;
	

	private static final int SOCKET_TIMEOUT_MS = 1000;
	private static final int SOCKET_PORT = 5555;

	private static final int NEW_MATCH_IGNORE_TIME = 200;
	private static final int FETCH_MATCH_SCORE_TIMEOUT = 2000;
	
	private static final int NUM_MATCH_TYPES = 15;
	
	private static final Map<GameType, Integer> scoring_fields;
	static {
		Map<GameType, Integer> aMap = new HashMap<>();
		aMap.put(GameType.BASIC, MatchScoringBasic.NUM_TOTAL_FIELDS);
		aMap.put(GameType.TOSS_UP, MatchScoringTossUp.NUM_TOTAL_FIELDS);
		aMap.put(GameType.SKYRISE, MatchScoringSkyrise.NUM_TOTAL_FIELDS);
		aMap.put(GameType.MISSILE_MANIA, MatchScoringMissileMania.NUM_TOTAL_FIELDS);
		aMap.put(GameType.NOTHING_BUT_NET, MatchScoringNothingButNet.NUM_TOTAL_FIELDS);
		aMap.put(GameType.KNOT_A_PROBLEM, MatchScoringKnotAProblem.NUM_TOTAL_FIELDS);
		aMap.put(GameType.STARSTRUCK, MatchScoringStarstruck.NUM_TOTAL_FIELDS);
		aMap.put(GameType.IN_THE_ZONE, MatchScoringInTheZone.NUM_TOTAL_FIELDS);
		aMap.put(GameType.RINGMASTER, MatchScoringRingmaster.NUM_TOTAL_FIELDS);

		scoring_fields = Collections.unmodifiableMap(aMap);
	}
	
	public static final String[] authgroup_titles = new String[] {
		"Logged Out",
		"Admin",
		"Commentator",
		"Scorer",
		"Queuer",
		"Organisation"
	};
    
    private Socket socket = null;
    private BufferedReader in = null;
    private BufferedWriter out = null;
    private final LinkedList<DataListener> _listeners = new LinkedList<DataListener>();
	private Boolean is_socket_open = false;
	private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
	
	private final Boolean[] receive_finished = { false, false, false };

	private String last_error = null;

	private String username = null;
	private String enc = null;
	private int authgroup = 0;
	private GameType game_type = GameType.BASIC;

	private final LinkedList<LinkedList<Match>> matchlist = new LinkedList<LinkedList<Match>>();
	private final LinkedList<LinkedList<Ranking>> rankings = new LinkedList<LinkedList<Ranking>>();
	private LinkedList<Integer> order = new LinkedList<Integer>();
	private final LinkedList<int[]> limits = new LinkedList<int[]>();
	
	private final LinkedList<Team> teamlist = new LinkedList<Team>();
	
	private int cur_division = 0;
	private final LinkedList<String> divisions = new LinkedList<String>();
	
	private final LinkedList<int[]> queued_match = new LinkedList<int[]>();
	private final LinkedList<int[]> saved_match = new LinkedList<int[]>();
	private final LinkedList<Long> time_expiration = new LinkedList<>();
	private final LinkedList<Integer> time_remaining = new LinkedList<Integer>();
	private final LinkedList<Runnable> time_runnables = new LinkedList<Runnable>();
	private final LinkedList<Boolean> paused_flag = new LinkedList<>();
	private final LinkedList<int[]> match_mode = new LinkedList<int[]>();
	private final LinkedList<Integer> aud_display = new LinkedList<Integer>();
	private final LinkedList<Integer> auton_winner = new LinkedList<Integer>();
	private final LinkedList<String> fields = new LinkedList<String>();
	private final LinkedList<LinkedList<Integer>> division_fields = new LinkedList<LinkedList<Integer>>();
	private final LinkedList<Integer> cur_field = new LinkedList<Integer>();
	private final LinkedList<int[]> sit_out = new LinkedList<int[]>();
	
	private MatchScoring latest_scoring = null;
	private final int[] latest_scoring_request = { 0, 0, 0, 0 };
	private boolean latest_scoring_result = false;
	
	private final LinkedList<String> online_users = new LinkedList<String>();
	private final LinkedList<Message> messages = new LinkedList<Message>();
	private final int[] num_unread = { 0, 0 };
    
    public TcpClient() {
	}

    private void cleanUp() {
		is_socket_open = false;
		try {
			socket.close();
		} catch (Exception ignored) { }
		socket = null;
		in = null;
		out = null;
		
		last_error = null;
		
		username = null;
		enc = null;
		authgroup = 0;
		
		matchlist.clear();
		rankings.clear();
		order.clear();
		for(int i=0; i<5; i++) {
			limits.clear();
		}
		
		teamlist.clear();
		
		divisions.clear();

		queued_match.clear();
		saved_match.clear();

		time_expiration.clear();
		time_remaining.clear();
		time_runnables.clear();
		paused_flag.clear();
		match_mode.clear();
		aud_display.clear();
		auton_winner.clear();
		fields.clear();
		cur_field.clear();
		sit_out.clear();
		
		latest_scoring = null;
		
		online_users.clear();
		messages.clear();
		for(int i=0; i<2; i++) {
			num_unread[i] = 0;
		}
		
		try {
			socket.close();
		} catch (Exception ignored) {}
	}

	private static String getStringMD5(String string) throws Exception {
        byte[] defaultBytes = string.getBytes();
        MessageDigest algorithm = MessageDigest.getInstance("MD5");
        algorithm.reset();
        algorithm.update(defaultBytes);
        byte messageDigest[] = algorithm.digest();

        StringBuffer hexString = new StringBuffer();
        for (int i=0;i<messageDigest.length;i++) {
                String tmp = Integer.toHexString(0xFF & messageDigest[i]);
                if (tmp.length()==1)
                        hexString.append("0").append(tmp);
                else
                        hexString.append(tmp);
        }
        return hexString.toString();
	}
	
	private LinkedList<Integer> sortTeamList(Team[] list) {
		LinkedList<Integer> data = new LinkedList<Integer>();
		String[] names = new String[list.length];
		for(int i=0; i<list.length; i++) {
			names[i] = list[i].getNumber();
		}
		
		String tmp[] = Arrays.copyOf(names, names.length);
		Arrays.sort(tmp);
		
		for(int i=0; i<list.length; i++) {
			data.add(Arrays.asList(names).indexOf(tmp[i]));
		}
		return data;
	}
	
	private synchronized void fireEvent(String dataType) {
		DataEvent event = new DataEvent(this);
		event.setDataType(dataType);
		Iterator<DataListener> i = _listeners.iterator();
		while(i.hasNext())  {
			i.next().dataReceived(event);
		}
	}
	
	private void sendMessage(final String paramString) {
		new Thread(new Runnable(){
			@Override
			public void run(){
				if (out != null) {
					try {
						out.write(paramString + '\n');
						out.flush();
					} catch (Exception ex) { ex.printStackTrace(); }
				}
			}
		}).start();
	}

	private void calculateTimeRemaining(int division) {
		int remaining = (int) (time_expiration.get(division) - System.currentTimeMillis());
		time_remaining.set(division, remaining > 0 ? remaining : 0);
		fireEvent("7");
	}

	private void listener() {
		new Thread(new Runnable() {
			public void run() {
				if (socket == null)
					return;
				while (true) {
					if(authgroup <= 0) {
						return;
					}
					String[] parts;
					try {
						String str = in.readLine();
						if(str != null) {
							parts = str.split("" + ((char)29), -1);
							if(parts.length > 0) {
								boolean valid = false;
								boolean valid_message = false;

								if(parts[0].equals("2") && (parts.length % 12 == 2)) {
									valid = true;
									receive_finished[0] = true;
									
									for(int i=0; i<matchlist.size(); i++) {
										matchlist.get(i).clear();
									}
									for(int i=0; i< parts.length/12; i++) {
										Date scheduled = new Date(Long.parseLong(parts[(1 + i*12)]));
										int division = Integer.parseInt(parts[(2 + i*12)])-1;

										if(division < divisions.size()) {
											int new_type = Integer.parseInt(parts[(3 + i * 12)]);
											int new_num = Integer.parseInt(parts[(5 + i * 12)]);
											int new_round = Integer.parseInt(parts[(4 + i * 12)]);
											int pos = limits.get(division)[new_type - 1] - 1;
											while (pos++ < (matchlist.get(division).size() - 1)) {
												if (matchlist.get(division).get(pos).getType() < new_type)
													continue;
												if (matchlist.get(division).get(pos).getType() > new_type)
													break;
												if (matchlist.get(division).get(pos).getRound() < new_round)
													continue;
												if (matchlist.get(division).get(pos).getRound() > new_round)
													break;
												if (matchlist.get(division).get(pos).getNum() < new_num)
													continue;
												if (matchlist.get(division).get(pos).getNum() > new_num)
													break;
											}
											matchlist.get(division).add(pos, new Match(new_type, new_round, new_num, new int[]{Integer.parseInt(parts[(6 + i * 12)]), Integer.parseInt(parts[(7 + i * 12)]), Integer.parseInt(parts[(8 + i * 12)])}, new int[]{Integer.parseInt(parts[(9 + i * 12)]), Integer.parseInt(parts[(10 + i * 12)]), Integer.parseInt(parts[(11 + i * 12)])}, Integer.parseInt(parts[(12 + i * 12)]) - 1, scheduled));
											for (int j = new_type; j < NUM_MATCH_TYPES; j++)
												limits.get(division)[j]++;
										}
									}
								}
								else if(parts[0].equals("3") && (parts.length % 7 == 2)) {
									valid = true;
									receive_finished[1] = true;
									
									teamlist.clear();
									order.clear();
									for(int i=0; i < parts.length/7; i++) {
										teamlist.add(new Team(parts[(1 + i*7)], parts[(2 + i*7)], parts[(3 + i*7)], parts[(4 + i*7)], parts[(5 + i*7)], parts[(6 + i*7)], parts[(7 + i*7)]));
									}
									order = sortTeamList(getTeamList());
								}
								else if(parts[0].equals("4") && (parts.length == 9)) {
									int division = Integer.parseInt(parts[1])-1;

									if(division < divisions.size()) {
										int new_type = Integer.parseInt(parts[2]);
										int new_num = Integer.parseInt(parts[4]);
										int new_round = Integer.parseInt(parts[3]);
										int pos = limits.get(division)[new_type - 1] - 1;
										Match cur;
										while (pos++ < (matchlist.get(division).size() - 1)) {
											cur = matchlist.get(division).get(pos);
											if (cur.getType() == new_type) {
												if (cur.getRound() == new_round) {
													if (cur.getNum() == new_num) {
														if (System.currentTimeMillis() > cur.getIgnoreMillis()) {
															cur.setScore(Integer.parseInt(parts[5]), Integer.parseInt(parts[6]));
															cur.setSitting(Integer.parseInt(parts[7]) - 1, Integer.parseInt(parts[8]) - 1);
															valid = true;

															if (division == latest_scoring_request[0] && cur.getType() == latest_scoring_request[1] && cur.getRound() == latest_scoring_request[2] && cur.getNum() == latest_scoring_request[3]) {
																latest_scoring_result = true;
															}
														}

														break;
													} else if (cur.getNum() > new_num)
														break;
												} else if (cur.getRound() > new_round)
													break;
											} else if (cur.getType() > new_type)
												break;
										}
									}
								}
								else if(parts[0].equals("5") && (parts.length == 4)) {
									valid = true;
									final int division = Integer.parseInt(parts[1])-1;

									if(division < divisions.size()) {
										match_mode.get(division)[1] = match_mode.get(division)[0];
										match_mode.get(division)[0] = Integer.parseInt(parts[2]);
										time_expiration.set(division, System.currentTimeMillis() + Long.parseLong(parts[3]));

										if (match_mode.get(division)[0] == 2) {
											// If there's a valid queued match
											Match match = getMatch(division, queued_match.get(division)[0], queued_match.get(division)[1], queued_match.get(division)[2]);
											if (match != null) {
												match.setHasPlayed(true);
											}
										}

										if (match_mode.get(division)[0] == 0 && (Long.parseLong(parts[3]) < 0 || Long.parseLong(parts[3]) % 1000 != 0)) {
											paused_flag.set(division, true);
										} else {
											paused_flag.set(division, false);
										}

										if (match_mode.get(division)[0] != 0 || match_mode.get(division)[1] == 0) {
											calculateTimeRemaining(division);
										}

										if (match_mode.get(division)[0] != 0 && time_remaining.get(division) > 0) {
											executor.remove(time_runnables.get(division));

											time_runnables.set(division, new Runnable() {
												@Override
												public void run() {
													calculateTimeRemaining(division);

													if (time_remaining.get(division) <= 0) {
														match_mode.get(division)[1] = match_mode.get(division)[0];
														match_mode.get(division)[0] = 0;
														fireEvent("5");

														executor.remove(time_runnables.get(division));

														return;
													}

													executor.schedule(this, ((time_remaining.get(division) - 1) % 1000) + 1, TimeUnit.MILLISECONDS);
												}
											});

											executor.schedule(time_runnables.get(division), ((time_remaining.get(division) - 1) % 1000) + 1, TimeUnit.MILLISECONDS);
										} else {
											executor.remove(time_runnables.get(division));
										}
									}
								}
								else if(parts[0].equals("6") && (parts.length == 8)) {
									valid = true;
									int division = Integer.parseInt(parts[1])-1;

									if(division < divisions.size()) {
										if (canQueueNewMatch(division)) {
											queued_match.get(division)[0] = Integer.parseInt(parts[2]);
											queued_match.get(division)[1] = Integer.parseInt(parts[3]);
											queued_match.get(division)[2] = Integer.parseInt(parts[4]);
											cur_field.set(division, Integer.parseInt(parts[5]) - 1);
											sit_out.get(division)[0] = Integer.parseInt(parts[6]) - 1;
											sit_out.get(division)[1] = Integer.parseInt(parts[7]) - 1;
										}
									}
								}
								else if(parts[0].equals("8") && (parts.length == 13)) {
									valid = true;
									Date scheduled = new Date(Long.parseLong(parts[1]));
									int division = Integer.parseInt(parts[2])-1;

									if(division < divisions.size()) {
										int new_type = Integer.parseInt(parts[3]);
										int new_num = Integer.parseInt(parts[5]);
										int new_round = Integer.parseInt(parts[4]);
										int pos = limits.get(division)[new_type - 1] - 1;
										while (pos++ < (matchlist.get(division).size() - 1)) {
											if (matchlist.get(division).get(pos).getType() < new_type)
												continue;
											if (matchlist.get(division).get(pos).getType() > new_type)
												break;
											if (matchlist.get(division).get(pos).getRound() < new_round)
												continue;
											if (matchlist.get(division).get(pos).getRound() > new_round)
												break;
											if (matchlist.get(division).get(pos).getNum() < new_num)
												continue;
											if (matchlist.get(division).get(pos).getNum() > new_num)
												break;
										}
										Match match = new Match(new_type, new_round, new_num, new int[]{Integer.parseInt(parts[6]), Integer.parseInt(parts[7]), Integer.parseInt(parts[8])}, new int[]{Integer.parseInt(parts[9]), Integer.parseInt(parts[10]), Integer.parseInt(parts[11])}, Integer.parseInt(parts[12]) - 1, scheduled);
										match.setIgnoreMillis(System.currentTimeMillis() + NEW_MATCH_IGNORE_TIME);
										matchlist.get(division).add(pos, match);
										for (int j = new_type; j < NUM_MATCH_TYPES; j++)
											limits.get(division)[j]++;
									}
								}
								else if(parts[0].equals("9") && (parts.length == 6 + scoring_fields.get(game_type))) {
									valid = true;
									int is_detailed = Integer.parseInt(parts[5]);
									
									if(is_detailed == 0) {
										if(game_type == GameType.BASIC) {
											latest_scoring = new MatchScoringBasic(Arrays.copyOfRange(parts, 6, parts.length));
										}
										else if(game_type == GameType.TOSS_UP) {
											latest_scoring = new MatchScoringTossUp(Arrays.copyOfRange(parts, 6, parts.length));
										}
										else if(game_type == GameType.SKYRISE) {
											latest_scoring = new MatchScoringSkyrise(Arrays.copyOfRange(parts, 6, parts.length));
										}
                                        else if(game_type == GameType.NOTHING_BUT_NET) {
                                            latest_scoring = new MatchScoringNothingButNet(Arrays.copyOfRange(parts, 6, parts.length));
                                        }
                                        else if(game_type == GameType.SKYRISE) {
                                            latest_scoring = new MatchScoringStarstruck(Arrays.copyOfRange(parts, 6, parts.length));
                                        }
										else if(game_type == GameType.IN_THE_ZONE) {
											latest_scoring = new MatchScoringInTheZone(Arrays.copyOfRange(parts, 6, parts.length));
										}
										else if(game_type == GameType.RINGMASTER) {
											latest_scoring = new MatchScoringRingmaster(Arrays.copyOfRange(parts, 6, parts.length));
										}
									}
								}
								else if(parts[0].equals("10") && (parts.length % 8 == 2)) {
									valid = true;
									int[] cur_pos = new int[rankings.size()];
									Arrays.fill(cur_pos, 0);
									for(int i=0; i < parts.length/8; i++) {
										int division = Integer.parseInt(parts[(2 + i*8)]) - 1;

										if(division < divisions.size()) {
											if (rankings.get(division).size() > cur_pos[division]) {
												rankings.get(division).get(cur_pos[division]).update(Integer.parseInt(parts[(1 + i * 8)]), Integer.parseInt(parts[(3 + i * 8)]), Integer.parseInt(parts[(4 + i * 8)]), Integer.parseInt(parts[(5 + i * 8)]), Integer.parseInt(parts[(6 + i * 8)]), Integer.parseInt(parts[(7 + i * 8)]), Integer.parseInt(parts[(8 + i * 8)]));
											} else {
												rankings.get(division).add(new Ranking(Integer.parseInt(parts[(1 + i * 8)]), Integer.parseInt(parts[(3 + i * 8)]), Integer.parseInt(parts[(4 + i * 8)]), Integer.parseInt(parts[(5 + i * 8)]), Integer.parseInt(parts[(6 + i * 8)]), Integer.parseInt(parts[(7 + i * 8)]), Integer.parseInt(parts[(8 + i * 8)])));
											}
											cur_pos[division]++;
										}
									}
								}
								else if(parts[0].equals("11") && (parts.length == 5)) {
									valid = true;
									int division = Integer.parseInt(parts[1])-1;

									if(division < divisions.size()) {
										saved_match.get(division)[0] = Integer.parseInt(parts[2]);
										saved_match.get(division)[1] = Integer.parseInt(parts[3]);
										saved_match.get(division)[2] = Integer.parseInt(parts[4]);
									}
								}
								else if(parts[0].equals("12") && (parts.length == 3)) {
									valid = true;
									int division = Integer.parseInt(parts[1])-1;

									if(division < divisions.size()) {
										aud_display.set(division, Integer.parseInt(parts[2]));
									}
								}
								else if(parts[0].equals("13") && (parts.length == 3)) {
									valid = true;
									int division = Integer.parseInt(parts[1])-1;

									if(division < divisions.size()) {
										auton_winner.set(division, Integer.parseInt(parts[2]));
									}
								}
								else if(parts[0].equals("14") && (parts.length == 2)) {
									valid = true;
									valid_message = true;
									messages.add(0, new Message(Message.BROADCAST, parts[1].trim(), new Date()));
									num_unread[Message.BROADCAST]++;
								}
								else if(parts[0].equals("15") && (parts.length == 3)) {
									valid = true;
									valid_message = true;
									messages.add(0, new Message(Message.PM, parts[1], parts[2].trim(), new Date()));
									num_unread[Message.PM]++;
								}
								else if(parts[0].equals("17")) {
									valid = true;
									online_users.clear();
									online_users.addAll(Arrays.asList(parts).subList(1, parts.length - 1));
								}
								else if(parts[0].equals("18") && (parts.length == 3)) {
									if(parts[2].equals("0")) {
										valid = true;
										online_users.remove(parts[1]);
									}
									else if(parts[2].equals("1")) {
										valid = true;
										online_users.add(parts[1]);
									}
								}
								else if(parts[0].equals("19") && (parts.length == 2)) {
									valid = true;
									authgroup = Integer.parseInt(parts[1]);
								}
								else if(parts[0].equals("22")) {
									valid = true;
									fields.clear();
									for(int i=0; i<division_fields.size(); i++) {
										division_fields.get(i).clear();
									}
									for(int i=0; i < (parts.length-2)/2; i++) {
										int division = Integer.parseInt(parts[2 + i*2]) - 1;

										if(division < divisions.size()) {
											fields.add(parts[1 + i * 2]);
											division_fields.get(division).add(i);
										}
									}
								}
								else if(parts[0].equals("23")) {
									valid = true;
									GameType parsedType = GameType.createFromServerIdentifier(parts[1]);
									if(parsedType != null) {
										game_type = parsedType;
									}
								}
								else if(parts[0].equals("24")) {
									valid = true;
									receive_finished[2] = true;
									
									divisions.clear();
									limits.clear();
									matchlist.clear();
									rankings.clear();
									queued_match.clear();
									saved_match.clear();
									time_expiration.clear();
									time_remaining.clear();
									time_runnables.clear();
									paused_flag.clear();
									match_mode.clear();
									aud_display.clear();
									auton_winner.clear();
									cur_field.clear();
									sit_out.clear();
									fields.clear();
									
									for(int i = 1; i < parts.length - 1; i++) {
										divisions.add(parts[i]);
										int[] newLimits = new int[NUM_MATCH_TYPES];
										for(int j=0; j<NUM_MATCH_TYPES; j++) {
											newLimits[j] = 0;
										}
										limits.add(newLimits);
										matchlist.add(new LinkedList<Match>());
										rankings.add(new LinkedList<Ranking>());
										queued_match.add(new int[] { -1, -1, -1 });
										saved_match.add(new int[] { -1, -1, -1 });
										time_expiration.add(System.currentTimeMillis());
										time_remaining.add(0);
										time_runnables.add(null);
										paused_flag.add(false);
										match_mode.add(new int[] { 0, 0 });
										aud_display.add(0);
										auton_winner.add(0);
										cur_field.add(0);
										sit_out.add(new int[] { 2, 2 });
										division_fields.add(new LinkedList<Integer>());
									}
								}
								else if(parts[0].equals("42")) {
									valid = true;
								}
								else if(parts[0].equals("1337")) {
									valid = true;
								}
								
								if(valid) {
									fireEvent(parts[0]);
								}
							}
						}
						else {
							if(is_socket_open) {
								logout();
							}
							break;
						}

						Thread.sleep(10);
					}
					catch (Exception e) {
						if(is_socket_open) {
							e.printStackTrace();
							logout();
						}
						break;
					}
				}
			}
		}).start();
	}
	
	public int getAuthGroup() {
		return authgroup;
	}
	public void setOffline(GameType n_game_type) {
		cleanUp();
		game_type = n_game_type;
		authgroup = -1;
	}
	
	public Boolean getReceiveFinished() {
		for(Boolean val : receive_finished) {
			if(!val)
				return false;
		}
		
		return true;
	}
	
	public GameType getGameType() {
		return game_type;
	}

	public boolean getGameTypeHasAPs() {
		switch(game_type) {
			case STARSTRUCK:
			case IN_THE_ZONE: return true;
			default: return false;
		}
	}

	public String getError() {
		return last_error;
	}

	public boolean getLoggedIn() {
		return authgroup != 0;
	}
	
	public void setCurDivision(int div) {
		cur_division = div;
	}
	public int getCurDivision() {
		return cur_division;
	}
	
	public String[] getDivisions() {
		return divisions.toArray(new String[divisions.size()]);
	}
	
	private int[] getQueuedMatch(int division) {
		return queued_match.get(division);
	}
	public int[] getQueuedMatch() {
		return getQueuedMatch(cur_division);
	}
	
	private int[] getSavedMatch(int division) {
		return saved_match.get(division);
	}
	public int[] getSavedMatch() {
		return getSavedMatch(cur_division);
	}

	private int getNumMatches(int division) {
		return matchlist.get(division).size();
	}
	public int getNumMatches() {
		return getNumMatches(cur_division);
	}
	
	private Match[] getMatchList(int division) {
		return matchlist.get(division).toArray(new Match[matchlist.get(division).size()]);
	}
	public Match[] getMatchList() {
		return getMatchList(cur_division);
	}
	
	private Match getMatch(int division, int s_type, int s_round, int s_num) {
		if(s_type > -1 && s_round > -1 && s_num > -1) {
			int pos = limits.get(division)[s_type-1]-1;
			Match cur;
			while(pos++ < (matchlist.get(division).size()-1)) {
				cur = matchlist.get(division).get(pos);
				if(cur.getType() == s_type) {
					if(cur.getRound() == s_round) {
						if(cur.getNum() == s_num) {
							return cur;
						}
						else if(cur.getNum() > s_num)
							break;
					}
					else if(cur.getRound() > s_round)
						break;
				}
				else if(cur.getType() > s_type)
					break;
			}	
		}
		return null;
	}
	public Match getMatch(int s_type, int s_round, int s_num) {
		return getMatch(cur_division, s_type, s_round, s_num);
	}
	
	private int[] getMatchData(int division, int pos) {
		Match tmp = matchlist.get(division).get(pos);
		return new int[] { tmp.getType(), tmp.getRound(), tmp.getNum() };
	}
	public int[] getMatchData(int pos) {
		return getMatchData(cur_division, pos);
	}
	
	private int getMatchPos(int division, int s_type, int s_round, int s_num) {
		if(s_type == -1 || s_round == -1 || s_num == -1) {
			return -1;
		}
		int pos = limits.get(division)[s_type-1]-1;
		Match cur;
		while(pos++ < (matchlist.get(division).size()-1)) {
			cur = matchlist.get(division).get(pos);
			if(cur.getType() == s_type) {
				if(cur.getRound() == s_round) {
					if(cur.getNum() == s_num) {
						return pos;
					}
					else if(cur.getNum() > s_num)
						break;
				}
				else if(cur.getRound() > s_round)
					break;
			}
			else if(cur.getType() > s_type)
				break;
		}
		return -1;
	}
	public int getMatchPos(int s_type, int s_round, int s_num) {
		return getMatchPos(cur_division, s_type, s_round, s_num);
	}

	public Team[] getTeamList() {
		return teamlist.toArray(new Team[teamlist.size()]);
	}
	public Team getTeam(int id) {
		return teamlist.get(id);
	}
	public Integer[] getTeamOrder() {
		return order.toArray(new Integer[order.size()]);
	}
	
	private Ranking[] getRankings(int division) {
		return rankings.get(division).toArray(new Ranking[rankings.get(division).size()]);
	}
	public Ranking[] getRankings() {
		return getRankings(cur_division);
	}
	
	private int getRankForTeam(int division, int id) {
		for(int i=0; i<rankings.get(division).size(); i++) {
			if(rankings.get(division).get(i).getTeam() == id) {
				return (i+1);
			}
		}
		
		return -1;
	}
	public int getRankForTeam(int id) {
		return getRankForTeam(cur_division, id);
	}
	
	private Ranking getRankDataForTeam(int division, int id) {
		int pos = getRankForTeam(division, id) - 1;
		if(pos >= 0) {
			return rankings.get(division).get(pos);
		}
		
		return null;
	}
	public Ranking getRankDataForTeam(int id) {
		return getRankDataForTeam(cur_division, id);
	}
	
	public String getUsername() {
		return username;
	}
	
	private int getTimeRemaining(int division) {
		return time_remaining.get(division);
	}
	public int getTimeRemaining() {
		return getTimeRemaining(cur_division);
	}
	
	private String getStringTimeRemaining(int division) {
		int totalSeconds = (int) Math.ceil(time_remaining.get(division) / 1000.0);
		int seconds = totalSeconds % 60;
		return (totalSeconds / 60) + ":" + ((seconds < 10) ? "0" + seconds : seconds);
	}
	public String getStringTimeRemaining() {
		return getStringTimeRemaining(cur_division);
	}
	
	private int[] getMatchMode(int division) {
		return match_mode.get(division);
	}
	public int[] getMatchMode() {
		return getMatchMode(cur_division);
	}

	private boolean getPausedFlag(int division) {
		return paused_flag.get(division);
	}
	public boolean getPausedFlag() {
		return getPausedFlag(cur_division);
	}
	
	private int getAudienceDisplay(int division) {
		return aud_display.get(division);
	}
	public int getAudienceDisplay() {
		return getAudienceDisplay(cur_division);
	}
	
	private int getAutonWinner(int division) {
		return auton_winner.get(division);
	}
	public int getAutonWinner() {
		return getAutonWinner(cur_division);
	}
	
	private String[] getFields(int division) {
		String[] result = new String[division_fields.get(division).size()];
		
		int i = 0;
		for(Integer field : division_fields.get(division)) {
			result[i++] = fields.get(field);
		}
		
		return result;
	}
	public String[] getFields() {
		return getFields(cur_division);
	}
	
	public String getField(int id) {
		return fields.get(id);
	}
	
	private Integer mapDivisionFieldToEventField(int division, int id) {
		return division_fields.get(division).get(id);
	}
	public Integer mapDivisionFieldToEventField(int id) {
		return mapDivisionFieldToEventField(cur_division, id);
	}
	
	private Integer mapEventFieldToDivisionField(int division, int id) {
		return division_fields.get(division).indexOf(id);
	}
	public Integer mapEventFieldToDivisionField(int id) {
		return mapEventFieldToDivisionField(cur_division, id);
	}
	
	private int getCurField(int division) {
		return cur_field.get(division);
	}
	public int getCurField() {
		return getCurField(cur_division);
	}
	
	private int[] getSittingOut(int division) {
		return sit_out.get(division);
	}
	public int[] getSittingOut() {
		return getSittingOut(cur_division);
	}
	
	public Message[] getMessages() {
		return messages.toArray(new Message[messages.size()]);
	}
	public Message getMessage(int pos) {
		return messages.get(pos);
	}
	public int[] getUnread() {
		return num_unread;
	}
	
	public String[] getOnlineUsers() {
		return online_users.toArray(new String[online_users.size()]);
	}
	public int getNumOnlineUsers() {
		return online_users.size();
	}
	
	private MatchScoring getMatchScoring(int division, int type, int round, int num) {
		latest_scoring = null;
		sendMessage("9" + ((char)29) + (division+1) + ((char)29) + type + ((char)29) + round + ((char)29) + num);
		
		int cur_step = 1;
		while(latest_scoring == null) {
			try {
				Thread.sleep(10);
			}
			catch(Exception ignored) {}
			
			if(10*cur_step++ > FETCH_MATCH_SCORE_TIMEOUT) {
				return null;
			}
		}
		return latest_scoring;
	}
	public MatchScoring getMatchScoring(int type, int round, int num) {
		return getMatchScoring(cur_division, type, round, num);
	}
	
	private Boolean setMatchScoring(int division, int type, int round, int num, MatchScoring scoring) {
		latest_scoring_result = false;
		latest_scoring_request[0] = division;
		latest_scoring_request[1] = type;
		latest_scoring_request[2] = round;
		latest_scoring_request[3] = num;
		
		sendMessage("4" + ((char)29) + (division+1) + ((char)29) + type + ((char)29) + round + ((char)29) + num + ((char)29) + scoring.getSendingFormat());
		
		int cur_step = 1;
		while(!latest_scoring_result) {
			try {
				Thread.sleep(10);
			}
			catch(Exception ignored) {}
			
			if(10*cur_step++ > 2000) {
				return false;
			}
		}
		
		return true;
	}
	public Boolean setMatchScoring(int type, int round, int num, MatchScoring scoring) {
		return setMatchScoring(cur_division, type, round, num, scoring);
	}

	public boolean canQueueNewMatch(int division) {
		return paused_flag.get(division) || (match_mode.get(division)[0] == 0 && match_mode.get(division)[1] != 2);
	}
	public boolean canQueueNewMatch() {
		return canQueueNewMatch(cur_division);
	}
	
	public void queueMatch(int type, int round, int num) {
		queueMatch(type, round, num, false);
	}
	public void queueMatch(int division, int type, int round, int num) {
		queueMatch(division, type, round, num, false);
	}
	public void queueMatch(int type, int round, int num, boolean init) {
		queueMatch(cur_division, type, round, num, init);
	}
	private void queueMatch(int division, int type, int round, int num, boolean init) {
		if(canQueueNewMatch(division)) {
			sendMessage("6" + ((char)29) + (division+1) + ((char)29) + type + ((char)29) + round + ((char)29) + num);
			if(init) {
				setAudienceDisplay(AUDIENCE_INTRO);
			}
			setAutonWinner(AUTON_NONE);
		}
	}
	
	private void saveMatch(int division, int type, int round, int num) {
		sendMessage("11" + ((char)29) + (division+1) + ((char)29) + type + ((char)29) + round + ((char)29) + num);
	}
	public void saveMatch(int type, int round, int num) {
		saveMatch(cur_division, type, round, num);
	}
	
	private void setControl(int division, int type) {
		sendMessage("5" + ((char)29) + (division+1) + ((char)29) + type);
	}
	public void setControl(int type) {
		setControl(cur_division, type);
	}
	
	private void setAutonWinner(int division, int winner) {
		if(queued_match.get(division)[0] > -1 && queued_match.get(division)[1] > -1 && queued_match.get(division)[2] > -1) {
			sendMessage("13" + ((char)29) + (division+1) + ((char)29) + winner);
		}
	}
	public void setAutonWinner(int winner) {
		setAutonWinner(cur_division, winner);
	}
	
	private void setAudienceDisplay(int division, int display) {
		sendMessage("12" + ((char)29) + (division+1) + ((char)29) + display);
	}
	public void setAudienceDisplay(int display) {
		setAudienceDisplay(cur_division, display);
	}
	
	private void setFieldSelect(int division, int field) {
		sendMessage("21" + ((char)29) + (division+1) + ((char)29) + (field+1));
	}
	public void setFieldSelect(int field) {
		setFieldSelect(cur_division, field);
	}
	
	public void sendPM(String destination, String message) {
		sendMessage("15" + ((char)29) + destination + ((char)29) + message);
	}
	
	public String encryptPassword(String str) {
		try {
			return getStringMD5(str+"thisll throw off decrypters!");
		}
		catch(Exception e) {
			return "";
		}
	}
	public boolean authenticateAgainst(String str) {
		return str.equals(enc);
	}

	public boolean login(String serverip, String user, String encrypted) {
		if(authgroup <= 0) {
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress(serverip, SOCKET_PORT), SOCKET_TIMEOUT_MS);
				out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				is_socket_open = true;
			}
			catch(Exception e) {
				cleanUp();
				last_error = "Invalid IP";
				return false;
			}
			
			try {
				String rcv = in.readLine();
				
				sendMessage(user+((char)29)+getStringMD5(encrypted+rcv));
				
				rcv = in.readLine();
				
				if(rcv.equals("0")) {
					cleanUp();
					last_error = "Invalid Login";
					return false;
				}
				else {
					username = user;
					enc = encrypted;
					authgroup = Integer.parseInt(rcv);
					listener();
					return true;
				}
			}
			catch(Exception e) {
				cleanUp();
				last_error = "Invalid Login";
				return false;
			}
		}
		else {
			last_error = "Already Logged In";
		}
		return false;
	}
	public void logout() {
		if (getLoggedIn()) {
			cleanUp();
			fireEvent("-1");
		}
	}
	
	public boolean changePassword(String str) {
		if(authgroup > 0) {
			sendMessage("16" + ((char)29) + str);
			
			logout();
			
			return true;
		}
		else {
			last_error = "Not Logged In";
		}
		return false;
	}
	
	public void clearUnread() {
		for(int i=0; i<2; i++) {
			num_unread[i] = 0;
		}
	}
	
	public synchronized void addDataListener(DataListener listener)  {
		_listeners.add(listener);
	}
	public synchronized void removeDataListener(DataListener listener)   {
		_listeners.remove(listener);
	}
}