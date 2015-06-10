// Justin Phan 63777127
import java.util.ArrayList;



public class Index 
{
	private String token;
	private ArrayList<Location> locations;
	
	public Index(String token, Location location)
	{
		this.token = token;
		locations = new ArrayList<Location>();
		locations.add(location);
	}
	
	public void addLocation(Location location)
	{
		locations.add(location);
	}
	
	public String getToken()
	{
		return token;
	}
	
	public ArrayList<Location> getLocations()
	{
		return locations;
	}
	
}
