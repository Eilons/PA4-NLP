package com.Technion.ie.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.Technion.ie.dao.State;

public class Utils {
	
	public static String RESULT_LINE_FORMAT = "%s %s\n";
	
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
	
	public static List<List<String>> createSentences (List<String> sentencesList)
	{
		List<List<String>> resultList = new ArrayList<List<String>>();
		List<String> newSentence = new ArrayList<String>();
		
		for (String word : sentencesList) 
		{
			if (!word.equals(""))
			{
				newSentence.add(word);
			}
			else 
			{
				if (!newSentence.isEmpty()){
					resultList.add(newSentence);
					newSentence = new ArrayList<String>();//Initialize for new sentence to come
				}
			
			}
		}
		//insert the last segment
		if (!newSentence.isEmpty())
		{
			resultList.add(newSentence);
		}
		return resultList;	
	}
	
	public static void writeViterbiResultsToTxt (List<int[]> resultTagList , List<List<String>> sentenceList, String resultOutPutFile)
	{
		System.out.println("Saving results tags to file...");
		String content ="";
		try {
			BufferedWriter wr = new BufferedWriter (new OutputStreamWriter(new FileOutputStream(resultOutPutFile), "UTF-8"));
			for (int i = 0; i < resultTagList.size(); i++) 
			{
				//resultTagList && sentenceList must have the same length
				int[] resultSegment = resultTagList.get(i);
				List<String> sentenceSegment = sentenceList.get(i);
				for (int j = 0; j < resultSegment.length; j++) 
				{
					content = (String.format(RESULT_LINE_FORMAT, sentenceSegment.get(j),State.getStateFromId(resultSegment[j]).getName()));
	 				wr.write(content); 
	 				wr.flush(); 
				}
				wr.newLine();
				wr.flush();
			}
			wr.close();
		}
 		catch(Exception ex) { 
 			System.out.println("Error while trying to write parameter file: " + ex.toString()); 
 		} 
 
 
 		System.out.println("Saving parameters to file...DONE"); 
 	}

}
