package cpmobile.core.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public abstract class WebService {

	protected String getFromURL(String target) throws IOException {
		URL url = new URL(target);
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);

		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder str = new StringBuilder();
		String line = null;
			
		while ((line = reader.readLine()) != null) {				
		    str.append(line);
		}
		
		return str.toString();
	}
	
	public abstract Object execute();

}