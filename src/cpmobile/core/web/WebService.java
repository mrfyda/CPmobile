package cpmobile.core.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public abstract class WebService {

	protected String getFromURL(String url) throws ClientProtocolException,
			IOException {
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(url);
				HttpResponse response = client.execute(request);
			
				InputStream in = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder str = new StringBuilder();
				String line = null;
				while((line = reader.readLine()) != null) {
				    str.append(line);
				}
				in.close();
				return str.toString();
			}

}