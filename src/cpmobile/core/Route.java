package cpmobile.core;
import java.util.LinkedList;
import java.util.ListIterator;
import java.io.Serializable;

public final class Route implements Serializable {
	public static final String TYPE_ALL = "";
	public static final String TYPE_ALFA = "AL";
	public static final String TYPE_IC = "IC";
	public static final String TYPE_R = "R";
	public static final String DATE_ALL = "";

	private String _depart; // Departure station
	private String _arrival; // Arrival station
	private LinkedList<Train> _weekDate; // Timetable for week
	private LinkedList<Train> _weekendDate; // Timetable for weekends and holidays

	public Route(String depart, String arrival) {
		_depart = depart;
		_arrival = arrival;
		_weekDate = new LinkedList<Train>();
		_weekendDate = new LinkedList<Train>();
	}

	public String getDepart() {
		return _depart;
	}

	public String getArrival() {
		return _arrival;
	}

	// Days are represented as [0, 7] where 7 means holiday
	protected void insertTrain(int day, String type, String dateDepart, String dateArrival) {
		if (day < 6)
			_weekDate.add(new Train(type, dateDepart, dateArrival));
		else
			_weekendDate.add(new Train(type, dateDepart, dateArrival));
	}

	public static final class IllegalDateFormatException extends Exception {
		public IllegalDateFormatException() {
			super("Date must be in HH:MM format");
		}
	}

	public static final class IllegalTypeException extends Exception {
		public IllegalTypeException() {
			super("Type of train must be : [" + TYPE_ALL + ", " + TYPE_ALFA + ", " + TYPE_IC + ", " + TYPE_R + "]");
		}
	}

	// Returns true if d1 if higher or equal to d2
	// false if otherwise
	public static boolean compareDate(String d1, String d2) throws IllegalDateFormatException {
		String[] date1 = d1.split(":");
		String[] date2 = d2.split(":");

		if (d1 == DATE_ALL)
			return true;

		if (d2 == DATE_ALL)
			return false;

		if (! (date1.length == date2.length && date1.length == 2))
			throw new IllegalDateFormatException();

		int hour = Integer.parseInt(date1[0]) - Integer.parseInt(date2[0]);

		return (hour != 0) ? true : (Integer.parseInt(date1[0]) - Integer.parseInt(date2[0]) >= 0); 
	}

	// Returns true if t1 is equal or included in t2
	// false if otherwise
	public static boolean compareType(String t1, String t2) throws IllegalTypeException {
		return (t2 == TYPE_ALL) || (t1 == t2);
	}

	// Performs a search for a specific type and a specific date of departure
	// Returns a vector - [Train][] where for each train returns Train.getDescription() format
	public String[][] getTimetable(int day, String specificType, String specificDate) throws IllegalDateFormatException, IllegalTypeException {
		LinkedList<String[]> res = new LinkedList<String[]>();
		String date, type;

		if (day < 6) {
			for (Train t : _weekDate) {
				date = t.getDateDepart();
				type = t.getType();

				// if Train t applies to the specific constraints
				if (compareDate(date, specificDate) && compareType(type, specificType))
					res.add(t.getDescription());
			}
		} else {
			for (Train t : _weekendDate) {
				date = t.getDateDepart();
				type = t.getType();

				// if Train t applies to the specific constraints
				if (compareDate(date, specificDate) && compareType(type, specificType))
					res.add(t.getDescription());
			}
		}

		return (String[][]) res.toArray();
	}

	public int hashCode() {
		return _depart.hashCode() * 11 + _arrival.hashCode();
	}

	public boolean equals(Object other) {
		if ((other != null) && (other instanceof Route)) {
			Route r = (Route) other;

			return 	_depart == r.getDepart() && _arrival == r.getArrival();
		}

		return false;
	}
}
