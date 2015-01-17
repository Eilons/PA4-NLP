package com.Technion.ie.Baseline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.Technion.ie.Utils.Utils;
import com.Technion.ie.dao.Feature;
import com.Technion.ie.dao.State;
import com.Technion.ie.dao.V_Vector;
import com.Technion.ie.ViterbiAlg.ViterbiAlg;


public class BaseLine {
	
	public static final String TAG_MODEL = "c:\\H4p-NLP\\tag.model";
	public static final String GENE_TRAIN = "c:\\H4p-NLP\\gene.train";
	public static final String GENE_DEV = "c:\\H4p-NLP\\gene.dev";
	public static final String GENE_DEV_P1_OUT = "/c:/h4p-NLP/gene_dev.p1.out";
	public static final String SUFFIX_TAGGER_OUT = "/c:/h4p-NLP/suffix_tagger.model";
	public static final String GENE_DEV_P2_OUT = "/c:/h4p-NLP/gene_dev.p2.out";
	public static final String GENE_DEV_P3_OUT = "/c:/h4p-NLP/gene_dev.p3.out";
	public static final int PART = 3;
	
	public void part1 () throws IOException
	{
		V_Vector v = new V_Vector();
		v.initalizeFromFile(TAG_MODEL);
		State[] tags = {State.O, State.I_GENE };
		
		//read sentences
		List<String> devSentncesList = Utils.readFile(GENE_DEV);
		List<List<String>> sentnecesList = Utils.createSentences(devSentncesList);
		
		//Viterbi
		List<int[]> resultList = new ArrayList<int[]>();
		ViterbiAlg viterbi = new ViterbiAlg (tags, v); 
		for (List<String> sentence : sentnecesList) 
		{
			viterbi.setSentece(sentence);
			int[] res = viterbi.viterbi(PART);
			resultList.add(res);
		}
		
		Utils.writeViterbiResultsToTxt(resultList,sentnecesList,GENE_DEV_P1_OUT);
	
	}
	
	public void perceptron () throws IOException
	{
		State[] tags = {State.O, State.I_GENE };
		//Feature f = new Feature ();
		//f.initalizeFromFile(GENE_TRAIN);
		List<String> trainingData = Utils.readFile(GENE_TRAIN);
		List<List<String>> sentnecesList = Utils.createSentences(trainingData);
		ViterbiAlg viterbi = new ViterbiAlg (tags, sentnecesList );
		viterbi.perceptron(PART);
		Utils.writeVectorParameterToTxt(SUFFIX_TAGGER_OUT, viterbi.getV());
		
	}
	
	public void part2() throws IOException
	{
		V_Vector v = new V_Vector();
		v.initalizeFromFile(SUFFIX_TAGGER_OUT);
		State[] tags = {State.O, State.I_GENE };
		
		//read sentences
		List<String> devSentncesList = Utils.readFile(GENE_DEV);
		List<List<String>> sentnecesList = Utils.createSentences(devSentncesList);
		
		//Viterbi
		List<int[]> resultList = new ArrayList<int[]>();
		ViterbiAlg viterbi = new ViterbiAlg (tags, v); 
		for (List<String> sentence : sentnecesList) 
		{
			viterbi.setSentece(sentence);
			int[] res = viterbi.viterbi(PART);
			resultList.add(res);
		}
		
		Utils.writeViterbiResultsToTxt(resultList,sentnecesList,GENE_DEV_P2_OUT);
	
	}
	
	public void part3() throws IOException
	{
		V_Vector v = new V_Vector();
		v.initalizeFromFile(SUFFIX_TAGGER_OUT);
		State[] tags = {State.O, State.I_GENE };
		
		//read sentences
		List<String> devSentncesList = Utils.readFile(GENE_DEV);
		List<List<String>> sentnecesList = Utils.createSentences(devSentncesList);
		
		//Viterbi
		List<int[]> resultList = new ArrayList<int[]>();
		ViterbiAlg viterbi = new ViterbiAlg (tags, v); 
		for (List<String> sentence : sentnecesList) 
		{
			viterbi.setSentece(sentence);
			int[] res = viterbi.viterbi(PART);
			resultList.add(res);
		}
		
		Utils.writeViterbiResultsToTxt(resultList,sentnecesList,GENE_DEV_P3_OUT);
	
	}

}
