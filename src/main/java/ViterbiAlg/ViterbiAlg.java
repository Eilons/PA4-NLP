package ViterbiAlg;

import java.util.List;

import com.Technion.ie.dao.History;
import com.Technion.ie.dao.State;
import com.Technion.ie.dao.V_Vector;

public class ViterbiAlg extends Exception {
	
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
            	double currProb  = Pi[sentence.size() - 1][u][v] + localScore (h,"STOP");  
            	
            	System.out.println(" U = " + u + " V = " + v);
                System.out.println(" Calculating Pi[" + (sentence.size() - 1) + "," + tags[u].getName() + "," + tags[v].getName() + "] + V * g(<" + u + v + "x" + sentence.size() + ">" + "," + "STOP"+ ") = " + currProb);
            	
                if (currProb > maxProb) {
                    result[result.length - 2] = u;
                    result[result.length - 1] = v;
                    maxProb = currProb;
                }
            }
        }
        
        for (int k = result.length - 3; k >= 0; k--) {
            int Yk1 = result[k + 1];
            int Yk2 = result[k + 2];
            result[k] = backpointer[k + 2][Yk1][Yk2];
        }

        return result;
	}
	
	private double findW(double[][][] Pi, int[][][] bp, List<String> sentence, int k, int u, int v)
	{
		double maxProb = 0;
		short argMax = -1;
		
		for (short w = 0; w < State.getStateSize(); w++) {
            double prevProbability;
            
            //for the first word we always assume the previous probaility is 0
            if (k == 0) {
                prevProbability = 0.0;
                History h = new History ("*","*",k,sentence);
                maxProb = prevProbability + localScore(h,State.getStateFromId(v).getName());
                if(maxProb > 0) { argMax = (short)v; }
                break;
            }
            else if (k == 1){
            	prevProbability = Pi[k - 1][w][u];
            	History h = new History ("*", State.getStateFromId(u).getName(),k,sentence);
            	maxProb = prevProbability + localScore(h,State.getStateFromId(v).getName());
            	if(maxProb > 0) { argMax = (short)v; }
            break;
            }
            prevProbability = Pi[k - 1][w][u];
            History h = new History(State.getStateFromId(w).getName(), State.getStateFromId(u).getName(), k, sentence);
            double currentProb = prevProbability + localScore (h,State.getStateFromId(v).getName());
            
            if (currentProb > maxProb) {
                maxProb = currentProb;
                bp[k][u][v] = w;
                argMax = w;
            }
            
            System.out.println("==========================================");
		}
		return maxProb;	
	}
	
    private Double localScore (History h, String v)
    {
    	double score = 0.0;
    	
    	String keyTrigram = String.format(FEATURE_TRIGRAM_FORMAT,
    			h.getT2(),h.getT1(),v);
    	
    	String keyTag = String.format(FEATURE_TAG_FORMAT, h.getCurrentWord(), v);
		
    	if (!this.vParameter.getMapParameter().containsKey(keyTrigram))
		{
			throw new NullPointerException ("v vector parameter doesnt contain" + keyTrigram );
		}
    	else {
    		score += this.vParameter.getMapParameter().get(keyTrigram);
    	}
    	
    	if (!this.vParameter.getMapParameter().containsKey(keyTag))
		{
			throw new NullPointerException ("v vector parameter doesnt contain" + keyTag );
		}
    	else {
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
