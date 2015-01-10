package com.Technion.ie.Baseline;

import java.io.IOException;

import com.Technion.ie.dao.V_Vector;

public class BaseLine {
	
	public static final String TAG_MODEL = "c:\\H4p-NLP\\tag.model";
	
	public void part1 () throws IOException
	{
		V_Vector v = new V_Vector();
		v.initalizeFromFile(TAG_MODEL);
		
		
		
		
	}

}
