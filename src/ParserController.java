// Justin Phan 63777127


import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;


public class ParserController 
{
	private static TreeMap<String,Index> mainTree;
	protected static HashMap<String, String> stopWords;
	private FileWriter writer;
	private String file;
	private static final int NUMBEROFTHREADS = 4;
	protected static long systemclock = 0;
	
	public ParserController(String file)
	{
		mainTree = new TreeMap<String,Index>();
		this.file = file;
		initStopWords();
	}
	
	public void runParsers()
	{
		File directory = new File(file);
		File[] fs = directory.listFiles();
		
		Parser parser1 = new Parser("T1");
		Parser parser2 = new Parser("T2");
		Parser parser3 = new Parser("T3");
		Parser parser4 = new Parser("T4");
		
		
		partition(fs, parser1, 0);
		partition(fs, parser2, 1);
		partition(fs, parser3, 2);
		
		int finalPass = NUMBEROFTHREADS - 1;
		int increment = (int)Math.floor(fs.length / NUMBEROFTHREADS);
		for(int i = (finalPass*increment); i < fs.length; i++)
		{
			addDirectoryContent(fs[i], parser4);
		}
		
		long start = System.currentTimeMillis();
		System.out.println("Starting parser");
		parser1.start();
		parser2.start();
		parser3.start();
		parser4.start();
		
		
		addToTree(parser1);
		addToTree(parser2);
		addToTree(parser3);
		addToTree(parser4);
	
		long end = System.currentTimeMillis();	
		
		System.out.println("Size of main tree " + mainTree.size());
		System.out.println("Done parsing files, took : " +  ((end - start)/1000) + " seconds");
		
	}
	
	public static synchronized void addToTree(Parser parser)
	{
		
		for(Entry<String, Index> entry : parser.getSubTree().entrySet())
		{
			if(mainTree.containsKey(entry.getKey()))
			{
				for(Location l : entry.getValue().getLocations())
				{
					mainTree.get(entry.getKey()).addLocation(l);
				}
			}
			else
			{
				mainTree.put(entry.getKey(), entry.getValue());
			}
			
		}
		
		parser.getSubTree().clear();
	}
			
	
	private void partition(File[] fs, Parser parser, int pass)
	{
		int increment = (int)Math.floor(fs.length / NUMBEROFTHREADS);
		
		for(int i = (increment*pass); i < ((pass+1)*increment); i++)
		{
			addDirectoryContent(fs[i], parser);
		}
	}
	
	private void initStopWords()
	{
		Scanner scanner = new Scanner("stopWords.txt");
		stopWords = new HashMap<String, String>();
		
		while(scanner.hasNext())
		{
			String token =  scanner.next();
			
			stopWords.put(token, token);
		}
		
		scanner.close();
	}
	
	
	private void addDirectoryContent(File dir, Parser parser)
	{
		if(dir.isDirectory())
		{	
			File[] files = dir.listFiles();
			for (File file : files) 
			{
				if (file.isDirectory()) 
				{
					addDirectoryContent(file, parser);
				}
				else 
				{
					parser.addFile(file);
				}
			}
		}
		else
		{
			parser.addFile(dir);
		}
	}
	
	
	public void write()
	{
		try 
		{
			writer = new FileWriter("index_plain.txt");
			
			for(Index i : mainTree.values())
			{

				writer.write(i.getToken()+"\t");
				for(Location l : i.getLocations())
				{
					writer.write(l.toString() + "\t");
				}
				writer.write("\n");
			}
			
			writer.close();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
}
