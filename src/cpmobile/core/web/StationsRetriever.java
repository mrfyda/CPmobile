package cpmobile.core.web;

import java.util.ArrayList;
import java.util.List;

import cpmobile.core.Station;

public class StationsRetriever extends WebService {
	private static final String STATIONS_URL = "http://cp.pt/cp/getStations.do";

	public List<Station> execute() {
		try {
			List<Station> list = new ArrayList<Station>();
			String r = getFromURL(STATIONS_URL);
			
			for (String s : r.split(";")) {
				Station station = new Station(s);
				list.add(station);
			}
			
			return list; 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}


