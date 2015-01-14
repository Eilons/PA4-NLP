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
	public static String FEATURE_SUFF_FORMAT = "SUFF:%s:%s";

	
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
	            		String key = (String.format(FEATURE_TAG_FORMAT,
								string[0],string[1]));
						this.mapParameter.put(key, 1.0);
				}
			}
		}
		//SUFF FEATURE
		Set<String> suff = new HashSet<String>();//keep all Suffix:Tags - acts like cach
		for (String data : trainingData)
		{
			String[] string = Utils.splitToTokens(data);
			int endIndex = string[0].length();
			if (endIndex > 0) {
				String subString = string[0].substring(endIndex-1, endIndex);
				if (!suff.contains(subString)) {
					suff.add(subString+":"+string[1]);
					String key = (String.format(FEATURE_SUFF_FORMAT,
							subString,string[1])); 
					this.mapParameter.put(key, 1.0);
				}
			}
			if (endIndex > 1) {
				String subString = string[0].substring(endIndex-2, endIndex);
				if (!suff.contains(subString)) {
					suff.add(subString+":"+string[1]);
					String key = (String.format(FEATURE_SUFF_FORMAT,
							subString,string[1])); 
					this.mapParameter.put(key, 1.0);
				}
			}
			if (endIndex > 2) {
				String subString = string[0].substring(endIndex-3, endIndex);
				if (!suff.contains(subString)) {
					suff.add(subString+":"+string[1]);
					String key = (String.format(FEATURE_SUFF_FORMAT,
							subString,string[1])); 
					this.mapParameter.put(key, 1.0);
				}
			}
		}
	}
	
	
	

}
