package com.Technion.ie.Baseline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.Technion.ie.Utils.Utils;
import com.Technion.ie.dao.State;
import com.Technion.ie.dao.V_Vector;
import com.Technion.ie.ViterbiAlg.ViterbiAlg;


public class BaseLine {
	
	public static final String TAG_MODEL = "c:\\H4p-NLP\\tag.model";
	public static final String GENE_DEV = "c:\\H4p-NLP\\gene.dev";
	public static final String GENE_DEV_P1_OUT = "/c:/h4p-NLP/gene_dev.p1.out";
	
	public void part1 () throws IOException
	{
		V_Vector v = new V_Vector();
		v.initalizeFromFile(TAG_MODEL);
		State[] tags = {State.I_GENE, State.O };
		
		//read sentences
		List<String> devSentncesList = Utils.readFile(GENE_DEV);
		List<List<String>> sentnecesList = Utils.createSentences(devSentncesList);
		
		//Viterbi
		List<int[]> resultList = new ArrayList<int[]>();
		ViterbiAlg viterbi = new ViterbiAlg (tags, v); 
		for (List<String> sentence : sentnecesList) 
		{
			viterbi.setSentece(sentence);
			int[] res = viterbi.viterbi();
			resultList.add(res);
		}
		
		Utils.writeViterbiResultsToTxt(resultList,sentnecesList,GENE_DEV_P1_OUT);
	
	}

}
