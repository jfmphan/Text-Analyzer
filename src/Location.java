public class Location
{
	private String file;
	private int frequency;
	private String positions="";
	
	public Location(String file, int position)
	{
		this.file = file;
		frequency = 1;
		positions +=position;
	}
	
	public void addPosition(int a)
	{
		positions += "," + a;
		frequency++;
	}
	
	public String getFile()
	{
		return file;
	}
	
	public int getFrequency()
	{
		return frequency;
	}
	
	
	public String toString()
	{
		String s = file;
		
		s += ".:" + frequency + ":" + positions;

		
		return s;
	}
	
	public String getPositions()
	{
		return positions;
	}
}
