package cpmobile.core;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public final class Train implements Serializable {
	
	private static final long serialVersionUID = 3629626263929217422L;
	
	private String _type;
	private String _timeDepart;
	private String _timeArrival;

	protected Train(String type, String depart, String arrival) {
		_type = type;
		_timeDepart = depart;
		_timeArrival = arrival;
	}

	protected String getTimeDepart() {
		return _timeDepart;
	}

	protected String getType() {
		return _type;
	}

	protected String getTimeArrival() {
		return _timeArrival;
	}

	// return format:
	// [0] - Type of train
	// [1] - time of Departure [HH:MM]
	// [2] - time of Arrival [HH:MM]
	protected List<String> getDescription() {
		List<String> array = new ArrayList<String>(3);

		array.add(_type);
		array.add(_timeDepart);
		array.add(_timeArrival);

		return array;
	}
	
	// _timeDepart.hashCode() * 31 + _timeArrival.hashCode() * 17 + _type.hashCode()
	public int hashCode() {
		return (_timeDepart.hashCode() << 5) - _timeDepart.hashCode() + (_timeArrival.hashCode() << 4) + _timeArrival.hashCode() + _type.hashCode();
	}
	
	public boolean equals(Object o) {
		if ((o != null) && (o instanceof Train)) {
			Train t = (Train) o;
		
			return _timeDepart.equals(t.getTimeDepart()) && _timeArrival.equals(t.getTimeArrival()) && _type.equals(t.getType());
		}
		
		return false;
	}
}
