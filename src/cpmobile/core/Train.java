package cpmobile.core;
import java.io.Serializable;

public final class Train implements Serializable {
	private String _type;
	private String _dateDepart;
	private String _dateArrival;

	protected Train(String type, String depart, String arrival) {
		_type = type;
		_dateDepart = depart;
		_dateArrival = arrival;
	}

	protected String getDateDepart() {
		return _dateDepart;
	}

	protected String getType() {
		return _type;
	}

	protected String getDateArrival() {
		return _dateArrival;
	}

	// return format:
	// [0] - Type of train
	// [1] - Date of Departure [HH:MM]
	// [2] - Date of Arrival [HH:MM]
	protected String[] getDescription() {
		String[] res = new String[3];

		res[0] = _type;
		res[1] = _dateDepart;
		res[2] = _dateArrival;

		return res;
	}
}