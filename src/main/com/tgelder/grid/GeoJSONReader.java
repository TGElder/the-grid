package main.com.tgelder.grid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONArray;


public abstract class GeoJSONReader
{
	
	static JSONObject readJSON(String file)
	{
			try
			{
			    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			    int character;
			    StringBuilder stringBuilder = new StringBuilder();
			    
			    while ((character = bufferedReader.read()) != -1)
			    {
			    	
			    	stringBuilder.append((char) character);
			    				   
			    }
			    bufferedReader.close();
			    
			    String string = stringBuilder.toString();

			    return new JSONObject(string);
			    
			}
			catch (IOException e)
			{
				System.err.println("Could not open file "+file);
				e.printStackTrace();
			}
			catch (JSONException e)
			{
				System.err.println("Could not convert text to JSON Object");
				e.printStackTrace();
			}
			
	
			return null;
	}
	
	public static List<Point> parseJSON(JSONObject json)
	{
		List<Point> out = new ArrayList<Point> ();
		
		Calendar cal = Calendar.getInstance();
       
		try
		{
			JSONArray locations = json.getJSONArray("locations");
			for (int c=0; c<locations.length(); c++)
			{

				if (locations.getJSONObject(c).has("latitudeE7"))
				{
				
					long timestamp = locations.getJSONObject(c).getLong("timestampMs");
					Date date = new Date(timestamp);
					
					
					
					cal.setTime(date);
					
					if (cal.get(Calendar.YEAR)>=2013)
					{
					
				        cal.set(Calendar.HOUR_OF_DAY, 0);
				        cal.set(Calendar.MINUTE, 0);
				        cal.set(Calendar.SECOND, 0);
				        cal.set(Calendar.MILLISECOND, 0);
	
						double lat = locations.getJSONObject(c).getDouble("latitudeE7");
						double lon = locations.getJSONObject(c).getDouble("longitudeE7");
						
						out.add(new Point(lon/10000000,lat/10000000,cal.getTime()));
					
					}
				}
				
				
			}
		}
		catch (JSONException e)
		{
			System.err.println("Cannot parse JSON object.");
			e.printStackTrace();
		}
		
		return out;
	}
	
	public static List<Point> read(String filename)
	{
		return parseJSON(readJSON(filename));
	}
	
	 

}
