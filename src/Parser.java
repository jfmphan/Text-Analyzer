// Justin Phan 63777127


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;


public class Parser implements Runnable 
{
	private ArrayList<File> files;
	private String fileName;
	private TreeMap<String, Index> subTree;
	private Scanner scanner;
	private BufferedReader reader;
	private Thread thread;
	private String name;
	private static final int RESETFILESIZE = 250;

	
	
	// Pass an array of files to be parsed. 
	public Parser(String name)
	{
		subTree = new TreeMap<String, Index>();
		files = new ArrayList<File>();
		this.name = name;
	}
	
	/*
	 * For each file remove the header.
	 * Once header is removed then make all words into
	 * indices and add them into a hash map for each file.
	 * Once that file is done being parsed, then all the the indices in 
	 * the file hashmap will be added to a paser's treemap containing all the
	 * indices from all the files it has parsed.    
	 */
	public void parse(File file) throws IOException
	{
		
		TreeMap<String, Location> localMap = new TreeMap<String, Location>();
		
		int curCounter = 1;
		fileName = file.getPath();
		fileName = fileName.replace("maildir\\", "");
		reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(file))));
		
		
		String line;
		while((line = reader.readLine()) != null)
		{
			System.out.println(line);
			scanner = new Scanner(line);
			while(scanner.hasNext())
			{
					String token = scanner.next();
					
				if(token.contains("----"))
				{	
					if(scanner.hasNextLine())
					{
						reader.readLine();
					}
				}
				else	
				{	
				
					if(!token.endsWith(":") && valid(token))
					{
						token = clean(token);
						token = token.toLowerCase();
						
						
						if(!ParserController.stopWords.containsKey(token) && token.length() > 1 && Character.isLetter(token.charAt(0)))
						{
							Stemmer stem = new Stemmer();
							char chars[] = token.toCharArray();
							for(char c : chars)
							{	
								stem.add(c);
							}
							
							stem.stem();
							
							token = stem.toString();
							
							if(localMap.containsKey(token))
							{
								localMap.get(token).addPosition(curCounter);
							}
							else
							{
								localMap.put(token, new Location(fileName, curCounter));
							}
							curCounter++;
						}
						else
						{
							curCounter++;
						}
					}
				}
			}	
			
			scanner.close();
		}	
			
		reader.close();
		
		
		// Adds the word frequencies from this file to the index.
		for(Entry<String, Location> e : localMap.entrySet())
		{
			if(subTree.containsKey(e.getKey()))
			{
				subTree.get(e.getKey()).addLocation(e.getValue());
			}
			else
			{
				subTree.put(e.getKey(), new Index(e.getKey(), e.getValue()));
			}
		}
	
		
	}

	private boolean valid(String token)
	{
		char[] chars = token.toCharArray();
		
		for(int i = 0;  i < chars.length - 1; i++)
		{
			if(!Character.isLetter(chars[i]) && (chars[i] != '@' || chars[i] != '.')) 
			{
				return false;
			}
		}
		
		char last = chars[chars.length-1];
		switch (last)
		{
			case '?': 
					return true;
			case '!':
					return true;
			case '.':
					return true;
			case ',':
					return true;
			case '"':
					return true;
		}
		
		
		if(Character.isLetter(last))
		{
			return true;
		}
		
		return false;
	}
	
	private String clean(String token)
	{
		char[] chars = token.toCharArray();
		String s = "";
		
		for(char c : chars)
		{
			if(Character.isLetter(c) || c == '@' || c =='.')
			{

				s += c;
			}
			
		}
		
		if(s.endsWith("."))
		{
			s = s.substring(0, s.length()-1);
		}
		return s;
	}
	
	public void addFile(File file)
	{
		files.add(file);
	}
	
	public void run() 
	{
		int i = 0;
		int count = 0;
		int reset = 0;
		for(File file : files)
		{	
	
			try
			{
				if(i >= RESETFILESIZE)
				{
			
					ParserController.addToTree(this);
					i = 0;		
					
				}
				
				
				if(reset >= 10000)
				{
					System.out.println(name + " has parsed " + count + " files.");
					reset = 0;
				}
				
				parse(file);
				i++;
				count++;
				reset++;
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		
	}
	
	
	public void start()
	{
		if(thread == null)
		{
			thread = new Thread(this, "nothing");
			thread.start();
			try 
			{
				thread.join();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		
	}
	
	public String name()
	{
		return name;
	}
	
	public TreeMap<String, Index> getSubTree()
	{
		return subTree;
	}

}
