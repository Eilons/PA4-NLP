package com.Technion.ie.ViterbiAlg;

import java.util.List;

import org.apache.log4j.Logger;

import com.Technion.ie.dao.History;
import com.Technion.ie.dao.State;
import com.Technion.ie.dao.V_Vector;

public class ViterbiAlg extends Exception {
	
	private final static Logger logger = Logger.getLogger(ViterbiAlg.class);
	public static String FEATURE_TRIGRAM_FORMAT = "TRIGRAM:%s:%s:%s";
	public static String FEATURE_TAG_FORMAT = "TAG:%s:%s";
	
	private List<String> sentence;
	private State[] tags;
	private V_Vector vParameter;
	
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
	
	public int[] viterbi() {
		
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
                	Pi[k][u][v] = findW(Pi, backpointer, sentence, k, u, v); //call a function which will search for all allowed states at k - 1
                }
            }
        }
        
        System.out.println("=======================");
        System.out.println("Sentence has " + sentence.size() + " words");
        
        double maxProb = 0;
        for (int u = 0; u < tags.length; u++) {
            for (int v = 0; v < tags.length; v++) {
            	
            	History h = new History(State.getStateFromId(u).getName(), State.getStateFromId(v).getName(), sentence.size(), sentence);
            	double currScore  = Pi[sentence.size() - 1][u][v] + localScore (h,"STOP");  
            	
            	System.out.println(" U = " + u + " V = " + v);
                System.out.println(" Calculating Pi[" + (sentence.size() - 1) + "," + tags[u].getName() + "," + tags[v].getName() + "] + V * g(<" + tags[u].getName() + "," + tags[v].getName() + "," + "x" + "," + sentence.size() + ">" + "," + "STOP"+ ") = " + currScore);
            	
                if (currScore > maxProb) {
                    result[result.length - 2] = u;
                    result[result.length - 1] = v;
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
	
	private double findW(double[][][] Pi, int[][][] bp, List<String> sentence, int k, int u, int v)
	{
		double maxScore = Double.NEGATIVE_INFINITY;
		
		for (short w = 0; w < State.getStateSize(); w++) {
            double prevProbability;
            
            //for the first word we always assume the previous probaility is 0
            if (k == 0) {
                prevProbability = 0.0;
                History h = new History ("*","*",k,sentence);
                maxScore = prevProbability + localScore(h,State.getStateFromId(v).getName());
                logger.debug(" Pi[0, *, *] = 1" );
                logger.debug("History is: " + h.getT2() + "," + h.getT1() + "," + k + "," + sentence);
                logger.debug("feature trigram is: TRIGRAM:"+h.getT2()+":"+h.getT1()+":"+State.getStateFromId(v));
                logger.debug("feature tag is: TAG:"+h.getCurrentWord()+":"+State.getStateFromId(v));
                break;
            }
            else if (k == 1){
            	prevProbability = Pi[k - 1][w][u];
            	History h = new History ("*", State.getStateFromId(u).getName(),k,sentence);
            	maxScore = prevProbability + localScore(h,State.getStateFromId(v).getName());
                logger.debug("History is: " + h.getT2() + "," + h.getT1() + "," + k + "," + sentence);
                logger.debug("feature trigram is: TRIGRAM:"+h.getT2()+":"+h.getT1()+":"+State.getStateFromId(v));
                logger.debug("feature tag is: TAG:"+h.getCurrentWord()+":"+State.getStateFromId(v));
            break;
            }
            prevProbability = Pi[k - 1][w][u];
            History h = new History(State.getStateFromId(w).getName(), State.getStateFromId(u).getName(), k, sentence);
            double currentProb = prevProbability + localScore (h,State.getStateFromId(v).getName());
            
            if (currentProb > maxScore) {
                maxScore = currentProb;
                bp[k][u][v] = w;
            }
            	logger.debug("Taken max probability = " + maxScore + " => BP[" + k + "," + State.getStateFromId(u).getName() + "," + State.getStateFromId(v).getName() + "] = " + bp[k][u][v]);
            
		}
		return maxScore;	
	}
	
    private Double localScore (History h, String v)
    {
    	double score = 0.0;
    	
    	String keyTrigram = String.format(FEATURE_TRIGRAM_FORMAT,
    			h.getT2(),h.getT1(),v);
    	
    	String keyTag = String.format(FEATURE_TAG_FORMAT, h.getCurrentWord(), v);
		
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
	

	
	public List<String> getSentnece ()
	{
		return this.sentence;
	}
	
	public State[] getState ()
	{
		return this.tags;
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
