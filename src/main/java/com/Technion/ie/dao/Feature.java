package com.Technion.ie.dao;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.Technion.ie.Utils.Utils;
//Not sure still if needed!!!!
public class Feature extends Parameter {
	
	public static String FEATURE_TRIGRAM_FORMAT = "TRIGRAM:%s:%s:%s";
	public static String FEATURE_TAG_FORMAT = "TAG:%s:%s";

	
	public Feature()
	{
		super();
	}

	@Override
	public void initalizeFromFile(String path) throws IOException {
		
		State[] tags = {State.I_GENE, State.O };
		//TRIGRAM FEATURE
        //for each state U
        for (int u = 0; u < tags.length; u++) {
            //for each state V
            for (int v = 0; v < tags.length; v++) {
            	//for each state S
            	for (int s = 0; s < tags.length; s++) {
            	
            		String key = (String.format(FEATURE_TRIGRAM_FORMAT,
            										State.getStateFromId(u),State.getStateFromId(v),State.getStateFromId(s)));
            		this.mapParameter.put(key, 1.0);
            		
            	}
            }
        }
        //TAG FEATURE
        Set<String> words = new HashSet<String> ();//keep all seeing words:Tags - acts like cache
        //read from word files...
		List<String> trainingData;
		trainingData = Utils.readFile(path);
		for (String data : trainingData) 
		{
			String[] string = Utils.splitToTokens(data);
			if (string.length == 2)
			{
				String wordTagPair = string[0]+":"+string[1];
				if (!words.contains(wordTagPair)) {
						words.add(wordTagPair);
						this.mapParameter.put(wordTagPair, 1.0);
				}
			}
		}
	}
	

}
