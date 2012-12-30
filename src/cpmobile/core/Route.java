package cpmobile.core;
import java.util.*;
import java.io.Serializable;

public final class Route implements Serializable {

	private static final long serialVersionUID = 7694179989395756214L;
	
	private List<Train> _weekTime; // Timetable for week
	private List<Train> _weekendTime; // Timetable for weekends and holidays

	protected Route() {
		_weekTime = new LinkedList<Train>();
		_weekendTime = new LinkedList<Train>();
	}
	
	protected List<Train> getWeek() {
		return _weekTime;
	}
	
	protected List<Train> getWeekend() {
		return _weekendTime;
	}

	// Days are represented as [0, 7] where 7 means holiday
	protected void insertTrain(int day, String type, String timeDepart, String timeArrival) {
		if (day < 5)
			_weekTime.add(new Train(type, timeDepart, timeArrival));
		else 
			_weekendTime.add(new Train(type, timeDepart, timeArrival));
	}

	// Returns true if t1 if higher or equal to t2
	// false if otherwise
	private boolean compareTime(String t1, String t2) {
		if (t2.equals(""))
			return true;
		
		if (t1.equals(""))
			return false;
		
		String[] time1 = t1.split("h");
		String[] time2 = t2.split("h");

		int hour = Integer.parseInt(time1[0]) - Integer.parseInt(time2[0]);

		return (hour >= 0) ? true : (Integer.parseInt(time1[0]) - Integer.parseInt(time2[0]) >= 0); 
	}

	// Returns true if t1 is equal or included in t2
	// false if otherwise
	private boolean compareType(String t1, String t2) {
		return (t2.equals(Train.TYPE_ALL)) || (t1.equals(t2));
	}

	// Performs a search for a specific type and a specific date of departure
	// Returns a vector - [Train][] where for each train returns Train.getDescription() format
	protected List<List<String>> getTimetable(int day, String specificType, String specificTime) {
		List<List<String>> res = new LinkedList<List<String>>();
		String time, type;

		if (day < 5) {
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

		return res;
	}

	// weekTime.hashcode() * 31 + weekendTime.hashCode()
	public int hashCode() {
		return (_weekTime.hashCode() << 5) - _weekTime.hashCode() + _weekendTime.hashCode();
	}

	public boolean equals(Object other) {
		if ((other != null) && (other instanceof Route)) {
			Route r = (Route) other;

			return 	_weekTime.equals(r.getWeek()) && _weekendTime.equals(r.getWeekend());
		}

		return false;
	}
	
	public String toString() {
		return "NWeek: " + _weekTime.size() + "\nNWeekend: " + _weekendTime.size();
	}
}
