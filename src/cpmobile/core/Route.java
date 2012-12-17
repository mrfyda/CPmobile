package cpmobile.core;
import java.util.LinkedList;
import java.util.ListIterator;
import java.io.Serializable;

public final class Route implements Serializable {

	private String _depart; // Departure station
	private String _arrival; // Arrival station
	private LinkedList<Train> _weekTime; // Timetable for week
	private LinkedList<Train> _weekendTime; // Timetable for weekends and holidays

	protected Route(String depart, String arrival) {
		_depart = depart;
		_arrival = arrival;
		_weekTime = new LinkedList<Train>();
		_weekendTime = new LinkedList<Train>();
	}

	protected String getDepart() {
		return _depart;
	}

	protected String getArrival() {
		return _arrival;
	}

	// Days are represented as [0, 7] where 7 means holiday
	protected void insertTrain(int day, String type, String timeDepart, String timeArrival) {
		if (day < 6)
			_weekTime.add(new Train(type, timeDepart, timeArrival));
		else
			_weekendTime.add(new Train(type, timeDepart, timeArrival));
	}

	// Returns true if t1 if higher or equal to t2
	// false if otherwise
	private boolean compareTime(String t1, String t2) {
		String[] time1 = t1.split(":");
		String[] time2 = t2.split(":");

		int hour = Integer.parseInt(time1[0]) - Integer.parseInt(time2[0]);

		return (hour != 0) ? true : (Integer.parseInt(time1[0]) - Integer.parseInt(time2[0]) >= 0); 
	}

	// Returns true if t1 is equal or included in t2
	// false if otherwise
	private boolean compareType(String t1, String t2) {
		return (t2 == CPManager.TYPE_ALL) || (t1 == t2);
	}

	// Performs a search for a specific type and a specific date of departure
	// Returns a vector - [Train][] where for each train returns Train.getDescription() format
	protected String[][] getTimetable(int day, String specificType, String specificTime) {
		LinkedList<String[]> res = new LinkedList<String[]>();
		String time, type;

		if (day < 6) {
			for (Train t : _weekTime) {
				time = t.getTimeDepart();
				type = t.getType();

				// if Train t applies to the specific constraints
				if (compareTime(time, specificTime) && compareType(type, specificType))
					res.add(t.getDescription());
			}
		} else {
			for (Train t : _weekendTime) {
				time = t.getTimeDepart();
				type = t.getType();

				// if Train t applies to the specific constraints
				if (compareTime(time, specificTime) && compareType(type, specificType))
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
