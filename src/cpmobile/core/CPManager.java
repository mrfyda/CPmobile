package cpmobile.core;
import java.io.*;
import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public final class CPManager implements Serializable {

	private static final long serialVersionUID = 924866340298005552L;

	private static final String PATTERN = "\\s*<td align=\"center\">(.?\\d?\\dh\\d\\d|\\w?\\w)</td>";
	
	public static final String TYPE_ALL = "";
	public static final String TYPE_ALFA = "AL";
	public static final String TYPE_IC = "IC";
	public static final String TYPE_R = "R";
	public static final String TIME_ALL = "";

	public Map<String, Route> _db;
	private List<List<String>> _station;

	private CPManager(Map<String, Route> db, List<List<String>> station) {
		_db = db;
		_station = station;
	}
	
	private static String keygen(String depart, String arrival) {
		return depart + arrival;
	}

	private boolean validateStation(String name) {
		if (name != null) {
			for (List<String> list : _station)
				for (String s : list)
					if (s.equals(name))
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
			if (time.equals(TIME_ALL))
				return true;
			
			String[] t = time.split("h");

			if ((t != null) && (t.length == 2)) {
				int h = Integer.parseInt(t[0]);
				int m = Integer.parseInt(t[1]);

				return (h >= 0 && h <= 24) && (m >= 0 && m <= 60);
			}
		}

		return false;
	}
	
	private static Route createRoute(String s1, String s2) {
		URL url = null;
		List<String> res = new LinkedList<String>(); // Temporary list to keep timetables of each route
		Route r = new Route(s1, s2);
		
		try {
			url = new URL("http://www.cp.pt/cp/searchTimetable.do");
		}
		catch (MalformedURLException e) { System.err.println("Error: Invalid URL!"); }
		
		for (int isWeekend = 0; isWeekend < 2; ++isWeekend) {
			URLConnection conn = null;
			
			try {
				conn = url.openConnection();
				conn.setDoOutput(true);
			}
			catch (IOException e) { System.err.println("Error: Can't connect to website!"); }

			try {
				// Sending post to website
				OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
	
				if (isWeekend == 0) 
					writer.write("depart=" + s1 + "&arrival=" + s2 + "&date=2012-12-17&timeType=Partida&time=&regionals=regionals&returnDate=&returnTimeType=Partida&returnTime=");
				else
					writer.write("depart=" + s1 + "&arrival=" + s2 + "&date=2012-12-15&timeType=Partida&time=&regionals=regionals&returnDate=&returnTimeType=Partida&returnTime=");
				
				writer.flush();
	
				// Reading answer
				String line;
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	
				// Parsing answer
				while ((line = reader.readLine()) != null)					
				    if (line.matches(PATTERN))
				    	res.add(line.replaceFirst(PATTERN, "$1"));
			}
			catch (IOException e) { System.err.println("Error: Can't read from file!"); }

			// For each existing train, insert in route
			Iterator<String> it;
			for (it = res.iterator(); it.hasNext(); it.next())
				r.insertTrain(isWeekend + 4, it.next(), it.next(), it.next());

			res.clear();
		}
		
		return r;
	}

	// Parses file which contains all train stations ordered by groups
	// Then performs several queries to CP website and parses each HTML answer
	// Returns CPManager object if database is created successfully
	// Otherwise returns null
	// Example of file:
	// ALL LONG COURSE IN PORTUGAL (Lisboa-Oriente,Lisboa-Santa Apolónia,...\n)
	// ALL URBAN IN LISBON
	// ALL URBAN IN PORTO
	public static CPManager createDB(String stationFile, String saveFile) {
		List<List<String>> station = new LinkedList<List<String>>();
		Map<String, Route> db = new HashMap<String, Route>();

		// Parsing file
		try {
			BufferedReader in = new BufferedReader(new FileReader(stationFile));
			String s;

			while ((s = in.readLine()) != null)
				station.add(Arrays.asList(s.split(",")));

			in.close();
		}
		catch (FileNotFoundException e) { System.err.println("Error: file " + stationFile + " not found!"); }
		catch (IOException e) { System.err.println("Error: can't read from " + stationFile + "!"); }

		// Generating timetables
		for (List<String> group : station)
			// For each element compares with all the others
			for (String first : group)
				for (String second : group) {
					// In case this route was already processed
					if (first.equals(second) || db.containsKey(first + second))
						continue;
					
					db.put(keygen(first, second), createRoute(first, second));
					db.put(keygen(second, first), createRoute(second, first));
				}

		try {
			CPManager man = new CPManager(db, station);
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(saveFile)));

			out.writeObject(man);
			out.flush();
			out.close();

			return man;
		}
		catch (IOException e) { System.err.println("Error: Can't write to " + saveFile + "!"); }

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
	public List<List<String>> query(String depart, String arrival, String type, int day, String time) {
		if (validateStation(depart) && validateStation(arrival) && validateType(type) && validateDay(day) && validateTime(time)) {
			Route r = _db.get(depart + arrival);

			if (r != null)
				return r.getTimetable(day, type, time);
		}
		
		return null;
	}
}
