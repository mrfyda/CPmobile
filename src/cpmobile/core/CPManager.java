package cpmobile.core;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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

	private CPManager(HashMap<String, Route> db, String[][] station) {
		_db = db;
		_station = station;
	}

	// Parses file which contains all train stations ordered by groups
	// Then performs several queries to CP website and parses each HTML answer
	// Returns CPManager object if database is created successfully
	// Otherwise returns null
	// Example of file:
	// ALL LONG COURSE IN PORTUGAL (Lisboa-Oriente,Lisboa-Santa Apolónia,...\n)
	// ALL URBAN IN LISBON
	// ALL URBAN IN PORTO
	public static CPManager createDB(String file) {
		LinkedList<String[]> station = new LinkedList<String[]>();
		HashMap<String, Route> db = new HashMap<String, Route>();

		// Parsing file
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String s;

			while ((s = in.readLine()) != null) {
				station.add(s.split(","));
			}

			in.close();
		}
		catch (FileNotFoundException e) { System.err.println("Error: file " + file + " not found!"); }
		catch (IOException e) { System.err.println("Error: can't read from " + file + "!"); }

		// Generating timetables
		try {
			Pattern p = Pattern.compile("<td align=\"center\">(\\d?\\dh\\d\\d|\\w?\\w)</td>");
			ArrayList<String> res = new ArrayList<String>();
			Matcher m;

			for (String[] group : station) {
				int iLength = group.length - 1;
				int jLength = group.length;

				// For each element compares with all the others
				// matching i and j begin i the begin and j the other elements in the array
				for (int i = 0; i < iLength; ++i)
					for (int j = i + 1; j < jLength; ++j) {
						// In case this route was already processed
						if (db.containsKey(group[i] + group[j]))
							continue;

						for (int bool = 0; bool < 2; bool++) {
							URL url = new URL("http://www.cp.pt/cp/searchTimetable.do");
							URLConnection conn = url.openConnection();
							conn.setDoOutput(true);

							// Sending post to website
							OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

							if (j == 0)
								writer.write("depart=" + group[i] + "&arrival=" + group[j] + 
									"&date=2012-12-15&timeType=Partida&time=&regionals=regionals&returnDate=&returnTimeType=Partida&returnTime=");
							else
								writer.write("depart=" + group[i] + "&arrival=" + group[j] + 
									"&date=2012-12-15&timeType=Partida&time=&regionals=regionals&returnDate=&returnTimeType=Partida&returnTime=");

							writer.flush();

							// Reading answer
							String line;
							BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

							// Parsing answer
							while ((line = reader.readLine()) != null) {
							    m = p.matcher(line);

							    if (m.find())
							    	res.add(m.group());
							}

							// Creating route and adding to the other
							Route r = new Route(group[i], group[j]);
							int length = res.size();

							// For each existing train, insert in route
							for (int comb = 0; comb < length; comb += 4)
								r.insertTrain(bool * 6, res.get(comb), res.get(comb), res.get(comb + 2));

							res.clear();
							db.put(group[0] + group[1], r);
						}
					}

				return new CPManager(db, (String[][]) station.toArray());
			}
		}
		catch (IOException e) { e.printStackTrace(); }

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
