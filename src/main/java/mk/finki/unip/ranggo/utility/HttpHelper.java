package mk.finki.unip.ranggo.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpHelper {
	
	public static String get(String url){
		try {
			URL urlObj = new URL(url);
			
			HttpURLConnection connection = (HttpURLConnection)urlObj.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");

			int responseCode = connection.getResponseCode();
			if(responseCode == 200){
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				StringBuffer responseBuffer = new StringBuffer();
		
				while((line = reader.readLine()) != null){
					responseBuffer.append(line);
				}
				reader.close();
		
				return responseBuffer.toString();
			}else{
				return null;
			}
		}
		catch(MalformedURLException e){}
		catch(IOException e){}
		
		return null;
	}
}