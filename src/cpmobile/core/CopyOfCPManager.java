package cpmobile.core;
import java.io.*;
import java.util.*;

public final class CopyOfCPManager implements Serializable {

	private static final long serialVersionUID = 924866340298005552L;

	public static final String TIME_ALL = "";
	Map<String, Route> _db;
	List<List<String>> _station;

	private CopyOfCPManager(Map<String, Route> db, List<List<String>> station) {
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

	// Parses file which contains all train stations ordered by groups
	// Then performs several queries to CP website and parses each HTML answer
	// Returns CPManager object if database is created successfully
	// Otherwise returns null
	// Example of file:
	// ALL LONG COURSE IN PORTUGAL (Lisboa-Oriente,Lisboa-Santa Apolónia,...\n)
	// ALL URBAN IN LISBON
	// ALL URBAN IN PORTO
	public static CopyOfCPManager createDB(String stationFile, String saveFile) {
		List<List<String>> station = new LinkedList<List<String>>();
		Map<String, Route> db = new HashMap<String, Route>();

		// Parsing and preparing strings in stationFile
		try {
			BufferedReader in = new BufferedReader(new FileReader(stationFile));
			String line;

			while ((line = in.readLine()) != null) {
				List<String> lst = new ArrayList<String>();
				
				for (String s : Arrays.asList(line.split(",")))
					lst.add(s.toLowerCase());
				
				station.add(lst);
			}

			in.close();
		}
		catch (FileNotFoundException e) { System.err.println("Error: file " + stationFile + " not found!"); }
		catch (IOException e) { System.err.println("Error: can't read from " + stationFile + "!"); }

		// Generating timetables
		for (List<String> group : station) {
			// For each element compares with all the others
			int jLength = group.size();
			int iLength = jLength - 1;
			
			for (int i = 0; i < iLength; ++i) {
				String first = group.get(i);
				
				for (int j = i + 1; j < jLength; ++j) {
					String second = group.get(j);
					
					// In case this route was already processed
					if (first.equals(second) || db.containsKey(first + second))
						continue;
					
					// Manipulate strings to fit HTTP POST
					Station f = new Station(first.replace(' ', '+'));
					Station s = new Station(second.replace(' ', '+'));
					
					// HTTP POST
					Route r1 = RoutesManager.createRoute(f, s);
					Route r2 = RoutesManager.createRoute(s, f);
					
					// Verifying if all went OK
					if ((r1 != null) && (r2 != null)) {
						db.put(keygen(first, second), r1);
						db.put(keygen(second, first), r2);
					} else
						System.err.println("Error: " + first + " -> " + second + " not supported!");
				}
			}
		}
		
		CopyOfCPManager man = new CopyOfCPManager(db, station);

		return saveDB(man, saveFile) ? man : null;
	}

	// Loads database from file 
	// Returns CPManager object if it's successfully loaded
	// otherwise returns null
	public static CopyOfCPManager loadDB(String file) {
		try {
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			CopyOfCPManager manager = (CopyOfCPManager) in.readObject();
			in.close();

			return manager;
		}
		catch (FileNotFoundException e) { System.err.println("Error: file " + file + " not found!"); }
		catch (IOException e) { System.err.println("Error: can't read from " + file + "!"); }
		catch (ClassNotFoundException e) { System.err.println("Error: file " + file + " is corrupt!"); }

		return null;
	}
	
	private static boolean saveDB(CopyOfCPManager man, String file) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

			out.writeObject(man);
			out.flush();
			out.close();

			return true;
		}
		catch (IOException e) { System.err.println("Error: Can't write to " + file + "!"); }
		
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
	public List<List<String>> query(String depart, String arrival, String type, int day, String time) {
		depart = depart.toLowerCase();
		arrival = arrival.toLowerCase();
		
		Train t = new Train(type, "", "");
		
		if (validateStation(depart) && validateStation(arrival) && t.isValid() && validateDay(day) && validateTime(time)) {
			Route r = _db.get(keygen(depart, arrival));

			if (r != null)
				return r.getTimetable(day, type, time);
		}
		
		return null;
	}
}
