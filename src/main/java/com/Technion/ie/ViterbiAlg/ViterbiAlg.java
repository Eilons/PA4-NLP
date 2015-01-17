package com.Technion.ie.ViterbiAlg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.Technion.ie.Utils.Utils;
import com.Technion.ie.dao.Feature;
import com.Technion.ie.dao.History;
import com.Technion.ie.dao.State;
import com.Technion.ie.dao.V_Vector;

public class ViterbiAlg extends Exception {
	
	private final static Logger logger = Logger.getLogger(ViterbiAlg.class);
	public static String FEATURE_TRIGRAM_FORMAT = "TRIGRAM:%s:%s:%s";
	public static String FEATURE_TAG_FORMAT = "TAG:%s:%s";
	public static String FEATURE_SUFF_FORMAT = "SUFF:%s:%s";
	public static String FEATURE_PRE_FORMAT = "PRE:%s:%s";
	public static int ITERATION = 5;
	
	private List<String> sentence;
	private List<List<String>> sentencesTrainingList;
	private State[] tags;
	private V_Vector vParameter;
	private Feature fParameter;
	
	public ViterbiAlg (List<String> sentence , State[] tags, V_Vector v)
	{
		this.sentence = sentence;
		this.tags = tags;
		this.vParameter = v;
	}
	
	public ViterbiAlg (State[] tags, V_Vector v)
	{
		this.tags = tags;
		this.vParameter = v;
	}
	
	public ViterbiAlg (State[] tags, List<List<String>> sentnecesList, Feature f)
	{
		this.tags = tags;
		this.sentencesTrainingList = sentnecesList;
		this.fParameter = f;
		this.vParameter = new V_Vector();
	}
	
	public ViterbiAlg (State[] tags, List<List<String>> sentnecesList)
	{
		this.tags = tags;
		this.sentencesTrainingList = sentnecesList;
		this.vParameter = new V_Vector();
	}
	
	public int[] viterbi(int part) {
		
		int[] result = new int[sentence.size()];
		int[][][] backpointer = new int[sentence.size()][tags.length][tags.length];
		
        for (int i = 0; i < sentence.size(); i++) {
            for (int j = 0; j < tags.length; j++) {
                for (int k = 0; k < tags.length; k++) {
                    backpointer[i][j][k] = -1;
                }
            }
        }
        
        double[][][] Pi = new double[sentence.size()][tags.length][tags.length];
        
        //for every word
        for (int k = 0; k < sentence.size(); k++) {
            //for each state U
            for (int u = 0; u < tags.length; u++) {
                //for each state V
                for (int v = 0; v < tags.length; v++) {
                	logger.debug("Pi[" + k + "," + State.getStateFromId(u).getName() + "," + State.getStateFromId(v).getName() + "]");
                	Pi[k][u][v] = findW(Pi, backpointer, sentence, k, u, v,part); //call a function which will search for all allowed states at k - 1
                }
            }
        }
        
        System.out.println("=======================");
        System.out.println("Sentence has " + sentence.size() + " words");
        
        double maxProb = 0;
        for (int u = 0; u < tags.length; u++) {
            for (int v = 0; v < tags.length; v++) {
            	
            	History h = new History(State.getStateFromId(u).getName(), State.getStateFromId(v).getName(), sentence.size(), sentence);
            	double currScore  = Pi[sentence.size() - 1][u][v] + localScore (h,"STOP",part);  
            	
            	System.out.println(" U = " + u + " V = " + v);
                System.out.println(" Calculating Pi[" + (sentence.size() - 1) + "," + tags[u].getName() + "," + tags[v].getName() + "] + V * g(<" + tags[u].getName() + "," + tags[v].getName() + "," + "x" + "," + sentence.size() + ">" + "," + "STOP"+ ") = " + currScore);
            	
                if (currScore > maxProb) {
                	if (result.length == 1)
                	{
                		result[result.length - 1] = v;
                	}
                	else {
                    result[result.length - 2] = u;
                    result[result.length - 1] = v;
                	}
                    maxProb = currScore;
                }
            }
        }
        logger.debug("Taken max end sentence probability = " + maxProb);
        
        for (int k = result.length - 3; k >= 0; k--) {
            int Yk1 = result[k + 1];
            int Yk2 = result[k + 2];
            result[k] = backpointer[k + 2][Yk1][Yk2];
        }

        return result;
	}
	
	private double findW(double[][][] Pi, int[][][] bp, List<String> sentence, int k, int u, int v,int part)
	{
		double maxScore = Double.NEGATIVE_INFINITY;
		
		for (short w = 0; w < State.getStateSize(); w++) {
            double prevProbability;
            
            //for the first word we always assume the previous probaility is 0
            if (k == 0) {
                prevProbability = 0.0;
                History h = new History ("*","*",k,sentence);
                maxScore = prevProbability + localScore(h,State.getStateFromId(v).getName(),part);
                logger.debug(" Pi[0, *, *] = 1" );
                logger.debug("History is: " + h.getT2() + "," + h.getT1() + "," + k + "," + sentence);
                logger.debug("feature trigram is: TRIGRAM:"+h.getT2()+":"+h.getT1()+":"+State.getStateFromId(v));
                logger.debug("feature tag is: TAG:"+h.getCurrentWord()+":"+State.getStateFromId(v));
                break;
            }
            else if (k == 1){
            	prevProbability = Pi[k - 1][w][u];
            	History h = new History ("*", State.getStateFromId(u).getName(),k,sentence);
            	maxScore = prevProbability + localScore(h,State.getStateFromId(v).getName(),part);
                logger.debug("History is: " + h.getT2() + "," + h.getT1() + "," + k + "," + sentence);
                logger.debug("feature trigram is: TRIGRAM:"+h.getT2()+":"+h.getT1()+":"+State.getStateFromId(v));
                logger.debug("feature tag is: TAG:"+h.getCurrentWord()+":"+State.getStateFromId(v));
            break;
            }
            prevProbability = Pi[k - 1][w][u];
            History h = new History(State.getStateFromId(w).getName(), State.getStateFromId(u).getName(), k, sentence);
            double currentProb = prevProbability + localScore (h,State.getStateFromId(v).getName(),part);
            
            if (currentProb > maxScore) {
                maxScore = currentProb;
                bp[k][u][v] = w;
            }
            	logger.debug("Taken max probability = " + maxScore + " => BP[" + k + "," + State.getStateFromId(u).getName() + "," + State.getStateFromId(v).getName() + "] = " + bp[k][u][v]);
            
		}
		return maxScore;	
	}
	
    private Double localScore (History h, String v, int part)
    {
    	double score = 0.0;
    	
    	String keyTrigram = String.format(FEATURE_TRIGRAM_FORMAT,
    			h.getT2(),h.getT1(),v);
    	
    	String keyTag = String.format(FEATURE_TAG_FORMAT, h.getCurrentWord(), v);
		
    	if (part == 2 || part == 3)
		{
			List<String> trimWord = suff (h.getCurrentWord());
			for (String trim : trimWord) {
				
				String keySuff = String.format(FEATURE_SUFF_FORMAT, trim, v);
		    	if (this.vParameter.getMapParameter().containsKey(keySuff))
				{
		    		score += this.vParameter.getMapParameter().get(keySuff);
				}
			}
		}
    	if (part == 3)
    	{
			List<String> trimWord = pre (h.getCurrentWord());
			for (String trim : trimWord) {
				
				String keyPre = String.format(FEATURE_PRE_FORMAT, trim, v);
		    	if (this.vParameter.getMapParameter().containsKey(keyPre))
				{
		    		score += this.vParameter.getMapParameter().get(keyPre);
				}
			}
    	}
    	
    	
    	if (this.vParameter.getMapParameter().containsKey(keyTrigram))
		{
    		score += this.vParameter.getMapParameter().get(keyTrigram);
		}
    	if (this.vParameter.getMapParameter().containsKey(keyTag))
		{
    		score += this.vParameter.getMapParameter().get(keyTag);
		}    	
    	return score;
    }
    
	private List<String> suff(String currentWord) {
		
		List<String> trim = new ArrayList<String>();
		int endIndex = currentWord.length();
		if (endIndex > 0) {
			String subString = currentWord.substring(endIndex-1, endIndex);
			trim.add(subString);
			}
		if (endIndex > 1) {
			String subString = currentWord.substring(endIndex-2, endIndex);
			trim.add(subString);
			}
		if (endIndex > 2) {
			String subString = currentWord.substring(endIndex-3, endIndex);
			trim.add(subString);
			}
		
		return trim;
	}
	
	private List<String> pre (String currentWord)
	{
		List<String> trim = new ArrayList<String>();
		int endIndex = currentWord.length();
		if (endIndex > 0) {
			String subString = currentWord.substring(0, 1);
			trim.add(subString);
			}
		if (endIndex > 1) {
			String subString = currentWord.substring(0, 2);
			trim.add(subString);
			}
		if (endIndex > 2) {
			String subString = currentWord.substring(0, 3);
			trim.add(subString);
			}
		
		return trim;
	}
	

	//return vParamter trained using Training samples
    public void perceptron (int part)
    {
    	for (int i = 1; i <= ITERATION; i++) {
			
    	for (List<String> trainSentence : this.sentencesTrainingList)
    	{
    		List<String> wordSentence = new ArrayList<String>();
    		List<String> tagSentnece = new ArrayList<String> ();
    		separateTagWord (wordSentence, tagSentnece, trainSentence);
    		
    		
    		List<String> bestFeatureKeys = new ArrayList<String>();
    		List<String> goldFeatureKeys = new ArrayList<String>();
    		setSentece(wordSentence);
    		logger.debug("iteration number:" + i + "sentnece:" + wordSentence);
    		//part 1
    			int[] F = viterbi(part);
    		
    		//set best tagging
    			List<String> bestTagging = new ArrayList<String>();
    			for (int tag : F) {
				bestTagging.add(State.getStateFromId(tag).getName());
			}
    		//part 2 + 3
    			bestFeatureKeys = getKeyFeatures(getSentnece(), bestTagging, part);
    			goldFeatureKeys = getKeyFeatures(getSentnece(), tagSentnece, part);
    		// part 4
    			updateVScores(bestFeatureKeys, goldFeatureKeys);
    		}
    	}
    	 
    }

	private void updateVScores(List<String> bestFeatureKeys, List<String> goldFeatureKeys) {
		for (String key : goldFeatureKeys) {
			
			if (this.vParameter.getMapParameter().containsKey(key))
			{
				double value = this.vParameter.getMapParameter().get(key);
				value = (bestFeatureKeys.contains(key)) ? value : value + 1.0;
				this.vParameter.setMapParameter(key, value);
			}
			else
			{
				double value = 0.0;
				value = (bestFeatureKeys.contains(key)) ? value : value + 1.0;
				this.vParameter.setMapParameter(key, value);
			}
		}
		for (String key : bestFeatureKeys) {
			
			if (this.vParameter.getMapParameter().containsKey(key))
			{
				double value = this.vParameter.getMapParameter().get(key);
				value = (goldFeatureKeys.contains(key)) ? value : value - 1.0;
				this.vParameter.setMapParameter(key, value);
			}
			else
			{
				double value = 0.0;
				value = (goldFeatureKeys.contains(key)) ? value : value - 1.0;
				this.vParameter.setMapParameter(key, value);
			}
		}
	}
    
    private void separateTagWord (List<String> wordSentence, List<String> tagSentence, List<String> trainSentence)
    {
    	for (String line : trainSentence) {
    		String[] string = Utils.splitToTokens(line);
    		wordSentence.add(string[0]);
    		tagSentence.add(string[1]);
		}
    }
    
    /**
     * 		
     * @param sentnece
     * @param tagList
     * @return all keys realted for features of the current sentnece and tagList
     */
    private List<String> getKeyFeatures (List<String> sentnece, List<String> tagList, int part)
    {
    	//Set<String> keysSet = new HashSet<String>();
    	List<String> keysList = new ArrayList<String>();
    	int sentenceLength = sentnece.size();
    	for (int i = 0; i < sentenceLength; i++) {
    		//keysSet.add(TrigramKeys(i,tagList,sentenceLength));
    		//keysSet.add(TagKeys(sentnece.get(i), tagList.get(i)));
    		//keysSet.addAll(SuffKeys(sentence.get(i), tagList.get(i)));
    		keysList.add(TrigramKeys(i,tagList,sentenceLength));
    		keysList.add(TagKeys(sentnece.get(i), tagList.get(i)));
    		keysList.addAll(SuffKeys(sentnece.get(i), tagList.get(i)));
    		if (part == 3)
    		{
    			keysList.addAll(PreKeys(sentnece.get(i), tagList.get(i)));
    		}
		}
    	//For STOP case - only affect trigram
    	//keysSet.add(TrigramKeys(sentenceLength,tagList,sentenceLength));
    	//keysList.addAll(keysSet);
    	keysList.add(TrigramKeys(sentenceLength,tagList,sentenceLength));
    	return keysList;
    }
    
    private List<String> PreKeys (String word, String tag)
    {
    	List<String> trimList = pre(word);
    	List<String> key = new ArrayList<String>();
    	for (String trim : trimList) {
    		String keySuff = String.format(FEATURE_PRE_FORMAT, trim, tag);
    		key.add(keySuff);
		}
    	
    	return key;
    }
    
    private List<String> SuffKeys (String word, String tag)
    {
    	List<String> trimList = suff(word);
    	List<String> key = new ArrayList<String>();
    	for (String trim : trimList) {
    		String keySuff = String.format(FEATURE_SUFF_FORMAT, trim, tag);
    		key.add(keySuff);
		}
    	
    	return key;
    }
    
    private String TagKeys (String word, String tag)
    {
    	return String.format(FEATURE_TAG_FORMAT, word, tag);
    }
    
    private String TrigramKeys (int position, List<String> tagList, int sentneceLength)
    {
    	String key="";
    	if (position == 0)
    	{
    		key = String.format(FEATURE_TRIGRAM_FORMAT,
        			"*","*",tagList.get(position));
    	}
    	else if (position == sentneceLength && sentneceLength == 1)
    	{
    		key = String.format(FEATURE_TRIGRAM_FORMAT,
    				"*",tagList.get(position-1),"STOP");
    	}
    	else if (position == sentneceLength)
    	{
    		key = String.format(FEATURE_TRIGRAM_FORMAT,
    				tagList.get(position-2),tagList.get(position-1),"STOP");
    	}
    	else if (position == 1)
    	{
    		key = String.format(FEATURE_TRIGRAM_FORMAT,
        			"*",tagList.get(position-1),tagList.get(position));
    	}
    	else {
    		key = String.format(FEATURE_TRIGRAM_FORMAT,
    				tagList.get(position-2),tagList.get(position-1),tagList.get(position));
    	}
    	return key;
    }
    
	
	public List<String> getSentnece ()
	{
		return this.sentence;
	}
	
	public State[] getState ()
	{
		return this.tags;
	}
	
	public HashMap<String,Double> getV()
	{
		return this.vParameter.getMapParameter();
	}
	
	public void setSentece (List<String> sentence)
	{
		this.sentence = sentence;
	}
	
	public void setTags (State[] tags)
	{
		this.tags = tags;
	}
	

}
