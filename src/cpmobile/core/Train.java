package cpmobile.core;
import java.io.Serializable;

public final class Train implements Serializable {
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
	protected String[] getDescription() {
		String[] res = new String[3];

		res[0] = _type;
		res[1] = _timeDepart;
		res[2] = _timeArrival;

		return res;
	}
}
