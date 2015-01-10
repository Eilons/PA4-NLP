package com.Technion.ie.Utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Utils {
	
	public static List<String> readFile (String path) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF8")); 
		List<String> allLines = Utils.readAllLines(reader);
		
		return allLines;
	}
	
	private static List<String> readAllLines(BufferedReader reader) throws IOException {
		List<String> sentenceList = new ArrayList<String> ();
		String line="";
		while ((line = reader.readLine()) != null)//loop till you dont have any lines left
		{
			sentenceList.add(line);
		}
	
		return sentenceList;
	}
	//split string sentence by spaces
	public static String[] splitToTokens (String line)
	{
			String[] strings = line.split("\\s+");
			return strings;
	}
	
	
}
