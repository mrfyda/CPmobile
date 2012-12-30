package cpmobile.core.web;

public class RoutesRetriever extends WebService {
	private static final String ROUTES_URL = "http://www.cp.pt/cp/searchTimetable.do";

	public String execute(String... strings) {
		StringBuilder url = new StringBuilder(ROUTES_URL + "?");
		
		for (String s : strings)
			url.append(s);
		
		try {
			return getFromURL(url.toString()); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}


