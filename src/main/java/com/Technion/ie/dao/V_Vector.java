package com.Technion.ie.dao;

import java.io.IOException;
import java.util.List;

import com.Technion.ie.Utils.Utils;

public class V_Vector extends Parameter {

	public V_Vector()
	{
		super();
	}
	
	@Override
	public void initalizeFromFile(String path) throws IOException {
		List<String> fileSentences;
		fileSentences = Utils.readFile(path);
		for (String sentence : fileSentences) 
		{
			String[] string = Utils.splitToTokens(sentence);
			this.mapParameter.put(string[0], Double.parseDouble(string[1]));
		}
		
		
	}

}
