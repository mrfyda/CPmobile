package cpmobile.core;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;


public final class CPManager {
	public static final String TYPE_ALL = "";
	public static final String TYPE_ALFA = "AL";
	public static final String TYPE_IC = "IC";
	public static final String TYPE_R = "R";
	public static final String TIME_ALL = "";

	public static final String ERROR_STATION = "Error: Invalid station";
	public static final String ERROR_TIME = "Error: Invalid time";
	public static final String ERROR_TYPE = "Error: Invalid type";
	public static final String ERROR_DAY = "Error: Invalid day";

	private HashMap<String, Route> _db;
	private String[][] _station;

	private CPManager() {}

	// Parses file which contains all train stations ordered by groups
	// Then performs several queries to CP website and parses each HTML answer
	// Returns CPManager object if database is created successfully
	// Otherwise returns null
	// Example of file:
	// ALL LONG COURSE IN PORTUGAL [Lisboa-Oriente,Lisboa-Santa Apolónia,...\n]
	// ALL URBAN IN LISBON
	// ALL URBAN IN PORTO
	public static CPManager createDB(String file) {
		// Parsing file

		return null;
	}

	// Loads database from file 
	// Returns CPManager object if it's successfully loaded
	// otherwise returns null
	public static CPManager loadDB(String file) {
		try {
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			CPManager manager = (CPManager) in.readObject();
			in.close();

			return manager;
		}
		catch (FileNotFoundException e) { System.err.println("Error: file " + file + " not found!"); }
		catch (IOException e) { System.err.println("Error: can't read from " + file + "!"); }
		catch (ClassNotFoundException e) { System.err.println("Error: file " + file + " is corrupt!"); }

		return null;
	}

	private boolean validateStation(String station) {
		if (station != null) {
			for (String[] array : _station)
				for (String s : array)
					if (s.equals(station))
						return true;
		}

		return false;
	}

	private boolean validateType(String type) {
		return (type != null) && (type.equals(TYPE_ALL) || type.equals(TYPE_ALFA) || type.equals(TYPE_IC) || type.equals(TYPE_R));
	}

	private boolean validateDay(int day) {
		return day >= 0 && day <= 7;
	}

	private boolean validateTime(String time) {
		if (time != null) {
			String[] t = time.split(":");

			if (t.length == 2) {
				int h = Integer.parseInt(t[0]);
				int m = Integer.parseInt(t[1]);

				return (h >= 0 && h <= 24) && (m >= 0 && m <= 60);
			}
		}

		return false;
	}

	// Verifies all parameters before query the database
	// Returns the specified route timetable if exists
	// otherwise returns null
	//
	// Example of input:
	// depart: "Entroncamento"
	// arrival: "Santarém"
	// type: "R"
	// day: 0 - means Monday (7 means holiday)
	// time: 16:00
	//
	// Example of return:
	// String[0][0] = R (type of train)
	// String[0][1] = HH:MM (time of Departure)
	// String[0][2] = HH:MM (time of Arrival)
	public String[][] query(String depart, String arrival, String type, int day, String time) {
		if (validateStation(depart) && validateStation(arrival) && validateType(type) && validateDay(day) && validateTime(time)) {
			Route r = _db.get(depart + arrival);

			if (r != null)
				return r.getTimetable(day, type, time);
		}

		return null;
	}
}
