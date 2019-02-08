package me.nallen.fox.server.eventmanager;

public class Team {
	private String country = null;
	private String location = null;
	private String name = null;
	private String number = null;
	private String school = null;
	private String shortName = null;
	private String state = null;

	public Team(String n_number, String n_name, String n_school, String n_location, String n_country, String n_shortName, String n_state) {
		number = n_number;
		name = n_name;
		school = n_school;
		location = n_location;
		country = n_country;
		shortName = n_shortName;
		state = n_state;
	}

	public String getCountry() {
		return country;
	}

	public String getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public String getNumber() {
		return number;
	}

	public String getSchool() {
		return school;
	}

	public String getShortName() {
		return shortName;
	}

	public String getState() {
		return state;
	}

	public String getFullLocation() {
		String tmp = "";
		if (location.length() > 0)
			tmp += ", " + location;
		if (state.length() > 0)
			tmp += ", " + state;
		if (country.length() > 0)
			tmp += ", " + country;
		if(tmp.length() > 0)
			return tmp.substring(2);
		else
			return "";
	}
}