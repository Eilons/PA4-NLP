package com.Technion.ie.Baseline;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.Technion.ie.ViterbiAlg.ViterbiAlg;


public class ViterbiAlgTest {
	private ViterbiAlg classUnderTest;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		classUnderTest = new ViterbiAlg();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void suff_test() throws Exception {
		String word1 = "BY";
		String word2 = "E";
		String word3 = "CHAIR";
		List<String> trim1 = new ArrayList<String>();
		List<String> trim2 = new ArrayList<String>();
		List<String> trim3 = new ArrayList<String>();
		
		trim1 = Whitebox.<List<String>>invokeMethod(classUnderTest,"suff", word1);
		trim2 = Whitebox.<List<String>>invokeMethod(classUnderTest,"suff", word2);
		trim3 = Whitebox.<List<String>>invokeMethod(classUnderTest,"suff", word3);
		
		Assert.assertEquals("length of trim1 is not correct", (int)2, trim1.size());
		Assert.assertEquals("length of trim1 is not correct", (int)1, trim2.size());
		Assert.assertEquals("length of trim1 is not correct", (int)3, trim3.size());
		
		Assert.assertEquals("Value is not correct", "Y", trim1.get(0));
		Assert.assertEquals("Value is not correct", "BY", trim1.get(1));
		Assert.assertEquals("Value is not correct", "E", trim2.get(0));
		Assert.assertEquals("Value is not correct", "R", trim3.get(0));
		Assert.assertEquals("Value is not correct", "IR", trim3.get(1));
		Assert.assertEquals("Value is not correct", "AIR", trim3.get(2));
		
	}

}
