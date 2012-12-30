package cpmobile.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import cpmobile.core.web.RoutesRetriever;
import cpmobile.core.web.WebService;

public class RoutesManager {

	private static final String PATTERN = "\\s*<td align=\"center\">(\\*?\\d?\\dh\\d\\d|\\w?\\w)</td>";
	private static final String PATTERN2 = "\\s*<td align=\"center\">(\\w?\\w)<b class=\"orange\">\\|</b>(\\w?\\w)</td>";
	
	public static Route createRoute(Station s1, Station s2) {
		List<String> res = new LinkedList<String>(); // Temporary list to keep timetables of each route
		Route r = new Route();
		WebService w = new RoutesRetriever();
		
		for (int isWeekend = 0; isWeekend < 2; ++isWeekend) {
			String str;
			
			if (isWeekend == 0) 
				str = (String) w.execute("depart=" + s1.getName() + "&arrival=" + s2.getName() + "&date=2012-12-17&timeType=Partida&time=&allServices=allServices&returnDate=&returnTimeType=Partida&returnTime=");
			else
				str = (String) w.execute("depart=" + s1.getName() + "&arrival=" + s2.getName() + "&date=2012-12-15&timeType=Partida&time=&allServices=allServices&returnDate=&returnTimeType=Partida&returnTime=");
			
			// Parsing answer				
			for (String line : str.split("        ")) {
			    if (line.matches(PATTERN2))
			    	res.add(line.replaceFirst(PATTERN2, "$1|$2"));
			    else if (line.matches(PATTERN))
			    	res.add(line.replaceFirst(PATTERN, "$1"));
			}

			// Input is always a multiple of 4 [Type, TimeDepart, TimeArrival, DiffTime]
			if ((res.size() == 0) || (res.size() % 4 != 0))
				return null;

			// For each existing train, insert in route
			Iterator<String> it;
			for (it = res.iterator(); it.hasNext(); it.next())
				r.insertTrain(isWeekend + 4, it.next(), it.next(), it.next());

			res.clear();
		}
		
		return r;
	}
}