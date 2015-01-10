package com.Technion.ie.dao;

import java.io.IOException;
import java.util.HashMap;

public abstract class Parameter {
	
	protected HashMap<String,Double> mapParameter;
	
	public Parameter ()
	{
		mapParameter = new HashMap<String,Double>();
	}
	
	public abstract void initalizeFromFile (String path) throws IOException;
	
	public void setMapParameter (String key, double value)
	{
		mapParameter.put(key, value);
	}
	
	public HashMap<String,Double> getMapParameter()
	{
		return this.mapParameter;
	}

}
