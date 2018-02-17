package me.nallen.fox.server.eventmanager;

public class DataEvent extends java.util.EventObject {
	private static final long serialVersionUID = 1L;
	private String dataType = null;

	public DataEvent(Object source) {
        super(source);
    }
	
	public void setDataType(String type) {
		dataType = type;
	}
	public String getDataType() {
		return dataType;
	}
}
