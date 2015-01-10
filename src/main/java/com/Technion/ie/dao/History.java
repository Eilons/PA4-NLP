package com.Technion.ie.dao;

import java.util.List;

public class History {
	
	private String t2;//t-2
	private String t1;//t-1
	private int i;//position
	private List<String> x;//sentence
	
	public History(String t2, String t1, int i, List<String> x)
	{
		this.t2 = t2;
		this.t1 = t1;
		this.i = i;
		this.x = x;
	}

	public String getT2 ()
	{
		return this.t2;
	}
	
	public String getT1 ()
	{
		return this.t1;
	}
	
	public int getI ()
	{
		return this.i;
	}
	
	public List<String> getX()
	{
		return this.x;
	}
	
	public String getCurrentWord ()
	{
		if (i == x.size()) return "";//for the case "STOP"
		return this.x.get(this.i);
	}
	
}
