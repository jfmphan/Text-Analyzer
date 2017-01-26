public class Main
{
	public static void main(String args[])
	{	
		if(args[0] == null)
		{
			System.out.println("Args empty.");
		}
		else
		{
			ParserController controller = new ParserController(args[0]);
			controller.runParsers();
			long start = System.currentTimeMillis();
			System.out.println("Starting to write to file.");
			controller.write();
			System.out.println("Write time: " + (System.currentTimeMillis() - start)/1000);
					
		}
	}
}
