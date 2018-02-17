package me.nallen.fox.server.eventmanager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
	public static final int BROADCAST = 0;
	public static final int PM = 1;
	
	private final int type;
	private final String sender;
	private final String message;
	private final Date timestamp;
	
	public Message(int n_type, String n_sender, String n_message, Date n_timestamp) {
		type = n_type;
		sender = n_sender;
		message = n_message;
		timestamp = n_timestamp;
	}
	public Message(int n_type, String n_message, Date n_timestamp) {
		this(n_type, "", n_message, n_timestamp);
	}
	
	public int getType() {
		return type;
	}
	public String getSender() {
		return sender;
	}
	public String getMessage() {
		return message;
	}
	public String getFormattedMessage() {
		if(type == Message.BROADCAST) {
			return message;
		}
		else if(type == Message.PM) {
			return "<b>" + sender + "</b>: " + message;
		}
		return "";
	}
	public Date getTimeStamp() {
		return timestamp;
	}
	public String getTimeStampString() {
		DateFormat df = new SimpleDateFormat("hh:mma dd/MM/yy");
		return df.format(timestamp);
	}
}