package cpmobile.core;

import java.io.Serializable;
import java.util.List;

import cpmobile.core.web.StationsRetriever;


public final class CPManager implements Serializable {

	private static final long serialVersionUID = 1467747784025757764L;

	public void clean() {
		// clean db;	
	}
	
	public void update() {
		clean();
		
		StationsRetriever sr = new StationsRetriever();
		List<Station> stations = sr.execute();
		// insert db
		
		for (Station origin : stations) {
			for (Station destination : stations) {
				Route route = RoutesManager.createRoute(origin, destination);
				// insert db
			}
		}
	}

}
